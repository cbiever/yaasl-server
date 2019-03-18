package yaasl.server.export

import org.apache.fop.apps.FopFactoryBuilder
import org.apache.xmlgraphics.util.MimeConstants.MIME_PDF
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yaasl.server.convert.Converter.parseDate
import yaasl.server.model.Flight
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.net.URI
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.namespace.QName
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource

@Component
class PDFExporter {

    private val LOG = LoggerFactory.getLogger(javaClass)

    fun generate(flights: List<Flight>, location: Optional<String>, date: Optional<String>, translations: Optional<String>): ByteArray? {
        val xml = xml(flights, if (location.isPresent) location.get().toUpperCase() else null, if (date.isPresent) parseDate(date.get()) else null)
        val factory = FopFactoryBuilder(URI.create(".")).build()
        ByteArrayOutputStream().use { outputStream ->
            val fop = factory.newFop(MIME_PDF, outputStream)
            val xslt = StreamSource(javaClass.getResourceAsStream("/flights.xsl"))
            val transformer = TransformerFactory.newInstance().newTransformer(xslt)
            transformer.setParameter("translations", if (translations.isPresent) translations.get() else null)
            val result = SAXResult(fop.defaultHandler)
            val source = StreamSource(StringReader(xml))
            transformer.transform(source, result)
            return outputStream.toByteArray()
        }
    }

    private fun xml(flights: List<Flight>, location: String?, date: Date?): String {
        val context = JAXBContext.newInstance(Data::class.java)
        val marshaller = context.createMarshaller()
        val writer = StringWriter()
        marshaller.marshal(JAXBElement(QName("data"), Data::class.java, Data(flights, location, date)), writer)
        return writer.toString()
    }

}
