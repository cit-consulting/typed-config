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
import org.apache.commons.configuration2.sync.LockMode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class EnumConfigIntegrationTest {

    private Proxy proxy;
    private XMLConfiguration xmlConfig;

    @Before
    public void setUp() throws Exception {
        xmlConfig = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(new Parameters()
                        .xml()
                        .setFileName("enumConfig.xml"))
                .getConfiguration();

        proxy = ConfigProxyFactory
                .getDefault()
                .make(Proxy.class, xmlConfig);
    }

    @Test
    public void shouldUpdateNestedTypeReferences() throws Exception {
        assertEquals(Color.RED, proxy.getColors().get(0));
        assertEquals(Color.RED, proxy.getColors().get(1));

        xmlConfig.setProperty("color(0)", "BLACK");

        assertEquals(Color.RED, proxy.getColors().get(0));

    }

    public enum Color {
        RED, BLACK
    }

    public interface Proxy {
        @Config("color")
        List<Color> getColors();
    }
}
