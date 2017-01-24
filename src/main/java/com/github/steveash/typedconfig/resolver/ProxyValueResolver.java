/*
 * Copyright (c) 2012 Jonathan Tyers, Steve Ash
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.steveash.typedconfig.resolver;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map.Entry;

/**
 * Resolves a proxy for a given interface type
 *
 * @author Steve Ash
 */
public class ProxyValueResolver implements ValueResolver, ValueResolverForBindingFactory {

    private final ConfigBinding parentBinding;
    private final HierarchicalConfiguration config;
    private final ConfigFactoryContext context;

    public ProxyValueResolver(ConfigBinding binding, HierarchicalConfiguration config, ConfigFactoryContext context) {
        this.parentBinding = binding;
        this.config = config;
        this.context = context;
    }

    @Override
    public Object resolve() {
        return make(parentBinding.getDataType().getRawType(), config);
    }

    @Override
    public Object convertDefaultValue(String defaultValue) {
        throw new IllegalStateException("cannot use a default value on a proxy interface");
    }

    @Override
    public String configurationKeyToLookup() {
        return parentBinding.getConfigKeyToLookup();
    }

    private <T> T make(Class<T> interfaze, HierarchicalConfiguration configuration) {
        try {
            return tryToMake(interfaze, configuration);
        } catch (NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }

    private <T> T tryToMake(Class<T> interfaze, HierarchicalConfiguration configuration) throws NoSuchMethodException {
        Builder<Method, Object> builder = ImmutableMap.builder();
        for (Method method : interfaze.getDeclaredMethods()) {
            builder.put(method, makeResolverForMethod(interfaze, method, configuration));
        }
        return makeProxyForResolvers(interfaze, builder.build(), configuration);
    }

    private Object makeResolverForMethod(Class<?> interfaze, Method method, HierarchicalConfiguration config) {

        ConfigBinding newMethodBinding = context.getBindingFor(interfaze, method, config);
        return makeResolverForBinding(newMethodBinding, interfaze, method, config);
    }

    public Object makeResolverForBinding(ConfigBinding binding, Class<?> interfaze, Method method,
                                         HierarchicalConfiguration config) {
        return context.makeResolverForBinding(config, binding, parentBinding);
    }

    @SuppressWarnings("unchecked")
    private <T> T makeProxyForResolvers(final Class<?> interfaze,
                                        final ImmutableMap<Method, Object> propertyResolvers, HierarchicalConfiguration configuration) throws NoSuchMethodException {

        final ImmutableMap<Method, Object> allResolvers = addInternalResolvers(interfaze,
                propertyResolvers);

        final Method equalsMethod = Object.class.getDeclaredMethod("equals", Object.class);

        InvocationHandler handler = (proxy, method, args) -> {

            Object value = allResolvers.get(method);
            if (value != null) {
                return value;
            }
            if (equalsMethod.equals(method)) {
                return ProxyValueResolver.this.proxyEquals(interfaze, propertyResolvers, args[0]);
            }

            throw new IllegalStateException("no method is known for " + method);
        };
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{interfaze, ProxiedConfiguration.class}, handler);
    }

    private ImmutableMap<Method, Object> addInternalResolvers(Class<?> interfaze,
                                                              ImmutableMap<Method, Object> resolverMap) throws NoSuchMethodException {

        ValueResolver hashResolver = makeHashResolver(interfaze, resolverMap);
        ValueResolver toStringResolver = makeToStringResolver(interfaze, resolverMap);
        ValueResolver getIfaceResolver = new InstanceValueResolver(interfaze);
        ValueResolver getResolversResolver = new InstanceValueResolver(resolverMap);

        Method hashMethod = Object.class.getDeclaredMethod("hashCode");
        Method toStringMethod = Object.class.getDeclaredMethod("toString");
        Method getIfaceMethod = ProxiedConfiguration.class.getDeclaredMethod("getInterfaceClass");
        Method getResolversMethod = ProxiedConfiguration.class.getDeclaredMethod("getResolvers");

        Builder<Method, Object> builder = ImmutableMap.builder();
        builder.putAll(resolverMap);
        builder.put(hashMethod, hashResolver.resolve());
        builder.put(toStringMethod, toStringResolver.resolve());
        builder.put(getIfaceMethod, getIfaceResolver.resolve());
        builder.put(getResolversMethod, getResolversResolver.resolve());
        return builder.build();
    }

    private ValueResolver makeToStringResolver(Class<?> interfaze, ImmutableMap<Method, Object> resolverMap) {
        return new ToStringResolver(interfaze, resolverMap);

    }

    private ValueResolver makeHashResolver(Class<?> interfaze, ImmutableMap<Method, Object> resolverMap) {
        return new HashCodeResolver(interfaze, resolverMap);
    }

    private boolean proxyEquals(Class<?> thisIface, ImmutableMap<Method, Object> thisResolvers, Object that) {
        if (!(that instanceof ProxiedConfiguration)) {
            return false;
        }
        ProxiedConfiguration thatConfig = ((ProxiedConfiguration) that);

        Class<?> thatIface = thatConfig.getInterfaceClass();
        if (!thisIface.equals(thatIface)) {
            return false;
        }

        ImmutableMap<Method, ValueResolver> thatResolvers = thatConfig.getResolvers();
        if (thisResolvers.size() != thatResolvers.size()) {
            throw new IllegalStateException("not sure how the same iface can have different resolver map");
        }

        for (Entry<Method, Object> thisEntry : thisResolvers.entrySet()) {
            Object thisValue = thisEntry.getValue();
            Object thatValue = thatResolvers.get(thisEntry.getKey());
            if (!thisValue.equals(thatValue)) {
                return false;
            }
        }
        return true;
    }
}