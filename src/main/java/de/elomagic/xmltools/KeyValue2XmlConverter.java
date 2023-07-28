package de.elomagic.xmltools;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
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

    /**
     * Returns the delimiter string, which will divide key into key items.
     * <p>
     * Default "."
     *
     * @return key item delimiter
     */
    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    /**
     * Set the delimiter string, which will divide key into key items.
     *
     * @param keyDelimiter key item delimiter
     */
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

        keyValueMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> mapKeyValue(e.getKey(), e.getValue(), doc));

        return Optional.of(doc);

    }

    public void mapKeyValue(@NotNull String key, @NotNull String value, @NotNull Document document) {

        String[] keyChain = key.split("\\.");

        //document.getDocumentElement().

        Node element = document;

        boolean isAttr = false;

        for (String item : keyChain) {
            Matcher matcher = keyPattern.matcher(item);

            if (matcher.find()) {
                String name = matcher.group("name");
                int index = Integer.valueOf(Objects.toString(matcher.group("index"), Integer.toString(repetitionStart)));
                // TODO Check. Attr can only be set on latest item
                String attr = matcher.group("attr");

                while (findChild(element, index-repetitionStart, name).isEmpty()) {
                    element.appendChild(document.createElement(name));
                }

                element = findChild(element, index-repetitionStart, name).orElseThrow();

                if (attr != null) {
                    Attr a = document.createAttribute(attr);
                    a.setValue(value);
                    ((Element)element).setAttributeNode(a);
                    isAttr = true;
                }
            } else {
                throw new RuntimeException("Unsupported key value '" + key + "'.");
            }
        }

        if (!isAttr) {
            element.appendChild(document.createTextNode(value));
        }
    }

    @NotNull
    Optional<Element> findChild(@NotNull Node parent, int index, @NotNull String name) {
        return ElementTool
                .streamChildElements(parent)
                .filter(c -> c.getNodeName().equals(name))
                .skip(index)
                .findAny();
    }

}
