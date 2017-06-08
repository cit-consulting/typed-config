package com.github.steveash.typedconfig.temp;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

public interface Source<E> {
    Class<E> getProxyClass();

    HierarchicalConfiguration getBaseHierarchicalConfiguration();

    void bind(Map<Method, Object> methodObjectImmutableMap);

    Object getValue(Method method);
}