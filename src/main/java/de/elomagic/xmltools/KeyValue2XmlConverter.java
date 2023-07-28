package de.elomagic.xmltools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class KeyValue2XmlConverter {

    private String keyDelimiter = ".";

    private boolean attributeSupport = true;
    private Pattern attributeDelimiterRegEx = Pattern.compile(".+#(.*)$");

    private int repetitionStart = 1;
    private Pattern repetitionRegExPattern = Pattern.compile(".+\\[(\\d)\\]$");

    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    public void setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
    }

    public Pattern getAttributeDelimiterRegEx() {
        return attributeDelimiterRegEx;
    }

    public void setAttributeDelimiterRegEx(Pattern attributeDelimiterRegEx) {
        this.attributeDelimiterRegEx = attributeDelimiterRegEx;
    }

    public boolean isAttributeSupport() {
        return attributeSupport;
    }

    public void setAttributeSupport(boolean attributeSupport) {
        this.attributeSupport = attributeSupport;
    }

    public int getRepetitionStart() {
        return repetitionStart;
    }

    public void setRepetitionStart(int repetitionStart) {
        this.repetitionStart = repetitionStart;
    }

    public Pattern getRepetitionRegExPattern() {
        return repetitionRegExPattern;
    }

    public void setRepetitionRegExPattern(Pattern repetitionRegExPattern) {
        this.repetitionRegExPattern = repetitionRegExPattern;
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
        //document.appendChild(document.createElement("bbbb"));

        for (String name : keyChain) {
            element = findAnyChild(element, name)
                    .orElse((Element)element.appendChild(document.createElement(name)));
        }

        element.appendChild(document.createTextNode(value));

    }

    @NotNull
    Optional<Element> findAnyChild(@NotNull Node parent, @NotNull String name) {
        return ElementTool.streamChildElements(parent).filter(c -> c.getNodeName().equals(name)).findAny();
    }

}
