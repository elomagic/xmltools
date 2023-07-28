package de.elomagic.xmltools;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyValue2XmlConverter {

    private String keyDelimiter = ".";
    private Pattern keyPattern = Pattern.compile("^(?<name>[^#\\[\\]]+)(\\[(?<index>\\d+)])?(#(?<attr>.+))?$");
    private int repetitionStart = 1;

    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    public void setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
    }

    public Pattern getKeyPattern() {
        return keyPattern;
    }

    public void setKeyPattern(Pattern keyPattern) {
        this.keyPattern = keyPattern;
    }

    public int getRepetitionStart() {
        return repetitionStart;
    }

    public void setRepetitionStart(int repetitionStart) {
        this.repetitionStart = repetitionStart;
    }

    @NotNull
    public Optional<Document> convert(@NotNull Map<String, String> keyValueMap) throws ParserConfigurationException {

        if (keyValueMap.isEmpty()) {
            return Optional.empty();
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // TODO Sort keys
        keyValueMap.forEach((k, v) -> mapKeyValue(k, v, doc));

        return Optional.of(doc);

    }

    public void mapKeyValue(@NotNull String key, @NotNull String value, @NotNull Document document) {

        String[] keyChain = key.split("\\.");

        //document.getDocumentElement().

        Node element = document;

        for (String item : keyChain) {
            Matcher matcher = keyPattern.matcher(item);

            if (matcher.find()) {
                String name = matcher.group("name");
                int index = Integer.valueOf(Objects.toString(matcher.group("index"), "0"));
                // TODO Check. Attr can only be set on latest item
                String attr = matcher.group("attr");

                Optional<Element> oe = findAnyChild(element, name);
                if (oe.isEmpty()) {
                    element = element.appendChild(document.createElement(name));
                } else {
                    element = oe.get();
                }

                if (attr != null) {
                    // TODO element = ((Element)element).setAttributeNode(document.createAttribute(attr));
                }
            } else {
                throw new RuntimeException("Unsupported key value '" + key + "'.");
            }
        }

        element.appendChild(document.createTextNode(value));
    }

    @NotNull
    Optional<Element> findAnyChild(@NotNull Node parent, @NotNull String name) {
        return ElementTool
                .streamChildElements(parent)
                .filter(c -> c.getNodeName().equals(name))
                .findAny();
    }

}
