/*
 * Copyright 2021 Erlend Klakegg Bergheim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.schunit.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A single error detected as part of testing.
 */
@Getter
@AllArgsConstructor
public class Error {

    /**
     * Error types.
     */
    public enum Type {
        UNKNOWN_ID,
        UNEXPECTED_TRIGGER,
        UNEXPECTED_SUCCESS,
        EXPECTED_TRIGGER,
        EXPECTED_SUCCESS,
    }

    /**
     * Error type.
     */
    private final Type type;

    /**
     * Identifier of the validation rule causing the error.
     */
    private final String id;

    /**
     * The validation rule message if available.
     */
    private final String message;

    /**
     * The amount of times this error occurs.
     */
    private final Integer count;

}
