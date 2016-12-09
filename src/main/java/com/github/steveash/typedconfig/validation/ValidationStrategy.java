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

package com.github.steveash.typedconfig.validation;

import com.github.steveash.typedconfig.resolver.ValueResolver;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;

/**
 * @author Steve Ash
 */
public interface ValidationStrategy {

    @Deprecated
    ValueResolver decorateForValidation(ValueResolver resolver, Class<?> interfaze, Method method);

    /**
     * Validates all constraints on {@code object}.
     *
     * @param object object to validate
     * @return the object if the validation is successful
     * @throws ConstraintViolationException if the validation is not passed
     */
    Object validate(Object object);
}
