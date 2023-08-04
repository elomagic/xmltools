package de.elomagic.xmltools;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Xml2KeyValueConverterTest {

    @Test
    void read() throws Exception {

        Xml2KeyValueConverter converter = new Xml2KeyValueConverter();

        Map<String, String> result = converter.convert(getClass().getResourceAsStream("/sample01.xml"));

        //result.forEach((key, value) -> System.out.println(key + "=" + value));

        //result.keySet().stream().sorted().forEach(k -> System.out.println(k + "=" + result.get(k)));
        result.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> System.out.println(e.getKey() + " = " + e.getValue()));

        assertEquals(26, result.size());
        assertEquals("1", result.get("root.child1[1].subelement[1]"));
        assertEquals("2", result.get("root.child1[1].subelement[2]"));
        assertEquals("false", result.get("root.child5.active"));

        converter.setAttributeSupport(false);
        Map<String, String> result2 = converter.convert(getClass().getResourceAsStream("/sample01.xml"));
        assertEquals(23, result2.size());
        assertFalse(result.containsKey("abc"));
    }

    @Test
    void testAddKeyPrefix() {

        Xml2KeyValueConverter converter = new Xml2KeyValueConverter();

        assertEquals("value", converter.addKeyPrefix("a", Map.of("b.c", "value")).get("a.b.c"));
    }
}