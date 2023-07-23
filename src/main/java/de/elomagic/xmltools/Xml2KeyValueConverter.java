package de.elomagic.xmltools;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

    /**
     * Returns delimiters string
     * <p>
     * Default "." (Dot)
     *
     * @return Returns a string.
     */
    @NotNull
    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    /**
     * Set key word delimiter.
     * <p>
     * Default "." (Dot)
     *
     * @param keyDelimiter A string
     */
    public void setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
    }

    /**
     * Returns support of converting XML attributes.
     * <p>
     * Default true
     *
     * @return When true, XML attributes will also be converted otherwise not
     */
    public boolean isAttributeSupport() {
        return attributeSupport;
    }

    /**
     * Set converting support of XML attributes
     * <p>
     * Default true;
     *
     * @param attributeSupport When true, XML attributes will also be converted otherwise not
     */
    public void setAttributeSupport(boolean attributeSupport) {
        this.attributeSupport = attributeSupport;
    }


    /**
     * Returns delimiters for attributes.
     * <p>
     * Default "#" (Hashtag)
     *
     * @return Returns a string.
     */
    @NotNull
    public String getAttributeDelimiter() {
        return attributeDelimiter;
    }

    /**
     * Set delimiter for the attribute name.
     * <p>
     * Default "#" (Hashtag)
     *
     * @param attributeDelimiter A string
     */
    public void setAttributeDelimiter(@NotNull String attributeDelimiter) {
        this.attributeDelimiter = attributeDelimiter;
    }

    /**
     * Reads an XML document from a file and converts it into a key value {@link Map}.
     *
     * @param file File to read
     * @return Returns a map but never null
     * @throws ParserConfigurationException Thrown when unable to parse the XML document
     * @throws IOException Thrown when unable to read XML document from the input stream
     * @throws SAXException Thrown when unable to parse the XML document
     */
    @NotNull
    public Map<String, String> read(@NotNull Path file) throws ParserConfigurationException, IOException, SAXException {

        return read(Files.newInputStream(file));

    }

    /**
     * Reads an XML document from an {@link InputStream} and converts it into a key value {@link Map}.
     *
     * @param in Input stream where to read the XML document
     * @return Returns a map but never null
     * @throws ParserConfigurationException Thrown when unable to parse the XML document
     * @throws IOException Thrown when unable to read XML document from the input stream
     * @throws SAXException Thrown when unable to parse the XML document
     */
    @NotNull
    public Map<String, String> read(@NotNull InputStream in) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        //doc.getDocumentElement().normalize();

        return parseElementChilds(doc.getDocumentElement().getNodeName(), doc.getDocumentElement());
    }

    @NotNull
    private Map<String, String> parseElementChilds(@NotNull String chainKey, @NotNull Element element) {

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
                textContent = child.getTextContent();
            } else if (type == Node.ELEMENT_NODE) {
                result.putAll(parseElementChilds(String.join(keyDelimiter, chainKey, name), (Element)child));
                skipTextNode = true;
            }
        }

        if (!skipTextNode) {
            result.put(chainKey, textContent);
        }

        return result;
    }

}
