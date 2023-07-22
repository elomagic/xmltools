package de.elomagic.xmltools;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Xml2KeyValueConverter {

    private String keyDelimiter = ".";

    private boolean attributeSupport = true;
    private String attributeDelimiter = "#";

    @NotNull
    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    public void setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
    }

    public boolean isAttributeSupport() {
        return attributeSupport;
    }

    public void setAttributeSupport(boolean attributeSupport) {
        this.attributeSupport = attributeSupport;
    }

    @NotNull
    public String getAttributeDelimiter() {
        return attributeDelimiter;
    }

    public void setAttributeDelimiter(@NotNull String attributeDelimiter) {
        this.attributeDelimiter = attributeDelimiter;
    }

    @NotNull
    public Map<String, String> read(@NotNull Path file ) throws ParserConfigurationException, IOException, SAXException {

        return read(Files.newInputStream(file));

    }

    @NotNull
    public Map<String, String> read(@NotNull InputStream in) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        //doc.getDocumentElement().normalize();

        return parseNodeChilds(doc.getDocumentElement().getNodeName(), doc.getDocumentElement());
    }

    private Map<String, String> parseNodeChilds(@NotNull String chainKey, @NotNull Element element) {

        Map<String, String> result = new HashMap<>();

        if (attributeSupport) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Attr attr = (Attr) element.getAttributes().item(i);
                result.put(String.join(attributeDelimiter, chainKey, attr.getName()), attr.getValue());
            }
        }

        boolean skipTextNode = false;
        String textContent = "";

        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            Node child = element.getChildNodes().item(i);

            short type = child.getNodeType();
            String name = child.getNodeName();

            if (type == Node.TEXT_NODE) {
                Text text = (Text)child;
                textContent = text.getTextContent();
            } else if (type == Node.ELEMENT_NODE) {
                Element e = (Element)child;
                result.putAll(parseNodeChilds(String.join(keyDelimiter, chainKey, name), e));
                skipTextNode = true;
            }
        }

        if (!skipTextNode) {
            result.put(chainKey, textContent);
        }

        return result;
    }

}
