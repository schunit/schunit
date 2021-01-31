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
import com.schunit.core.jaxb.v1.internal.ResultType;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.loader.SchematronLoader;
import com.schunit.core.model.Content;
import com.schunit.core.model.Result;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Schematron instances.
 */
public class SchematronRepository implements AutoCloseable {

    /**
     * Loader used to load Schematron instances.
     */
    @Inject
    private SchematronLoader loader;

    /**
     * Instances part of repository.
     */
    private final List<Schematron> instances = new ArrayList<>();

    public void load(Path path) throws SchunitException {
        instances.add(loader.load(path));
    }

    public List<Result> validate(List<Test> tests) throws SchunitException {
        List<Result> results = new ArrayList<>();

        for (Test test : tests)
            results.add(validate(test));

        return results;
    }

    public Result validate(Test test) throws SchunitException {
        return test.process(forTest(test).validate(test.getContent()));
    }

    /**
     * Load Schematron instances relevant to a given test.
     *
     * @param test Test to be found Schematron instances for.
     * @return Schematron instance wrapping relevant Schematron instances.
     */
    private Schematron forTest(Test test) {
        List<String> scope = test.getScope();

        if (scope.size() == 0)
            return new SchematronGroup(instances);

        return new SchematronGroup(instances.stream()
                .filter(s -> s.getRules().stream().anyMatch(scope::contains))
                .collect(Collectors.toList()));
    }

    /**
     * Clear repository instances.
     */
    @Override
    public void close() {
        instances.clear();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class SchematronGroup implements Schematron {

        private final List<Schematron> schematrons;

        public SchematronGroup(List<Schematron> schematrons) {
            this.schematrons = schematrons;
        }

        @Override
        public Path getPath() {
            throw new IllegalStateException("Path is not provided for a group of Schematron instances");
        }

        @Override
        public List<String> getRules() {
            return schematrons.stream()
                    .map(Schematron::getRules)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        @Override
        public ResultType validate(Content content) throws SchunitException {
            ResultType resultType = new ResultType();

            for (Schematron schematron : schematrons)
                resultType.getAssert().addAll(schematron.validate(content).getAssert());

            return resultType;
        }
    }
}
