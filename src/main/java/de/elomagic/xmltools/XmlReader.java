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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tooling class for reading XML bind objects.
 */
public class XmlReader {

    private XmlReader() {
    }

    /**
     * Read a Simple XML annotated object from an input stream.
     * <p>
     * @param in InputStream
     * @param clazz Class type of XML Object
     * @return Object
     * @throws JAXBException Thrown when unable to read XML into object
     */
    @NotNull
    public static <T> T read(@NotNull final InputStream in, @NotNull final Class<? extends T> clazz) throws JAXBException {
        final InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        return read(isr, clazz);
    }

    /**
     * Read a Simple XML annotated object from a reader.
     * <p>
     * @param reader Reader
     * @param clazz Class type of XML Object
     * @return Object
     * @throws JAXBException Thrown when unable to read XML into object
     */
    @NotNull
    public static <T> T read(@NotNull final Reader reader, @NotNull final Class<? extends T> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller m = context.createUnmarshaller();

        return (T)m.unmarshal(reader);
    }

    /**
     * Read a Simple XML annotated object from a string.
     * <p>
     * @param s String
     * @param clazz Class type of XML Object
     * @return T
     * @throws JAXBException Thrown when unable to read XML string into object
     */
    @NotNull
    public static <T> T read(@NotNull final String s, @NotNull final Class<? extends T> clazz) throws JAXBException {
        return read(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)), clazz);
    }

    /**
     * Read a JAXB annotated object from file.
     * <p>
     * @param file File
     * @param clazz Class type of object to be read
     * @return T
     * @throws JAXBException Thrown when unable to read XML file into object
     * @throws IOException Thrown when unable to read XML from ile
     */
    @NotNull
    public static <T> T read(@NotNull final Path file, @NotNull final Class<? extends T> clazz) throws JAXBException, IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return read(reader, clazz);
        }
    }

}
