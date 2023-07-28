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
