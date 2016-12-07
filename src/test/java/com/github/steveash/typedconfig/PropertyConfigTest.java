package com.github.steveash.typedconfig;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmitrijrudenko on 06.12.16.
 */
public class PropertyConfigTest {
    private Proxy proxy;
    private HierarchicalConfiguration config;

    @Before
    public void setUp() throws Exception {

        config = ConfigurationUtils
                .convertToHierarchical(
                        new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                                .configure(new Parameters()
                                        .properties()
                                        .setFileName("propertyConfig.properties")
                                        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')))
                                .getConfiguration(),
                        DefaultExpressionEngine.INSTANCE);
        proxy = ConfigProxyFactory
                .getDefault()
                .make(Proxy.class, config);
    }

    @Test
    public void testProperties() throws ConfigurationException {
        assertEquals("string", proxy.getA());
        assertEquals(Integer.valueOf(100), proxy.getB());
        assertEquals(3, proxy.getC().size());
        assertEquals(Integer.valueOf(1), proxy.getC().get(0));
        assertEquals(Integer.valueOf(2), proxy.getC().get(1));
        assertEquals(Integer.valueOf(3), proxy.getC().get(2));

        Child child = proxy.getChild();
        assertEquals("Dima", child.getName());
        assertEquals(3, child.getD().size());
        assertEquals(Integer.valueOf(111), child.getE());
        assertEquals(Integer.valueOf(4), child.getD().get(0));
        assertEquals(Integer.valueOf(5), child.getD().get(1));
        assertEquals(Integer.valueOf(6), child.getD().get(2));

        config.setProperty("a", "string2");
        config.setProperty("b", 0);
        config.setProperty("child.name", "Sveta");
        config.setProperty("child.e", 123);
        config.setProperty("c(0)", 100);

        assertEquals("Dima", child.getName());
        assertEquals(Integer.valueOf(100), proxy.getB());
        assertEquals("string", proxy.getA());
        assertEquals("Dima", proxy.getChild().getName());
        assertEquals(Integer.valueOf(111), child.getE());
    }

    public interface Proxy {
        String getA();
        Integer getB();
        Child getChild();
        List<Integer> getC();
    }

    public interface Child {
        String getName();
        List<Integer> getD();
        Integer getE();
    }
}
