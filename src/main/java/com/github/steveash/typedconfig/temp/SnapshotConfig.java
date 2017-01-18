package com.github.steveash.typedconfig.temp;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public class SnapshotConfig<E> implements Config {
    private final BaseHierarchicalConfiguration baseHierarchicalConfiguration;
    private final Class<E> interfaze;

    public SnapshotConfig(Class<E> interfaze,
                          BaseHierarchicalConfiguration baseHierarchicalConfiguration) {
        this.interfaze = interfaze;
        this.baseHierarchicalConfiguration = baseHierarchicalConfiguration;
    }

    @Override
    public Object getProxy() {
        return ConfigProxyFactory.getDefault().make(interfaze, baseHierarchicalConfiguration);
    }
}
