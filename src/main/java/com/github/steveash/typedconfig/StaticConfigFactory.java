package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.ConfigFactory;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: drudenko
 */
public class StaticConfigFactory implements ConfigFactory {
    private final Properties defaultProperties;
    private final Object config;

    public StaticConfigFactory(Class interfaze,
                        Properties defaultProperties) throws IOException {
        final BaseHierarchicalConfiguration configOfFile;
        configOfFile = new BaseHierarchicalConfiguration();
        configOfFile.append(ConfigurationConverter.getConfiguration(defaultProperties));
        configOfFile.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        this.defaultProperties = defaultProperties;

        config = ConfigProxyFactory
                .getDefault()
                .make(interfaze, configOfFile);
    }

    @Override
    public Properties getDefaultProperties() {
        return defaultProperties;
    }

    @Override
    public Object getConfig() {
        return config;
    }
}
