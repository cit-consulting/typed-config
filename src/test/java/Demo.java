import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.temp.DynamicConfig;
import com.github.steveash.typedconfig.temp.FixedPollingStrategy;
import com.github.steveash.typedconfig.temp.PollingStrategy;
import com.github.steveash.typedconfig.temp.SnapshotConfig;
import com.github.steveash.typedconfig.temp.Source;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Demo {
    public static void main(String[] args) throws InterruptedException {
        Properties properties = new Properties();
        properties.put("name", "Dima");
        properties.put("age", "27");


        BaseHierarchicalConfiguration defaultConfiguration;
        defaultConfiguration = new BaseHierarchicalConfiguration();
        defaultConfiguration.append(ConfigurationConverter.getConfiguration(properties));
        defaultConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

        SnapshotConfig<Proxy> snapshotConfig = new SnapshotConfig<>(Proxy.class, defaultConfiguration);

        Proxy singleProxy = snapshotConfig.getProxy();
        System.out.println("singleProxy" + " : " + singleProxy.getName());
        System.out.println("singleProxy" + " : " + singleProxy.getAge());

        TestDynamicSource<Proxy> source = new TestDynamicSource<>(Proxy.class);
        DynamicConfig<Proxy> simpleDynamicConfig = new DynamicConfig<>(source);
        Proxy simpleDynamicProxy = simpleDynamicConfig.getProxy();
        System.out.println("simpleDynamicProxy" + " : " + simpleDynamicProxy.getName());
        System.out.println("simpleDynamicProxy" + " : " + simpleDynamicProxy.getAge());

        System.out.println("simpleDynamicProxy" + " : " + simpleDynamicProxy.getName());
        System.out.println("simpleDynamicProxy" + " : " + simpleDynamicProxy.getAge());

        PollingStrategy pollingStrategy = new FixedPollingStrategy(1, TimeUnit.SECONDS);

        TestAutoReloadableSource<Proxy> autoReloadableSource = new TestAutoReloadableSource<>(Proxy.class, pollingStrategy);
        DynamicConfig<Proxy> dynamicConfig = new DynamicConfig<>(autoReloadableSource);
        Proxy dynamicProxy = dynamicConfig.getProxy();
        System.out.println("dynamicProxy" + " : " + dynamicProxy.getName());
        System.out.println("dynamicProxy" + " : " + dynamicProxy.getAge());

        for (int i = 0; i < 100; i++) {
            Thread.sleep(500);
//            dynamicProxy = dynamicConfig.getProxy();
            System.out.println("dynamicProxy" + " : " + i + " : " + dynamicProxy.getName());
            System.out.println("dynamicProxy" + " : " + i + " : " + dynamicProxy.getAge());
        }
    }

    public interface Proxy {
        String getName();

        //        @Range(min = 18, max = 60)
        Integer getAge();
    }

    public static class TestDynamicSource<E> implements Source<E> {
        private final Class<E> interfaze;
        private ImmutableMap<Method, Object> methodObjectImmutableMap;
        private BaseHierarchicalConfiguration configuration;

        TestDynamicSource(Class<E> interfaze) {
            this.interfaze = interfaze;
            this.configuration = getBaseHierarchicalConfiguration();
        }

        @Override
        public Class<E> getProxyClass() {
            return interfaze;
        }

        @Override
        public BaseHierarchicalConfiguration getBaseHierarchicalConfiguration() {
            Map<String, Object> props = new HashMap<>();
            props.put("name", "Dima");
            props.put("age", (new Random().nextInt(100) + 10));

            BaseHierarchicalConfiguration configuration;
            configuration = new BaseHierarchicalConfiguration();
            configuration
                    .append(ConfigurationConverter
                            .getConfiguration(ConfigurationConverter
                                    .getProperties(new MapConfiguration(props))));
            configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
            try {
                ConfigProxyFactory.getDefault().make(interfaze, configuration);
                this.configuration = configuration;
            } catch (Exception e) {
                if (this.configuration == null) {
                    throw new RuntimeException("Невалидная конфигурация!!!!", e);
                }

                System.err.println("Невалидная конфигурация!!!!");
            }
            return this.configuration;
        }

        @Override
        public void bind(ImmutableMap<Method, Object> methodObjectImmutableMap) {
            this.methodObjectImmutableMap = methodObjectImmutableMap;
        }

        @Override
        public Object getValue(Method method) {
            return methodObjectImmutableMap.get(method);
        }
    }

    public static class TestAutoReloadableSource<E> implements Source<E> {
        private final Class<E> interfaze;
        private final PollingStrategy pollingStrategy;
        private BaseHierarchicalConfiguration configuration;
        private ImmutableMap<Method, Object> methodObjectImmutableMap;


        TestAutoReloadableSource(Class<E> interfaze, PollingStrategy pollingStrategy) {
            this.interfaze = interfaze;
            this.configuration = getBaseHierarchicalConfiguration();
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
        public void bind(ImmutableMap<Method, Object> methodObjectImmutableMap) {
            this.methodObjectImmutableMap = methodObjectImmutableMap;
        }

        @Override
        public Object getValue(Method method) {
            return methodObjectImmutableMap.get(method);
        }

        @Override
        public Class<E> getProxyClass() {
            return interfaze;
        }

        @Override
        public BaseHierarchicalConfiguration getBaseHierarchicalConfiguration() {
            return configuration;
        }

        private void reload() {
            Map<String, Object> props = new HashMap<>();
            props.put("name", "Dima");
            props.put("age", (new Random().nextInt(100) + 10));

            BaseHierarchicalConfiguration configuration;
            configuration = new BaseHierarchicalConfiguration();
            configuration
                    .append(ConfigurationConverter
                            .getConfiguration(ConfigurationConverter
                                    .getProperties(new MapConfiguration(props))));
            configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
            try {
                ConfigProxyFactory.getDefault().make(interfaze, configuration);
                this.configuration = configuration;
            } catch (Exception e) {
                if (this.configuration == null) {
                    throw new RuntimeException("Невалидная конфигурация!!!!", e);
                }

                System.err.println("Невалидная конфигурация!!!!");
            }
        }
    }
}
