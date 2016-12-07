/*
 * Copyright (c) 2012 Jonathan Tyers, Steve Ash
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.annotation.Config;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class NestedConfig3ReloadIntegrationTest {

    private Proxy proxy;
    private XMLConfiguration xmlConfig;

    @Before
    public void setUp() throws Exception {
        xmlConfig = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(new Parameters()
                        .xml()
                        .setFileName("nestedConfig3.xml"))
                .getConfiguration();

        proxy = ConfigProxyFactory
                .getDefault()
                .make(Proxy.class, xmlConfig);
    }

    @Test
    public void shouldUpdateCachedValuesAfterUpdate() throws Exception {
        assertEquals(2, proxy.getChildren().size());
        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("bob", proxy.getChildren().get(1).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());
        assertEquals("Dallas", proxy.getChildren().get(1).getAddress().getCity());
        assertEquals("TN", proxy.getChildren().get(0).getAddress().getState());
        assertEquals("TX", proxy.getChildren().get(1).getAddress().getState());

        xmlConfig.setProperty("child(1).address.city", "Nashville");

        assertEquals(2, proxy.getChildren().size());
        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("bob", proxy.getChildren().get(1).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());
        assertEquals("Dallas", proxy.getChildren().get(1).getAddress().getCity());
        assertEquals("TN", proxy.getChildren().get(0).getAddress().getState());
        assertEquals("TX", proxy.getChildren().get(1).getAddress().getState());
    }

    @Test
    public void shouldUpdateReferencesToContainerSubnodes() throws Exception {
        Child firstChild = proxy.getChildren().get(0);

        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());

        xmlConfig.setProperty("child(0).name", "bubba");
        xmlConfig.setProperty("child(0).address.city", "Nashville");

        assertEquals("steve", firstChild.getName());
        assertEquals("Memphis", firstChild.getAddress().getCity());

        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());
    }

    @Test
    public void shouldUpdateNestedTypeReferences() throws Exception {
        NestedProxy nestedType = proxy.getNestedType();
        NestedNestedProxy nestedNestedProxy = nestedType.getNestedNestedType();

        assertEquals(42, proxy.getA());
        assertEquals(Color.RED, proxy.getColor());
        assertEquals(123, nestedType.getB());
        assertEquals(456, nestedNestedProxy.getC());

        assertEquals(123, proxy.getNestedType().getB());
        assertEquals(456, proxy.getNestedType().getNestedNestedType().getC());

        xmlConfig.setProperty("a", 43);
        xmlConfig.setProperty("nestedType.b", 987);
        xmlConfig.setProperty("nestedType.nestedNestedType.c", 654);

        assertEquals(42, proxy.getA());
        assertEquals(123, nestedType.getB());
        assertEquals(456, nestedNestedProxy.getC());

        assertEquals(42, proxy.getA());
        assertEquals(123, proxy.getNestedType().getB());
        assertEquals(456, proxy.getNestedType().getNestedNestedType().getC());
    }

    public interface Proxy {
        int getA();
        Color getColor();
        NestedProxy getNestedType();

        @Config("child")
        List<Child> getChildren();
    }

    public enum Color{
        RED,BLACK
    }
    public interface NestedProxy {
        int getB();

        NestedNestedProxy getNestedNestedType();
    }

    public interface NestedNestedProxy {
        int getC();
    }

    public interface Address {
        String getCity();

        String getState();
    }

    public interface Child {
        String getName();

        Address getAddress();
    }
}
