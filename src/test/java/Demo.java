import com.github.steveash.typedconfig.temp.Config;
import com.github.steveash.typedconfig.temp.DynamicConfig;
import com.github.steveash.typedconfig.temp.FixedPollingStrategy;
import com.github.steveash.typedconfig.temp.PollingStrategy;
import com.github.steveash.typedconfig.temp.ReloadableConfig;
import com.github.steveash.typedconfig.temp.SnapshotConfig;
import com.github.steveash.typedconfig.temp.Source;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

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

        Config snapshotConfig =
                new SnapshotConfig<>(Proxy.class, defaultConfiguration);

        Proxy singleProxy = (Proxy) snapshotConfig.getProxy();
        System.out.println(singleProxy.getName());
        System.out.println(singleProxy.getAge());

        Source source = () -> {
            Map<String, Object> props = new HashMap<>();
            props.put("name", "Dima");
            props.put("age", (new Random().nextInt(11) + 10)); // от 10 до 20 (включительно));

            BaseHierarchicalConfiguration configuration;
            configuration = new BaseHierarchicalConfiguration();
            configuration
                    .append(ConfigurationConverter
                            .getConfiguration(ConfigurationConverter
                                    .getProperties(new MapConfiguration(props))));
            configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
            return configuration;
        };
        PollingStrategy pollingStrategy = new FixedPollingStrategy(1, TimeUnit.SECONDS);

        ReloadableConfig reloadableConfig = new DynamicConfig<>(Proxy.class, defaultConfiguration, source, pollingStrategy);
        Proxy dynamicProxy = (Proxy) reloadableConfig.getProxy();
        System.out.println(dynamicProxy.getName());
        System.out.println(dynamicProxy.getAge());

        for (int i = 0; i < 100; i++) {
            Thread.sleep(500);
            dynamicProxy = (Proxy) reloadableConfig.getProxy();
            System.out.println(i + " : " + dynamicProxy.getName());
            System.out.println(i + " : " + dynamicProxy.getAge());
        }
    }

    public interface Proxy {
        String getName();

        Integer getAge();
    }
}
