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

package com.github.steveash.typedconfig.resolver.type;

import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.resolver.ProxyValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueType;

/**
 * @author Steve Ash
 */
public class ProxyValueResolverFactory implements ValueResolverFactory {
    @Override
    public ValueResolver makeForThis(final ConfigBinding binding, final HierarchicalConfiguration config,
                                     final ConfigFactoryContext context) {
        return new ProxyValueResolver(binding, config, context);
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        return configBinding.getDataType().getRawType().isInterface();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.Nested;
    }
}
