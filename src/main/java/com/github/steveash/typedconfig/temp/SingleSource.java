package com.github.steveash.typedconfig.temp;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by dmitrijrudenko on 24.01.17.
 */
public class SingleSource<E> implements Source<E> {
    private final Class<E> interfaze;
    private final HierarchicalConfiguration configuration;
    private ImmutableMap<Method, Object> methodObjectImmutableMap;

    public SingleSource(Class<E> interfaze, HierarchicalConfiguration configuration) {
        this.interfaze = interfaze;
        this.configuration = configuration;
    }

    @Override
    public Class<E> getProxyClass() {
        return interfaze;
    }

    @Override
    public HierarchicalConfiguration getBaseHierarchicalConfiguration() {
        return configuration;
    }

    @Override
    public Object getValue(Method method) {
        if (methodObjectImmutableMap == null) {
            throw new IllegalArgumentException("Не была проищведена связка с проксей.");
        }
        return methodObjectImmutableMap.get(method);
    }

    @Override
    public void bind(Map<Method, Object> immutableMap) {
        if (methodObjectImmutableMap != null) {
            //можно только один раз
            return;
        }
        methodObjectImmutableMap = ImmutableMap.copyOf(immutableMap);
    }
}
