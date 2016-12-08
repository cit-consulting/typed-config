package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.MapKey;
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

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmitrijrudenko on 06.12.16.
 */
public class PropertyConfigTest {
//    private Proxy proxyOfProperties;
//    private BaseHierarchicalConfiguration configOfProperties;

    private Proxy proxyOfFile;
    private HierarchicalConfiguration configOfFile;

    @Before
    public void setUp() throws Exception {
//        Properties properties = new Properties();
//        properties.put("a", "string");
//        properties.put("b", "100");
//
//        properties.put("child.name", "Dima");
//        properties.put("child.e", "111");
//
//        properties.put("childMap.name", "Name1");
//        properties.put("childMap.age", "21");
////
//        properties.put("childMap.name", "Name2");
//        properties.put("childMap.age", "22");
//
//        //Properties
//        configOfProperties = new BaseHierarchicalConfiguration();
//        configOfProperties.append(ConfigurationConverter.getConfiguration(properties));
//        configOfProperties.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
//        proxyOfProperties = ConfigProxyFactory.getDefault().make(Proxy.class, configOfProperties);

        //Properties file
        configOfFile = ConfigurationUtils
                .convertToHierarchical(
                        new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                                .configure(new Parameters()
                                        .hierarchical()
                                        .setFileName("propertyConfig.properties")
                                        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')))
                                .getConfiguration(),
                        DefaultExpressionEngine.INSTANCE);
        proxyOfFile = ConfigProxyFactory
                .getDefault()
                .make(Proxy.class, configOfFile);
    }

    @Test
    public void testProperties() throws ConfigurationException {
//        testProperties(proxyOfProperties, configOfProperties);
        testProperties(proxyOfFile, configOfFile);
    }

    private void testProperties(Proxy proxy, HierarchicalConfiguration configuration) throws ConfigurationException {
        assertEquals("string", proxy.getA());
        assertEquals(Integer.valueOf(100), proxy.getB());

        Child child = proxy.getChild();
        assertEquals("Dima", child.getName());
        assertEquals(Integer.valueOf(111), child.getE());

        assertEquals("Name1", proxy.getNestedProxy().get("Name1").getName());
        assertEquals(Integer.valueOf(21), proxy.getNestedProxy().get("Name1").getAge());
        assertEquals("Name2", proxy.getNestedProxy().get("Name2").getName());
        assertEquals(Integer.valueOf(22), proxy.getNestedProxy().get("Name2").getAge());

        configuration.setProperty("a", "string2");
        configuration.setProperty("b", 0);
        configuration.setProperty("child.name", "Sveta");
        configuration.setProperty("child.e", 123);

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

        @MapKey("name")
        @Config("childMap")
        Map<String, NestedProxy> getNestedProxy();
    }

    public interface Child {
        String getName();

        Integer getE();
    }

    public interface NestedProxy {
        String getName();

        Integer getAge();
    }
}
