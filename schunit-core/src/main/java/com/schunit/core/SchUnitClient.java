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
import com.schunit.core.lang.SchUnitException;
import com.schunit.core.loader.TestLoader;
import com.schunit.core.model.Result;
import com.schunit.core.repository.SchematronRepository;
import com.schunit.core.repository.TestRepository;
import com.schunit.core.util.ExtraFiles;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * SchUnit client.
 */
public class SchUnitClient implements AutoCloseable {

    /**
     * Guice injector.
     */
    private static final Injector INJECTOR = Guice.createInjector();

    /**
     * Modes available to this client.
     */
    public enum Mode {
        BASIC,
        ADVANCED,
    }

    /**
     * Load a basic instance of the client.
     *
     * @return Basic client instance
     */
    public static SchUnitClient newInstance() {
        return newInstance(Mode.BASIC);
    }

    /**
     * Load an instance of the client.
     *
     * @param mode Client mode
     * @return Client instance
     */
    public static SchUnitClient newInstance(Mode mode) {
        SchUnitClient schunitClient = INJECTOR.getInstance(SchUnitClient.class);
        schunitClient.mode = mode;

        return schunitClient;
    }

    /**
     * Repository of loaded Schematron files.
     */
    @Inject
    private SchematronRepository schematronRepository;

    /**
     * Repository of loaded tests. Used in advanced mode only.
     */
    @Inject
    private TestRepository testRepository;

    /**
     * Loader of tests. Used in basic mode only.
     */
    @Inject
    private TestLoader testLoader;

    /**
     * Client mode.
     */
    private Mode mode;

    /**
     * Load schematron.
     *
     * @param path Path to Schematron file or folder of Schematron files.
     * @return Test results when used in advanced mode, otherwise empty list.
     * @throws SchUnitException Exceptions related to loading of Schematron instance(s).
     * @throws IOException      Exceptions related to IO.
     */
    public List<Result> schematron(Path path) throws SchUnitException, IOException {
        for (Path p : ExtraFiles.expand(path))
            schematronRepository.load(p);

        if (Mode.ADVANCED.equals(mode))
            return verify();

        return Collections.emptyList();
    }

    /**
     * Load test(s).
     *
     * @param path Path to test file or folder of test files.
     * @return Test results.
     * @throws SchUnitException Exceptions related to loading of test instance(s).
     * @throws IOException      Exceptions related to IO.
     */
    public List<Result> test(Path path) throws SchUnitException, IOException {
        List<Result> results = new ArrayList<>();

        for (Path p : ExtraFiles.expand(path)) {
            switch (mode) {
                case BASIC:
                    results.addAll(schematronRepository.validate(testLoader.load(p)));
                    break;
                case ADVANCED:
                    results.addAll(schematronRepository.validate(testRepository.load(p)));
                    break;
            }
        }

        return results;
    }

    /**
     * Verify content in repositories.
     *
     * @return Test results.
     * @throws SchUnitException Exceptions related to validation and verification of test instance(s).
     */
    public List<Result> verify() throws SchUnitException {
        if (mode.equals(Mode.BASIC))
            throw new SchUnitException("Not supported in basic mode.");

        return schematronRepository.validate(testRepository.getInstances());
    }

    public static String version() {
        try {
            Properties prop = new Properties();
            prop.load(SchUnitClient.class.getClassLoader().getResourceAsStream("version.properties"));

            return prop.getProperty("version");
        } catch (IOException e) {
            return "Unknown";
        }
    }

    /**
     * Clear repositories.
     */
    @Override
    public void close() {
        schematronRepository.close();
        schematronRepository = null;

        testRepository.close();
        testRepository = null;
    }
}
