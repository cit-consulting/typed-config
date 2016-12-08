package com.github.steveash.typedconfig.resolver;

import com.github.steveash.typedconfig.ConfigBinding;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Steve Ash
 */
public class ValueResolverRegistryTest {

    @Test
    public void shouldReturnFactoryThatCanHandleType() throws Exception {
        ValueResolverFactory factory = mock(ValueResolverFactory.class);
        ConfigBinding configBinding = ConfigBinding.makeShimForKey("");
        given(factory.canResolveFor(configBinding)).willReturn(true);
        ValueResolverRegistry registry = new ValueResolverRegistry(
                Collections.singletonList(factory));

        assertNotNull(registry.lookup(configBinding));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoFactoryCanHandleType() throws Exception {
        ValueResolverFactory factory = mock(ValueResolverFactory.class);
        ConfigBinding binding = mock(ConfigBinding.class);
        given(factory.canResolveFor(binding)).willReturn(false);
        ValueResolverRegistry registry = new ValueResolverRegistry(
                Collections.singletonList(factory));

        assertNotNull(registry.lookup(binding));
    }
}