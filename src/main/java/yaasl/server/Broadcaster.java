package yaasl.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage
        ;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import yaasl.server.model.Update;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class Broadcaster extends TextWebSocketHandler {

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.info("session id: {} established", session.getId());
        Update update = new Update("set session id", session.getId());
        String json = objectMapper.writeValueAsString(update);
        session.sendMessage(new TextMessage(json));
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOG.info("session id: {} closed", session.getId());
        sessions.remove(session);
    }

    public void sendUpdate(Update update) {
        sendUpdate(update, "-1");
    }

    public void sendUpdate(Update update, String originatorID) {
        try {
            String json = objectMapper.writeValueAsString(update);
            sessions
                    .forEach(session -> {
                        try {
                            if (!session.getId().equals(originatorID)) {
                                session.sendMessage(new TextMessage(json));
                            }
                        } catch (IOException e) {
                            LOG.error("Could not send message to {}", session.getRemoteAddress(), e);
                        }
                    });
        } catch (Exception e) {
            LOG.error("Unable to send update {}", update, e);
        }
    }

}
