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
        return streamElementChilds(element).findAny().isPresent();
    }

    boolean hasChildText(@NotNull Element element) {
        return streamElementChilds(element).findFirst().isEmpty();
    }

    @NotNull
    Map<String, String> parseElementChilds(@NotNull Element element) {

        Map<String, String> result = new HashMap<>();

        // Map attributes the element
        if (attributeSupport) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Attr attr = (Attr) element.getAttributes().item(i);
                result.put(String.join(attributeDelimiter, element.getNodeName(), attr.getName()), attr.getValue());
            }
        }

        // Grouped multiple elements names
        Map<String, Integer> groupedChildKeys = new HashMap<>();
        Map<String, AtomicInteger> groupedChildIndexKeys = new HashMap<>();
        streamElementChilds(element)
                .filter(this::hasChildText)
                .forEach(child -> {
                    groupedChildKeys.put(child.getNodeName(), groupedChildKeys.getOrDefault(child.getNodeName(), 0) + 1);
                    groupedChildIndexKeys.put(child.getNodeName(), new AtomicInteger(repetitionStart));
                });
        streamElementChilds(element)
                .filter(this::hasElementsChilds)
                .forEach(child -> {
                    groupedChildKeys.put(child.getNodeName(), groupedChildKeys.getOrDefault(child.getNodeName(), 0) + 1);
                    groupedChildIndexKeys.put(child.getNodeName(), new AtomicInteger(repetitionStart));
                });

        // Map elements with text
        streamElementChilds(element)
                .filter(this::hasChildText)
                .forEach(child -> {
                    String childName = child.getNodeName();
                    String key = addKeyPrefix(element.getNodeName(),
                            groupedChildKeys.get(childName) == 1
                                    ? childName
                                    : (childName + String.format(repetitionPattern, groupedChildIndexKeys.get(childName).getAndIncrement())));

                    result.put(key, child.getTextContent());
        });

        // Map elements with elements inside
        streamElementChilds(element)
                .filter(this::hasElementsChilds)
                .forEach(child -> {
                    String childName = child.getNodeName();
                    String key = addKeyPrefix(element.getNodeName(),
                            groupedChildKeys.getOrDefault(childName, repetitionStart) == 1
                                    ? childName
                                    : (childName + String.format(repetitionPattern, groupedChildIndexKeys.get(childName).getAndIncrement())));

                    parseElementChilds(child).forEach((k, v) -> {
                        result.put(addKeyPrefix(key, childName), v);
                    });
                });

        return result;
    }

    @NotNull
    String addKeyPrefix(@NotNull String prefixKey, @NotNull String key) {
        return String.join(keyDelimiter, prefixKey, key);
    }

    @NotNull
    Map<String, String> addKeyPrefix(@NotNull String prefixKey, @NotNull Map<String, String> map) {
        Map<String, String> result = new HashMap<>();

        map.forEach((k, v) -> result.put(String.join(keyDelimiter, prefixKey, k), v));

        return result;
    }

}
