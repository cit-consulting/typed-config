package com.github.steveash.typedconfig.resolver;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.temp.Source;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by dmitrijrudenko on 24.01.17.
 */
public class Util {
    private final ConfigFactoryContext context;
    private final ConfigBinding parentBinding;

    public Util(Class interfaze, ConfigProxyFactory configProxyFactory) {
        this.context = configProxyFactory.getContext();
        this.parentBinding = ConfigBinding.makeRootBinding(TypeToken.of(interfaze));
    }

    public Map<Method, Object> getMapMethods(Source source) throws NoSuchMethodException {

        ImmutableMap.Builder<Method, Object> builder = ImmutableMap.builder();
        Class interfaze = source.getProxyClass();
        for (Method method : interfaze.getDeclaredMethods()) {
            builder.put(method, makeResolverForMethod(interfaze, method, source.getBaseHierarchicalConfiguration()));
        }

        ImmutableMap<Method, Object> resolverMap = builder.build();
        ValueResolver hashResolver = makeHashResolver(interfaze, resolverMap);
        ValueResolver toStringResolver = makeToStringResolver(interfaze, resolverMap);
        ValueResolver getIfaceResolver = new InstanceValueResolver(interfaze);
        ValueResolver getResolversResolver = new InstanceValueResolver(resolverMap);

        Method hashMethod = Object.class.getDeclaredMethod("hashCode");
        Method toStringMethod = Object.class.getDeclaredMethod("toString");
        Method getIfaceMethod = ProxiedConfiguration.class.getDeclaredMethod("getInterfaceClass");
        Method getResolversMethod = ProxiedConfiguration.class.getDeclaredMethod("getResolvers");

        ImmutableMap.Builder<Method, Object> build = ImmutableMap.builder();
        build.putAll(resolverMap);
        build.put(hashMethod, hashResolver.resolve());
        build.put(toStringMethod, toStringResolver.resolve());
        build.put(getIfaceMethod, getIfaceResolver.resolve());
        build.put(getResolversMethod, getResolversResolver.resolve());
        return build.build();
    }

    private Object makeResolverForMethod(Class<?> interfaze, Method method, HierarchicalConfiguration config) {

        ConfigBinding newMethodBinding = context.getBindingFor(interfaze, method, config);
        return makeResolverForBinding(newMethodBinding, config);
    }

    private Object makeResolverForBinding(ConfigBinding binding,
                                          HierarchicalConfiguration config) {
        return context.makeResolverForBinding(config, binding, parentBinding);
    }

    private ValueResolver makeToStringResolver(Class<?> interfaze, ImmutableMap<Method, Object> resolverMap) {
        return new ToStringResolver(interfaze, resolverMap);

    }

    private ValueResolver makeHashResolver(Class<?> interfaze, ImmutableMap<Method, Object> resolverMap) {
        return new HashCodeResolver(interfaze, resolverMap);
    }
}
