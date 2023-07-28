package de.elomagic.xmltools;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyValue2XmlConverterTest {

    @Test
    void testConvert() throws ParserConfigurationException, IOException, SAXException {

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/sample01.properties"));

        Map<String, String> map = new HashMap<>();
        properties.forEach((key, value) -> map.put(key.toString(), Objects.toString(value, "")));

        KeyValue2XmlConverter converter = new KeyValue2XmlConverter();
        Document document = converter.convert(map).orElseThrow();

        toString(document);

        assertEquals("root", document.getDocumentElement().getNodeName());
    }

    public static String toString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            System.out.println("XML=" + sw);
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
}