package com.github.steveash.typedconfig.temp;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public class AutoReloadableDynamicConfig<E> extends DynamicConfig<E> {
    private final PollingStrategy pollingStrategy;
    private BaseHierarchicalConfiguration configuration;

    public AutoReloadableDynamicConfig(Class<E> interfaze,
                                       Source source,
                                       PollingStrategy pollingStrategy) {
        super(interfaze, source);
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
    public Object getProxy() {
        reload();
        return ConfigProxyFactory.getDefault().make(interfaze, configuration);
    }

    public void shutdown() {
        pollingStrategy.shutdown();
    }

    private void reload() {
        BaseHierarchicalConfiguration temp = source.getBaseHierarchicalConfiguration();
        try {
            ConfigProxyFactory.getDefault().make(interfaze, temp);
            configuration = temp;
        } catch (RuntimeException ex) {
            //todo: конфиг невалидный, что то сделать
        }
    }
}
