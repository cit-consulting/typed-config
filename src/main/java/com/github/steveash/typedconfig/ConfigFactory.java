package com.github.steveash.typedconfig;

import java.util.Properties;

/**
 * @author: drudenko
 */
public interface ConfigFactory {
    Object getConfig();

    Properties getDefaultProperties();
}
