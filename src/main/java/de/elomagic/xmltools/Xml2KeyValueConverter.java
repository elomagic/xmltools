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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Xml2KeyValueConverter {

    private String keyDelimiter = ".";

    private boolean attributeSupport = true;
    private String attributeDelimiter = "#";

    private int repetitionStart = 1;
    private String repetitionPattern = "[%s]";

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

    public int getRepetitionStart() {
        return repetitionStart;
    }

    public void setRepetitionStart(int repetitionStart) {
        this.repetitionStart = repetitionStart;
    }

    public String getRepetitionPattern() {
        return repetitionPattern;
    }

    public void setRepetitionPattern(@NotNull String repetitionPattern) {
        this.repetitionPattern = repetitionPattern;
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

        return parseElementChilds(doc.getDocumentElement());
    }

    Stream<Element> streamElementChilds(@NotNull Element element) {

        List<Element> childElements = new ArrayList<>();

        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            Node child = element.getChildNodes().item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                childElements.add((Element)child);
            }
        }

        return childElements.isEmpty() ? Stream.empty() : childElements.stream();

    }

    boolean hasElementsChilds(@NotNull Element element) {
        return streamElementChilds(element).findFirst().isPresent();
    }

    @NotNull
    Map<String, String> parseElementChilds(@NotNull Element element) {

        Map<String, String> result = new HashMap<>();

        if (attributeSupport) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Attr attr = (Attr) element.getAttributes().item(i);
                result.put(String.join(attributeDelimiter, element.getNodeName(), attr.getName()), attr.getValue());
            }
        }

        Map<String, List<String>> groupedTextChilds = new HashMap<>();
        // Detect multiple text elements
        streamElementChilds(element)
                .filter(e -> !hasElementsChilds(e))
                .forEach(e -> {
                    List<String> list = groupedTextChilds.getOrDefault(e.getNodeName(), new ArrayList<>());
                    list.add(e.getTextContent());
                    groupedTextChilds.put(e.getNodeName(), list);
                });
        groupedTextChilds.forEach((k, m) -> {
            if (m.size() == 1) {
                result.put(paddingKey(element.getNodeName(), k), m.get(0));
            } else {
                AtomicInteger i = new AtomicInteger(repetitionStart);
                m.forEach(v -> result.put(paddingKey(element.getNodeName(), k + String.format(repetitionPattern, i.getAndIncrement())), v));
            }
        });

        // Detect multiple child element elements
        Map<String, Map<String, String>> groupedElementChilds = new HashMap<>();
        streamElementChilds(element)
                .filter(this::hasElementsChilds)
                .forEach(e -> {
                    Map<String, String> m = groupedElementChilds.getOrDefault(e.getNodeName(), new HashMap<>());

                    m.putAll(paddingKeys(parseElementChilds(e), element.getNodeName()));

                    groupedElementChilds.put(e.getNodeName(), m);
                });
        groupedElementChilds.forEach((k, m) -> {
            if (m.size() == 1) {
                result.putAll(m);
            } else {
                AtomicInteger i = new AtomicInteger(repetitionStart);
                m.forEach((k2, v) -> result.put(k2 + String.format(repetitionPattern, i.getAndIncrement()), v));
            }
        });

        return result;
    }

    @NotNull
    String paddingKey(@NotNull String paddingKey, @NotNull String key) {
        return String.join(keyDelimiter, paddingKey, key);
    }

    @NotNull
    Map<String, String> paddingKeys(@NotNull Map<String, String> map, @NotNull String paddingKey) {
        Map<String, String> result = new HashMap<>();

        map.forEach((k, v) -> result.put(String.join(keyDelimiter, paddingKey, k), v));

        return result;
    }

}
