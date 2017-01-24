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

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class ToStringResolverTest {

    @Test
    public void testResolve() throws Exception {
        ToStringResolver resolver = new ToStringResolver(TestIface.class,
                ImmutableMap.of(
                        TestIface.class.getDeclaredMethod("getA"), new InstanceValueResolver(42).resolve(),
                        TestIface.class.getDeclaredMethod("getB"), new InstanceValueResolver("Steve").resolve()));

        assertEquals("TestIface[getA=42,getB=Steve]", resolver.resolve());
    }

    public interface TestIface {
        int getA();

        String getB();
    }
}
