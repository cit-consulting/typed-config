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
import com.github.steveash.typedconfig.annotation.MapKey;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Steve Ash
 */
public class NestedMapConfigIntegrationTest {

    private Proxy proxy;
    private XMLConfiguration xmlConfig;

    @Before
    public void setUp() throws Exception {
        xmlConfig = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(new Parameters().xml().setFileName("nestedMapConfig.xml")).getConfiguration();
        proxy = ConfigProxyFactory.getDefault()
                .make(Proxy.class, xmlConfig);
    }

    @Test
    public void shouldRetrieveMapValuesByGivenKey() throws Exception {
        assertEquals(42, proxy.getA());
        assertEquals("Memphis", proxy.getChildren().get("steve").getAddress().getCity());
        assertEquals("Dallas", proxy.getChildren().get("bob").getAddress().getCity());
        assertEquals("Nashville", proxy.getChildren().get("jim").getAddress().getCity());
    }

    @Test(expected = RequiredConfigurationKeyNotPresentException.class)
    public void shouldThrowIfValueMissingAndMarkedRequired() throws Exception {
        proxy.getChildren().get("doesntExist").getAddress().getCity();
    }

    @Test
    public void shouldReturnNullIfValueIsNotRequired() throws Exception {
        assertNull(proxy.getChildrenOrNull().get("reallyDoesntExist"));
    }

    @Test
    public void shouldPickupChangedValues() throws Exception {

        assertEquals("Memphis", proxy.getChildren().get("steve").getAddress().getCity());

        xmlConfig.setProperty("child(0).address.city", "Paris");

        assertEquals("Memphis", proxy.getChildren().get("steve").getAddress().getCity());
    }

    public interface Proxy {
        int getA();

        @MapKey("name")
        @Config("child")
        Map<String, Child> getChildren();

        @MapKey(value = "name", required = false)
        @Config("child")
        Map<String, Child> getChildrenOrNull();
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
