package com.github.steveash.typedconfig.temp;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

/**
 * Created by dmitrijrudenko on 18.01.17.
 */
public class DynamicConfig<E> implements ReloadableConfig {
    private final Class<E> interfaze;
    private final Source source;
    private BaseHierarchicalConfiguration baseHierarchicalConfiguration;

    public DynamicConfig(Class<E> interfaze, Source source, BaseHierarchicalConfiguration defaultConfiguration) {
        this.interfaze = interfaze;
        this.source = source;
        this.baseHierarchicalConfiguration = defaultConfiguration;
    }

    @Override
    public Object getProxy() {
        reload();
        return ConfigProxyFactory.getDefault().make(interfaze, baseHierarchicalConfiguration);
    }

    void reload() {
        BaseHierarchicalConfiguration temp = source.getBaseHierarchicalConfiguration();
        try {
            ConfigProxyFactory.getDefault().make(interfaze, temp);
            baseHierarchicalConfiguration = temp;
        } catch (RuntimeException ex) {
            //todo: конфиг невалидный, что то сделать
        }
    }
}
