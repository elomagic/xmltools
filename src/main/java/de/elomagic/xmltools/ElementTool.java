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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ElementTool {

    private ElementTool() {
    }

    /**
     * Streams eny child element of type {@link Node#ELEMENT_NODE}.
     *
     * @param parentElement Parent element node
     *
     * @return A stream but never null.
     */
    @NotNull
    public static Stream<Element> streamChildElements(@NotNull Node parentElement) {

        List<Element> childElements = new ArrayList<>();

        for (int i = 0; i < parentElement.getChildNodes().getLength(); i++) {
            Node child = parentElement.getChildNodes().item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                childElements.add((Element)child);
            }
        }

        return childElements.isEmpty() ? Stream.empty() : childElements.stream();

    }

}
