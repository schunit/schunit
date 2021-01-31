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

package com.schunit.core.repository;

import com.schunit.core.api.Schematron;
import com.schunit.core.api.Test;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.loader.SchematronLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SchematronRepository implements AutoCloseable {

    @Inject
    private SchematronLoader loader;

    private final List<Schematron> instances = new ArrayList<>();

    private List<Schematron> getFor(Test test) {
        List<String> scope = test.getScope();

        if (scope.size() == 0)
            return Collections.unmodifiableList(instances);

        return instances.stream()
                .filter(s -> s.getRules().stream().anyMatch(scope::contains))
                .collect(Collectors.toList());
    }

    public void load(Path path) throws SchunitException, IOException {
        if (Files.isReadable(path)) {
            loadFile(path);
        } else {
            for (Path p : Files.list(path).sorted().collect(Collectors.toList()))
                loadFile(p);
        }
    }

    private void loadFile(Path path) throws SchunitException {
        instances.add(loader.load(path));
    }

    @Override
    public void close() {
        instances.clear();
    }
}
