import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.Option;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;


public class ConfigProxyDemo {
    public static void main(final String[] args) throws ConfigurationException {
        CarConfiguration configuration = ConfigProxyFactory.getDefault()
                .make(CarConfiguration.class, (HierarchicalConfiguration) new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().properties()
                                .setFileName("car.xml")));

        System.out.println("Building a '" + configuration.getName() +
                "' with " + configuration.getDoors() + " doors");

        if (configuration.hasAirConditioning()) {
            System.out.println("This car has airconditioning");
        }
    }

    @ConfigProxy
    static interface CarConfiguration {
        @Config(value = "doors", defaultValue = "4")
        int getDoors();

        @Config(value = "[@name]")
        String getName();

        @Config(value = "air-conditioning", options = Option.CHECK_KEY_EXISTS)
        boolean hasAirConditioning();
    }
}
