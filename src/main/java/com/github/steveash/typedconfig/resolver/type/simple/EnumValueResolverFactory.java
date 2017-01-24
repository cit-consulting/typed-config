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

package com.github.steveash.typedconfig.resolver.type.simple;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.resolver.SimpleValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.github.steveash.typedconfig.util.StringUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;

/**
 * @author Steve Ash
 */
public class EnumValueResolverFactory extends SimpleValueResolverFactory {

    @Override
    public ValueResolver makeForThis(final ConfigBinding binding, final HierarchicalConfiguration config,
                                     ConfigFactoryContext context) {

        final Class enumType = binding.getDataType().getRawType();
        final String key = binding.getConfigKeyToLookup();
        return new ValueResolver() {

            @Override
            public Object resolve() {
                return StringUtils.isBlank(config.getString(key, null))
                        ? null : resolveEnumInstance(config.getString(key, null));
            }

            private Object resolveEnumInstance(String enumLabel) {
                try {
                    return Enum.valueOf(enumType, enumLabel);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("The value of the configuration key " + key + " is " +
                            enumLabel + " which is not a member of the enum " +
                            enumType.getName(), e);
                }
            }

            @Override
            public Object convertDefaultValue(String defaultValue) {
                return resolveEnumInstance(defaultValue);
            }

            @Override
            public String configurationKeyToLookup() {
                return key;
            }
        };
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        return configBinding.getDataType().getType() instanceof Class &&
                Enum.class.equals(((Class) configBinding.getDataType().getType()).getSuperclass());
    }
}
