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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Steve Ash
 */
public class BeanValidatorValidationStrategy implements ValidationStrategy {

    private final Validator validator;

    private BeanValidatorValidationStrategy(Validator validator) {
        this.validator = validator;
    }

    public BeanValidatorValidationStrategy() {
        this(Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Override
    public Object validate(Object object) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException("The configuration " + object.getClass().getName()
                    + " returned value [" + object + "] which failed the validation constraints: "
                    + constraintViolations.toString(), constraintViolations);
        }
        return object;
    }
}
