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

import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of the result of a given test.
 */
@Getter
public class Result {

    /**
     * Errors discovered when running the test.
     */
    private final List<Error> errors = new ArrayList<>();

    /**
     * The path to the test (set) file.
     */
    private final Path path;

    /**
     * Internal identifier of the test in a test set.
     */
    private final String id;

    /**
     * Test description as made available in the test.
     */
    private final String description;

    /**
     * Simple initiation based upon a defined test.
     *
     * @param path        Path of where to find the test.
     * @param id          Internal identifier of the test in the test file.
     * @param description Description of the given test.
     */
    public Result(Path path, String id, String description) {
        this.path = path;
        this.id = id;
        this.description = description;
    }

    public void addError(Error.Type type, String id, String message) {
        addError(type, id, message, null);
    }

    public void addError(Error.Type type, String id, String message, Integer count) {
        this.errors.add(new Error(type, id, message, count));
    }

    /**
     * Get errors generated when running the test.
     *
     * @return List of errors.
     */
    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
