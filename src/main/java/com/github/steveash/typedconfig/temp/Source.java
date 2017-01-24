package com.github.steveash.typedconfig.temp;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

import java.lang.reflect.Method;

public interface Source<E> {
    Class<E> getProxyClass();
    BaseHierarchicalConfiguration getBaseHierarchicalConfiguration();
    void bind(ImmutableMap<Method, Object> methodObjectImmutableMap);
    Object getValue(Method method);
}