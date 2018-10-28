package yaasl.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import yaasl.server.model.Update
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

@Component
class Broadcaster : TextWebSocketHandler() {

    private val LOG = LoggerFactory.getLogger(javaClass)
    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val objectMapper = ObjectMapper()

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession?) {
        LOG.info("session id: {} established", session!!.id)
        val update = Update("set session id", session.id)
        val json = objectMapper.writeValueAsString(update)
        session.sendMessage(TextMessage(json))
        sessions.add(session)
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        LOG.info("session id: {} closed", session!!.id)
        sessions.remove(session)
    }

    @JvmOverloads
    fun sendUpdate(update: Update, originatorID: String? = "-1") {
        try {
            val json = objectMapper.writeValueAsString(update)
            sessions.forEach { session ->
                try {
                    if (session.id != originatorID) {
                        session.sendMessage(TextMessage(json))
                    }
                } catch (e: IOException) {
                    LOG.error("Could not send message to {}", session.remoteAddress, e)
                }
            }
        } catch (e: Exception) {
            LOG.error("Unable to send update {}", update, e)
        }

    }

}
