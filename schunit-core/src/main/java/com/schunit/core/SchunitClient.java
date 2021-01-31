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

package com.schunit.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.schunit.core.api.Test;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.repository.SchematronRepository;
import com.schunit.core.repository.TestRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

public class SchunitClient implements AutoCloseable {

    private static final Injector INJECTOR = Guice.createInjector();

    public static SchunitClient newInstance(Path path) {
        SchunitClient schunitClient = INJECTOR.getInstance(SchunitClient.class);
        schunitClient.path = path;

        return schunitClient;
    }

    @Inject
    private SchematronRepository schematronRepository;

    @Inject
    private TestRepository testRepository;

    private Path path;

    public void schematron(Path path) throws SchunitException, IOException {
        schematronRepository.load(path);
    }

    public void test(Path path) throws SchunitException, IOException {
        testRepository.load(path);
    }

    public void verify() {
        for (Test test : testRepository.getInstances()) {

        }
    }

    @Override
    public void close() throws Exception {
        schematronRepository.close();
        schematronRepository = null;

        testRepository.close();
        testRepository = null;
    }
}
