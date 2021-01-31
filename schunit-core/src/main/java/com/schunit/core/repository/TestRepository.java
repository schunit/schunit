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

import com.schunit.core.api.Test;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.loader.TestLoader;
import lombok.Getter;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for test instances.
 */
public class TestRepository implements AutoCloseable {

    /**
     * Loader used to load test instances.
     */
    @Inject
    private TestLoader loader;

    /**
     * Instances part of repository.
     */
    @Getter
    private final List<Test> instances = new ArrayList<>();

    /**
     * Load test(s) from test file.
     *
     * @param path Path to file containing test(s).
     * @return Loaded test instances.
     * @throws SchunitException Exceptions related to loading of test instance(s).
     */
    public List<Test> load(Path path) throws SchunitException {
        List<Test> result = loader.load(path);

        instances.addAll(result);

        return result;
    }

    /**
     * Clear repository instances.
     */
    @Override
    public void close() {
        instances.clear();
    }
}
