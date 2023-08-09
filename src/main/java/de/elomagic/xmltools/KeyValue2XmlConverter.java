/*
 * XML Tools
 * Copyright (c) 2023-present Carsten Rambow
 * mailto:developer AT elomagic DOT de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * Tooling class to convert a key value map to a XML document.
 */
public class KeyValue2XmlConverter {

    private String keyDelimiter = ".";
    private Pattern keyPattern = Pattern.compile("^(?<name>[^#\\[\\]]+)(\\[(?<index>\\d+)])?(#(?<attr>.+))?$");
    private int repetitionStart = 1;

    /**
     * Returns the delimiter string, which will divide key into key items.
     * <p>
     * Default "."
     *
     * @return key item delimiter but never null
     */
    @NotNull
    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    /**
     * Set the delimiter string, which will divide key into key items.
     *
     * @param keyDelimiter key item delimiter
     * @return This instance
     */
    public KeyValue2XmlConverter setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
        return this;
    }

    /**
     * Returns a regular expression {@link Pattern} to separate key item into name, repetition and attribute.
     * <p>
     * Default "^(?<name>[^#\\[\\]]+)(\\[(?<index>\\d+)])?(#(?<attr>.+))?$"
     *
     * @return The key pattern but never null.
     */
    @NotNull
    public Pattern getKeyPattern() {
        return keyPattern;
    }

    /**
     * Set regular expression {@link Pattern} to separate key item into name, repetition and attribute.
     *
     * @param keyPattern The key pattern but never null.
     * @return This instance
     */
    public KeyValue2XmlConverter setKeyPattern(@NotNull Pattern keyPattern) {
        this.keyPattern = keyPattern;
        return this;
    }

    public int getRepetitionStart() {
        return repetitionStart;
    }

    /**
     * Set value, where repetition index will start.
     *
     * @param repetitionStart Start from index
     * @return This instance
     */
    public KeyValue2XmlConverter setRepetitionStart(int repetitionStart) {
        this.repetitionStart = repetitionStart;
        return this;
    }

    /**
     * Converts a key value map to an XML {@link Document}.
     *
     * @param keyValueMap Key value map
     * @return Returns an {@link Optional} but never null
     * @throws ParserConfigurationException Thrown when unable to create the XML document.
     */
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

    void mapKeyValue(@NotNull String key, @NotNull String value, @NotNull Document document) {

        String[] keyChain = key.split("\\.");

        //document.getDocumentElement().

        Node element = document;

        boolean isAttr = false;

        for (String item : keyChain) {
            Matcher matcher = keyPattern.matcher(item);

            if (matcher.find()) {
                String name = matcher.group("name");
                int index = Integer.parseInt(Objects.toString(matcher.group("index"), Integer.toString(repetitionStart)));
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
