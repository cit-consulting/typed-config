package com.github.steveash.typedconfig.temp;

import com.github.steveash.typedconfig.ConfigProxyFactory;

public class DynamicConfig<E> implements ReloadableConfig {
    final Class<E> interfaze;
    final Source source;

    public DynamicConfig(Class<E> interfaze, Source source) {
        this.interfaze = interfaze;
        this.source = source;
        getProxy();
    }

    @Override
    public Object getProxy() {
        return ConfigProxyFactory.getDefault().make(interfaze, source.getBaseHierarchicalConfiguration());
    }
}
