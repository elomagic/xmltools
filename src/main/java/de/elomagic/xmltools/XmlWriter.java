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
import jakarta.xml.bind.Marshaller;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Tooling class for writing XML bind objects.
 */
public class XmlWriter {

    private XmlWriter() {

    }

    /**
     * Write a Simple XML annotated object to an output stream with UTF-8.
     *
     * @param out Output stream
     * @param o Simple XML Annotated object
     * @throws JAXBException Thrown when unable to serialize the object into an XML document
     */
    public static void write(@NotNull final OutputStream out, @NotNull final Object o) throws JAXBException {
        OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        write(osw, o);
    }

    /**
     * Write a Simple XML annotated object to an writer stream.
     *
     * @param writer Writer stream
     * @param o Simple XML Annotated object
     * @throws JAXBException Thrown when unable to serialize the object into an XML document
     */
    public static void write(@NotNull final Writer writer, @NotNull final Object o) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(o.getClass());
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        m.marshal(o, writer);
    }

    /**
     * Write a Simple XML annotated object to local file.
     *
     * @param output File
     * @param o Simple XML Annotated object
     * @throws IOException Thrown when unable to write into the file
     * @throws JAXBException Thrown when unable to serialize the object into an XML document
     */
    public static void write(@NotNull final Path output, @NotNull final Object o) throws IOException, JAXBException {
        try (Writer writer = Files.newBufferedWriter(output, StandardOpenOption.WRITE)) {
            write(writer, o);
        }
    }

}
