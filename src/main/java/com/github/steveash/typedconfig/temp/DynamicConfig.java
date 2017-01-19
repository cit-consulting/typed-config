package com.github.steveash.typedconfig.temp;

import com.github.steveash.typedconfig.ConfigProxyFactory;

public class DynamicConfig<E> implements ReloadableConfig {
    private final Source<E> source;

    public DynamicConfig(Source<E> source) {
        this.source = source;
    }

    @Override
    public E getProxy() {
        return ConfigProxyFactory.getDefault().make(source.getProxyClass(), source.getBaseHierarchicalConfiguration());
    }
}
