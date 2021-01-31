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

@Getter
public class Result {

    private final List<Error> errors = new ArrayList<>();

    private final Path path;

    private final String id;

    private final String description;

    public Result(Path path, String id, String description) {
        this.path = path;
        this.id = id;
        this.description = description;
    }

    public void addError(Error.Type type, String id, String description) {
        addError(type, id, description, null);
    }

    public void addError(Error.Type type, String id, String description, Integer count) {
        this.errors.add(new Error(type, id, type.name(), count));
    }

    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
