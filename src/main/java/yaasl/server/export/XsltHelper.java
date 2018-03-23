package yaasl.server.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.parseDateTime;

public class XsltHelper {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    public String formatDate(String date, String format) {
        Calendar calendar = parseDateTime(date);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(calendar.getTime());
    }

    public String translate(String key, String translationsMap) {
        String translation = key;
        if (translationsMap != null) {
            try {
                Map<String, String> translations = new ObjectMapper().readValue(parseBase64Binary(translationsMap), Map.class);
                translation = translations.get(key);
            }
            catch (Exception e) {
                LOG.error("unable to translate {}", key, e);
            }
        }
        return translation;
    }

}
