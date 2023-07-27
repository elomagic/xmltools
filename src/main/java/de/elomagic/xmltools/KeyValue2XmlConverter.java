package de.elomagic.xmltools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class KeyValue2XmlConverter {

    private String keyDelimiter = ".";

    private boolean attributeSupport = true;
    private String attributeDelimiter = "#";

    private int repetitionStart = 1;
    private String repetitionPattern = "[%s]";

    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    public void setKeyDelimiter(@NotNull String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
    }

    public String getAttributeDelimiter() {
        return attributeDelimiter;
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

    @NotNull
    public Optional<Document> convert(@NotNull Map<String, String> keyValueMap) throws ParserConfigurationException {

        if (keyValueMap.isEmpty()) {
            return Optional.empty();
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        keyValueMap.forEach((k, v) -> mapKeyValue(k, v, doc));

        return Optional.of(doc);

    }

    public void mapKeyValue(@NotNull String key, @NotNull String value, @NotNull Document document) {

        String[] items = key.split(keyDelimiter);

        //document.getDocumentElement().

        for (String item : items) {
            //document.getChildNodes()
        }


    }

}
