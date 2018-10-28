package yaasl.server.export

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import javax.xml.bind.DatatypeConverter.parseBase64Binary
import javax.xml.bind.DatatypeConverter.parseDateTime

class XsltHelper {

    private val LOG = LoggerFactory.getLogger(javaClass)

    fun formatDate(date: String, format: String): String {
        val calendar = parseDateTime(date)
        val formatter = SimpleDateFormat(format)
        return formatter.format(calendar.time)
    }

    fun translate(key: String, translationsMap: String): String {
        var translation = key
        try {
            val translations = ObjectMapper().readValue(parseBase64Binary(translationsMap), Map::class.java)
            if (translations.containsKey(key)) {
                translation = translations.get(key) as String
            }
        } catch (e: Exception) {
            LOG.error("unable to translate {}", key, e)
        }
        return translation
    }

}
