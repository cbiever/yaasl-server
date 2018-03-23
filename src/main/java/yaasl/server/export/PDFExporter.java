package yaasl.server.export;

import org.apache.fop.apps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import yaasl.server.model.Flight;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static yaasl.server.convert.Converter.parseDate;

@Component
public class PDFExporter {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    public byte[] generate(List<Flight> flights, Optional<String> location, Optional<String> date, Optional<String> translations) throws Exception {
        String xml = xml(flights, location.isPresent() ? location.get().toUpperCase() : null, date.isPresent() ? parseDate(date.get()) : null);
        FopFactory factory = new FopFactoryBuilder(URI.create(".")).build();
        byte[] pdf;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Fop fop = factory.newFop(MIME_PDF, outputStream);
            Source xslt = new StreamSource(getClass().getResourceAsStream("/flights.xsl"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);
            transformer.setParameter("translations", translations.isPresent() ? translations.get() : null);
            Result result = new SAXResult(fop.getDefaultHandler());
            Source source = new StreamSource(new StringReader(xml));
            transformer.transform(source, result);
            pdf = outputStream.toByteArray();
        }
        return pdf;
    }

    private String xml(List<Flight> flights, String location, Date date) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Data.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(new JAXBElement<Data>(new QName("data"), Data.class, new Data(flights, location, date)), writer);
        return writer.toString();
    }

}
