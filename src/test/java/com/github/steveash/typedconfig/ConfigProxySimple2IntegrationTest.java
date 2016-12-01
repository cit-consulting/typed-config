package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class ConfigProxySimple2IntegrationTest {

    private Simple2 proxy;

    @Before
    public void setUp() throws Exception {
        proxy = ConfigProxyFactory.getDefault()
                .make(Simple2.class, new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setFileName("simple2Integration.xml")).getConfiguration());
    }

    @Test
    public void testWithBaseAndWithoutConfigValueAnnotation() throws Exception {
        assertEquals("123", proxy.myConfig1());
        assertEquals("456", proxy.anotherProperty());
    }

    @ConfigProxy(basekey = "myBase.")
    public interface Simple2 {

        String myConfig1();

        @Config("myConfig2")
        String anotherProperty();
    }
}
