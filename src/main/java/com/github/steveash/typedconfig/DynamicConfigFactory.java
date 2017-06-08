package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.ConfigFactory;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.PropertiesProvider;
import com.github.steveash.typedconfig.validation.BeanValidatorValidationStrategy;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.validation.Validator;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: drudenko
 */
public class DynamicConfigFactory implements ConfigFactory {
    private final Properties defaultProperties;
    private final Class interfaze;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final PropertiesProvider propertiesProvider;
    private final ConfigProxyFactory configProxyFactory;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private long initialDelay = 0;
    private long period = 1;
    private Object config;
    private Object snapshot;

    public DynamicConfigFactory(Class interfaze,
                                Properties defaultProperties,
                                PropertiesProvider propertiesProvider,
                                Validator validator) throws IOException {
        this.propertiesProvider = propertiesProvider;
        final BaseHierarchicalConfiguration configOfFile;
        configOfFile = new BaseHierarchicalConfiguration();
        configOfFile.append(ConfigurationConverter.getConfiguration(defaultProperties));
        configOfFile.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        this.defaultProperties = defaultProperties;
        this.interfaze = interfaze;

        configProxyFactory = ConfigProxyFactory
                .builder().withCustomValidationStrategy(new BeanValidatorValidationStrategy(validator)).build();

        config = configProxyFactory.make(interfaze, configOfFile);
        snapshot = config;
    }

    public void init() {
        runUpdating();
    }

    public void destroy() {
        scheduledExecutorService.shutdown();
    }

    public Properties getDefaultProperties() {
        return defaultProperties;
    }

    @Override
    public Object getConfig() {
        boolean hasResource = TransactionSynchronizationManager.hasResource(interfaze);
        if (!hasResource) {
            TransactionSynchronizationManager.bindResource(interfaze, config);
            snapshot = config;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(final boolean readOnly) {
                    super.beforeCommit(readOnly);
                    TransactionSynchronizationManager.unbindResourceIfPossible(interfaze);
                }
            });
        } else {
            TransactionSynchronizationManager.initSynchronization();
        }
        return snapshot;
    }

    void setConfig(final Object config) {
        this.config = config;
    }

    private void runUpdating() {
        Runnable command = () -> {
            try {
                Properties properties = new Properties();
                properties.putAll(getDefaultProperties());
                properties.putAll(propertiesProvider.getProperties());
                final BaseHierarchicalConfiguration configOfFile;
                configOfFile = new BaseHierarchicalConfiguration();
                configOfFile.append(ConfigurationConverter.getConfiguration(properties));
                configOfFile.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
                setConfig(configProxyFactory.make(interfaze, configOfFile));
            } catch (Throwable e) {
                logger.warn("Could not update the config: {}, cause: {}", config, e.getMessage());
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, timeUnit);
    }

    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        destroy();
        init();
    }

    public void setInitialDelay(final long initialDelay) {
        this.initialDelay = initialDelay;
        destroy();
        init();
    }

    public void setPeriod(final long period) {
        this.period = period;
        destroy();
        init();
    }
}
