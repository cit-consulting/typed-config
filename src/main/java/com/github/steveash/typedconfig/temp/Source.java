package com.github.steveash.typedconfig.temp;

import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public interface Source<E> {
    Class<E> getProxyClass();
    BaseHierarchicalConfiguration getBaseHierarchicalConfiguration();
}