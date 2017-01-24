package com.github.steveash.typedconfig.temp;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

import java.lang.reflect.Method;

/**
 * Created by dmitrijrudenko on 24.01.17.
 */
public class SingleSource implements Source {
    private final Class interfaze;
    private final BaseHierarchicalConfiguration configuration;
    private ImmutableMap<Method, Object> methodObjectImmutableMap;

    public SingleSource(Class interfaze, BaseHierarchicalConfiguration configuration) {
        this.interfaze = interfaze;
        this.configuration = configuration;
    }

    @Override
    public Class getProxyClass() {
        return interfaze;
    }

    @Override
    public BaseHierarchicalConfiguration getBaseHierarchicalConfiguration() {
        return configuration;
    }

    @Override
    public Object getValue(Method method) {
        return methodObjectImmutableMap.get(method);
    }

    @Override
    public void bind(ImmutableMap immutableMap) {
        methodObjectImmutableMap = immutableMap;
    }
}
