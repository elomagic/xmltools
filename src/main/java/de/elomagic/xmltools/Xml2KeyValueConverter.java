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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tooling class to convert a XML document to a key value map.
 */
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
     * @return This instance
     */
    public Xml2KeyValueConverter setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
        return this;
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
     * @return This instance
     */
    public Xml2KeyValueConverter setAttributeSupport(boolean attributeSupport) {
        this.attributeSupport = attributeSupport;
        return this;
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
     * @return Return this instance
     */
    public Xml2KeyValueConverter setAttributeDelimiter(@NotNull String attributeDelimiter) {
        this.attributeDelimiter = attributeDelimiter;
        return this;
    }

    /**
     * Returns the index where the repetition will start. Default 1
     *
     * @return Returns the repetition start index
     */
    public int getRepetitionStart() {
        return repetitionStart;
    }

    /**
     * Set the index where the repetition will start. Default 1
     *
     * @param repetitionStart The repetition start index
     * @return Returns this instance
     */
    public Xml2KeyValueConverter setRepetitionStart(int repetitionStart) {
        this.repetitionStart = repetitionStart;
        return this;
    }

    /**
     * Returns the repetition pattern.
     *
     * @return Returns the pattern.
     */
    public String getRepetitionPattern() {
        return repetitionPattern;
    }

    /**
     * Set the repetition pattern.
     *
     * @param repetitionPattern The Pattern
     * @return Returns this instance
     */
    public Xml2KeyValueConverter setRepetitionPattern(@NotNull String repetitionPattern) {
        this.repetitionPattern = repetitionPattern;
        return this;
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
    public Map<String, String> convert(@NotNull Path file) throws ParserConfigurationException, IOException, SAXException {

        return convert(Files.newInputStream(file));

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
    public Map<String, String> convert(@NotNull InputStream in) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        //doc.getDocumentElement().normalize();

        return addKeyPrefix(doc.getDocumentElement().getNodeName(), parseChildElements(doc.getDocumentElement()));
    }

    boolean hasChildElements(@NotNull Element element) {
        return ElementTool.streamChildElements(element).findAny().isPresent();
    }

    boolean hasChildText(@NotNull Element element) {
        return ElementTool.streamChildElements(element).findFirst().isEmpty();
    }

    @NotNull
    Map<String, String> parseChildElements(@NotNull Element element) {

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
        ElementTool.streamChildElements(element)
                .filter(this::hasChildText)
                .forEach(child -> {
                    groupedChildKeys.put(child.getNodeName(), groupedChildKeys.getOrDefault(child.getNodeName(), 0) + 1);
                    groupedChildIndexKeys.put(child.getNodeName(), new AtomicInteger(repetitionStart));
                });
        ElementTool.streamChildElements(element)
                .filter(this::hasChildElements)
                .forEach(child -> {
                    groupedChildKeys.put(child.getNodeName(), groupedChildKeys.getOrDefault(child.getNodeName(), 0) + 1);
                    groupedChildIndexKeys.put(child.getNodeName(), new AtomicInteger(repetitionStart));
                });

        // Map elements with text
        ElementTool.streamChildElements(element)
                .filter(this::hasChildText)
                .forEach(child -> {
                    String childName = child.getNodeName();
                    String key = groupedChildKeys.get(childName) == 1
                            ? childName
                            : (childName + String.format(repetitionPattern, groupedChildIndexKeys.get(childName).getAndIncrement()));

                    result.put(key, child.getTextContent());
        });

        // Map elements with elements inside
        ElementTool.streamChildElements(element)
                .filter(this::hasChildElements)
                .forEach(child -> {
                    String childName = child.getNodeName();
                    String key = groupedChildKeys.getOrDefault(childName, repetitionStart) == 1
                            ? childName
                            : (childName + String.format(repetitionPattern, groupedChildIndexKeys.get(childName).getAndIncrement()));

                    parseChildElements(child).forEach((k, v) -> result.put(addKeyPrefix(key, k), v));
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
