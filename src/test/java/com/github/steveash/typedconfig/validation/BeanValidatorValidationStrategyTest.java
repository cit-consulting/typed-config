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

package com.github.steveash.typedconfig.validation;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.fail;

/**
 * @author Steve Ash
 */
public class BeanValidatorValidationStrategyTest {

    @Test
    public void testProxyEmailGood() {
        Properties properties = new Properties();
        properties.put("email", "test@tets.com");
        getProxy(ProxyEmail.class, properties);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testProxyEmailBad() {
        Properties properties = new Properties();
        properties.put("email", "1234");
        getProxy(ProxyEmail.class, properties);
        fail();
    }

    @Test
    public void testProxyNotEmptyGood() {
        Properties properties = new Properties();
        properties.put("notEmpty", "anystring");
        getProxy(ProxyNotEmpty.class, properties);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testProxyNotEmptyBad() {
        Properties properties = new Properties();
        properties.put("notEmpty", "");
        getProxy(ProxyNotEmpty.class, properties);
        fail();
    }

    @Test
    public void testProxyRegexpGood() {
        Properties properties = new Properties();
        properties.put("regexp", "123-45-6789");
        getProxy(ProxyRegexp.class, properties);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testProxyRegexpBad() {
        Properties properties = new Properties();
        properties.put("regexp", "1234567");
        getProxy(ProxyRegexp.class, properties);
        fail();
    }

    @Test
    public void testProxyRandeGood() {
        Properties properties = new Properties();
        properties.put("rande", "15");
        getProxy(ProxyRande.class, properties);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testProxyRandeBad() {
        Properties properties = new Properties();
        properties.put("rande", "5");
        getProxy(ProxyRande.class, properties);
        fail();
    }


    @Test
    public void testProxyListEmptyGood() {
        Properties properties = new Properties();
        properties.put("list", "1,2,3");
        getProxy(ProxyListEmpty.class, properties);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testProxyListEmptyBad() {
        Properties properties = new Properties();
        getProxy(ProxyListEmpty.class, properties);
        fail();
    }

    private Object getProxy(Class proxyClass, Properties properties) {
        BaseHierarchicalConfiguration configOfProperties;
        configOfProperties = new BaseHierarchicalConfiguration();
        configOfProperties.append(ConfigurationConverter.getConfiguration(properties));
        configOfProperties.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        return ConfigProxyFactory.builder().beanValidation().build().make(proxyClass, configOfProperties);
    }

    public interface ProxyEmail {
        @Email
        String getEmail();
    }

    public interface ProxyNotEmpty {
        @NotBlank
        String getNotEmpty();
    }

    public interface ProxyRegexp {
        @Pattern(regexp = "\\d{3}-\\d{2}-\\d{4}")
        String getRegexp();
    }


    public interface ProxyRande {
        @Range(min = 10, max = 20)
        int getRande();
    }

    public interface ProxyListEmpty {
        @NotEmpty
        List<Integer> getList();
    }
}
