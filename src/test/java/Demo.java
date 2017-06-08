import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.resolver.Util;
import com.github.steveash.typedconfig.temp.FixedPollingStrategy;
import com.github.steveash.typedconfig.temp.PollingStrategy;
import com.github.steveash.typedconfig.temp.Source;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.hibernate.validator.constraints.Range;

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
        properties.put("child.name", "Oleg");
        properties.put("child.age", "2");

        BaseHierarchicalConfiguration defaultConfiguration;
        defaultConfiguration = new BaseHierarchicalConfiguration();
        defaultConfiguration.append(ConfigurationConverter.getConfiguration(properties));
        defaultConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        ConfigProxyFactory configProxyFactory = ConfigProxyFactory.builder().build();
        Proxy singleProxy = configProxyFactory.make(Proxy.class, defaultConfiguration);
        System.out.println("singleProxy" + " : " + singleProxy);

        PollingStrategy pollingStrategy = new FixedPollingStrategy(1, TimeUnit.SECONDS);
        TestAutoReloadableSource<Proxy> autoReloadableSource = new TestAutoReloadableSource<>(Proxy.class, pollingStrategy, configProxyFactory);
        Proxy dynamicProxy = ConfigProxyFactory.builder().build().makeDynamic(autoReloadableSource);

        for (int i = 0; i < 100; i++) {
            System.out.println("dynamicProxy" + " : " + i + " : " + dynamicProxy);
            Thread.sleep(500);
        }
    }

    public interface Proxy {
        String getName();

        @Range(min = 18, max = 60)
        Integer getAge();

        Child getChild();
    }

    public interface Child {
        String getName();

        Integer getAge();
    }

    public static class TestAutoReloadableSource<E> implements Source<E> {
        private final Class<E> interfaze;
        private final PollingStrategy pollingStrategy;
        private final Util util;
        private BaseHierarchicalConfiguration configuration;
        private ImmutableMap<Method, Object> methodObjectImmutableMap;

        TestAutoReloadableSource(Class<E> interfaze, PollingStrategy pollingStrategy, ConfigProxyFactory configProxyFactory) {
            this.interfaze = interfaze;
            this.configuration = getBaseHierarchicalConfiguration();
            this.pollingStrategy = pollingStrategy;

            util = new Util(interfaze, configProxyFactory);

            this.pollingStrategy.execute(() -> {
                try {
                    reload();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to poll configuration", e);
                }
            });
        }

        @Override
        public void bind(Map<Method, Object> methodObjectImmutableMap) {
            this.methodObjectImmutableMap = ImmutableMap.copyOf(methodObjectImmutableMap);
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

            props.put("child.name", "Oleg");
            props.put("child.age", (new Random().nextInt(100) + 10));

            BaseHierarchicalConfiguration configuration;
            configuration = new BaseHierarchicalConfiguration();
            configuration
                    .append(ConfigurationConverter
                            .getConfiguration(ConfigurationConverter
                                    .getProperties(new MapConfiguration(props))));
            configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
            try {
                ConfigProxyFactory.builder().build().make(interfaze, configuration);
                this.configuration = configuration;
                methodObjectImmutableMap = ImmutableMap.copyOf(util.getMapMethods(this));

            } catch (Exception e) {
                if (this.configuration == null) {
                    throw new RuntimeException("Невалидная конфигурация!!!!", e);
                }

                System.err.println("Невалидная конфигурация!!!!");
            }
        }
    }
}
