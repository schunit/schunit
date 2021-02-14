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

package com.schunit.cli.runner;

import com.schunit.cli.api.Runner;
import com.schunit.cli.model.Properties;
import com.schunit.cli.model.Plan;
import com.schunit.core.SchUnitClient;
import com.schunit.core.lang.SchUnitException;
import com.schunit.core.model.Error;
import com.schunit.core.model.Result;
import com.schunit.core.util.ExtraFiles;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class DefaultRunner implements Runner {

    private final Properties properties;

    private int tests;

    private int errors;

    public DefaultRunner(Properties properties) {
        this.properties = properties;
    }

    @Override
    public int execute(Plan plan) {
        log.info("Project: {}", plan.getName());

        // Reset counters
        tests = 0;
        errors = 0;

        try (SchUnitClient client = SchUnitClient.newInstance()) {
            for (Path s : plan.getSchematrons()) {
                for (Path p : ExtraFiles.expand(s)) {
                    try {
                        log.info("  Loading {}", plan.subpath(p));
                        client.schematron(p);
                    } catch (SchUnitException e) {
                        log.error(e.getMessage(), e);
                        return 1;
                    }
                }
            }

            for (Path t : plan.getTests()) {
                for (Path p : ExtraFiles.expand(t)) {
                    try {
                        handleResults(plan, client.test(p));
                    } catch (SchUnitException e) {
                        log.error("  Unable to handle {}: {}", p, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error("  {}", e.getMessage());
            return 1;
        }

        log.info("  Result: {}/{}", tests - errors, tests);

        return errors > 0 || tests == 0 ? 1 : 0;
    }

    private void handleResults(Plan plan, List<Result> results) {
        for (Result result : results) {
            tests++;

            if (result.getErrors().size() == 0) {
                if (properties.isVerbose())
                    log.info("  Test {} #{}", plan.subpath(result.getPath()), result.getId());
            } else {
                errors++;

                log.warn("  Test {} #{}", result.getPath(), result.getId());

                for (Error error : result.getErrors()) {
                    log.warn("    {} / {} / {} / {}", error.getType(), error.getId(), error.getMessage(), error.getCount());
                }
            }
        }
    }
}
