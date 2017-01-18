package com.github.steveash.typedconfig.temp;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public class DynamicConfig<E> implements ReloadableConfig {
    private final Class<E> interfaze;
    private final PollingStrategy pollingStrategy;
    private final Source source;
    private BaseHierarchicalConfiguration baseHierarchicalConfiguration;

    public DynamicConfig(Class<E> interfaze,
                         BaseHierarchicalConfiguration defaultConfiguration,
                         Source source,
                         PollingStrategy pollingStrategy) {
        this.interfaze = interfaze;
        this.baseHierarchicalConfiguration = defaultConfiguration;
        this.source = source;
        this.pollingStrategy = pollingStrategy;
        this.pollingStrategy.execute(() -> {
            try {
                reload();
            } catch (Exception e) {
                throw new RuntimeException("Failed to poll configuration", e);
            }
        });
    }

    @Override
    public void reload() {
        BaseHierarchicalConfiguration temp = source.getBaseHierarchicalConfiguration();
        try {
            ConfigProxyFactory.getDefault().make(interfaze, temp);
            baseHierarchicalConfiguration = temp;
        } catch (RuntimeException ex) {
            //todo: конфиг невалидный, что то сделать
        }

    }

    public void shutdown() {
        pollingStrategy.shutdown();
    }


    @Override
    public Object getProxy() {
        return ConfigProxyFactory.getDefault().make(interfaze, baseHierarchicalConfiguration);
    }
}
