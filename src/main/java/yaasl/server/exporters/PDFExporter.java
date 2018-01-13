package yaasl.server.exporters;

import org.apache.commons.io.IOUtils;
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
import java.util.List;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;

@Component
public class PDFExporter {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    public byte[] generate(List<Flight> flights) throws Exception {
        String xml = xml(flights);
        FopFactory factory = new FopFactoryBuilder(URI.create(".")).build();
        byte[] pdf = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Fop fop = factory.newFop(MIME_PDF, outputStream);
            Source xslt = new StreamSource(getClass().getResourceAsStream("/flights.xsl"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);
            Result result = new SAXResult(fop.getDefaultHandler());
            Source source = new StreamSource(new StringReader(xml));
            transformer.transform(source, result);
            pdf = outputStream.toByteArray();
FileOutputStream file = new FileOutputStream("/home/casper/flights.xml");
file.write(xml.getBytes());
file.close();
file = new FileOutputStream("/home/casper/flights.pdf");
file.write(pdf);
file.close();
        }
        return pdf;
    }

    private String xml(List<Flight> flights) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Flight[].class);
        Marshaller marshaller = context.createMarshaller();
        JAXBElement<Flight[]> root = new JAXBElement<Flight[]>(new QName("flights"), Flight[].class, flights.toArray(new Flight[flights.size()]));
        StringWriter writer = new StringWriter();
        marshaller.marshal(root, writer);
        return writer.toString();
    }

}
