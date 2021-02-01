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

package com.schunit.core.loader;

import com.google.common.base.Strings;
import com.schunit.core.api.Test;
import com.schunit.core.jaxb.v1.ExpectationType;
import com.schunit.core.jaxb.v1.TestType;
import com.schunit.core.jaxb.v1.internal.AssertType;
import com.schunit.core.jaxb.v1.internal.ResultType;
import com.schunit.core.jaxb.v1.internal.StatusType;
import com.schunit.core.model.Content;
import com.schunit.core.model.Error;
import com.schunit.core.model.Expectation;
import com.schunit.core.model.Result;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

/**
 * Test instance.
 */
class TestInstance implements Test {

    /**
     * The path to the test (set) file.
     */
    @Getter
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
     * Scope, basically rule identifiers, to limit the test to.
     */
    @Getter
    private final List<String> scope;

    private final List<ExpectationType> expectedSuccess;

    private final List<ExpectationType> expectedTrigger;

    private final boolean ignoreSuccess;

    /**
     * The content to validate when running the test.
     */
    @Getter
    private final Content content;

    public TestInstance(Path path, TestType source, Content content) {
        this.path = path;

        this.id = source.getId();
        this.description = source.getDescription();
        this.scope = source.getScope();
        this.expectedSuccess = source.getSuccess();
        this.expectedTrigger = source.getTrigger();
        this.content = content;

        this.ignoreSuccess = expectedSuccess.size() == 1 && expectedSuccess.get(0).getValue().equals("#ignore");
    }

    @Override
    public Result process(ResultType source) {
        Result result = new Result(path, id, description);

        Expectation successes = new Expectation(result, expectedSuccess);
        Expectation triggers = new Expectation(result, expectedTrigger);

        for (AssertType at : source.getAssert()) {

            // Is it possible to recognize this success/trigger?
            if (Strings.isNullOrEmpty(at.getId())) {
                if (scope.size() == 0)
                    result.addError(Error.Type.UNKNOWN_ID, null, at.getValue());

                continue;
            }

            // Are we working with a defined scope?
            if (scope.size() != 0 && !scope.contains(at.getId()))
                continue; //

            // Handle successes
            if (StatusType.SUCCESS.equals(at.getStatus())) {
                if (!ignoreSuccess)
                    successes.register(at.getId(), at.getValue(), Error.Type.UNEXPECTED_SUCCESS);

                continue;
            }

            // Handle triggers
            if (StatusType.TRIGGER.equals(at.getStatus())) {
                triggers.register(at.getId(), at.getValue(), Error.Type.UNEXPECTED_TRIGGER);

                continue;
            }

            throw new IllegalStateException("Unexpected state in validation parsing.");
        }

        // Finish of those expected but not seen
        successes.finish(Error.Type.EXPECTED_SUCCESS);
        triggers.finish(Error.Type.EXPECTED_TRIGGER);

        return result;
    }
}
