package com.github.steveash.typedconfig.temp;

import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public class AutoReloadableDynamicConfig<E> extends DynamicConfig<E> {
    private final PollingStrategy pollingStrategy;

    public AutoReloadableDynamicConfig(Class<E> interfaze,
                                       Source source,
                                       BaseHierarchicalConfiguration defaultConfiguration,
                                       PollingStrategy pollingStrategy) {
        super(interfaze, source, defaultConfiguration);
        this.pollingStrategy = pollingStrategy;
        this.pollingStrategy.execute(() -> {
            try {
                reload();
            } catch (Exception e) {
                throw new RuntimeException("Failed to poll configuration", e);
            }
        });
    }

    public void shutdown() {
        pollingStrategy.shutdown();
    }
}
