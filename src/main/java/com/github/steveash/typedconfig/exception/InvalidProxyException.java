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

package com.github.steveash.typedconfig.exception;

/**
 * Indicates something wrong with the proxy interface that was passed in to the factory
 *
 * @author Steve Ash
 */
public class InvalidProxyException extends RuntimeException {
    public InvalidProxyException() {
    }

    public InvalidProxyException(String message) {
        super(message);
    }

    public InvalidProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProxyException(Throwable cause) {
        super(cause);
    }
}
