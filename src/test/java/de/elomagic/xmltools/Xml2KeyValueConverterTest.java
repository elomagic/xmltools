package de.elomagic.xmltools;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Xml2KeyValueConverterTest {

    @Test
    void read() throws Exception {

        Xml2KeyValueConverter converter = new Xml2KeyValueConverter();

        Map<String, String> result = converter.read(getClass().getResourceAsStream("/sample01.xml"));

        //result.forEach((key, value) -> System.out.println(key + "=" + value));

        result.keySet().stream().sorted().forEach(k -> System.out.println(k + "=" + result.get(k)));

        assertEquals(15, result.size());
        assertEquals("abc", result.get("root.child1#attr1"));

        converter.setAttributeSupport(false);
        Map<String, String> result2 = converter.read(getClass().getResourceAsStream("/sample01.xml"));
        assertEquals(12, result2.size());
        assertFalse(result.containsKey("abc"));
    }
}