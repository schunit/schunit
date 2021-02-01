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

package com.schunit.cli;

import com.schunit.core.SchUnitClient;
import com.schunit.core.lang.SchUnitException;
import com.schunit.core.model.Error;
import com.schunit.core.model.Result;
import com.schunit.core.util.ExtraFiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Main {

    public static void main(String... args) throws ParseException {
        Options options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("View help")
                .build());
        options.addOption(Option.builder("s")
                .longOpt("schematron")
                .desc("Schematron file/folder")
                .hasArgs()
                .build());
        options.addOption(Option.builder("t")
                .longOpt("test")
                .desc("Test file/folder")
                .hasArgs()
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
            return;
        }

        int tests = 0;
        int errors = 0;

        try (SchUnitClient client = SchUnitClient.newInstance()) {
            for (String s : cmd.getOptionValues("s")) {
                for (Path p : ExtraFiles.expand(Paths.get(s))) {
                    try {
                        log.info("Loading {}", s);
                        client.schematron(p);
                    } catch (SchUnitException e) {
                        log.error(e.getMessage(), e);
                        return;
                    }
                }
            }

            for (String t : cmd.getOptionValues("t")) {
                for (Path p : ExtraFiles.expand(Paths.get(t))) {
                    try {
                        for (Result result : client.test(p)) {
                            tests++;

                            if (result.getErrors().size() == 0) {
                                log.info("Test {} #{}", result.getPath(), result.getId());
                            } else {
                                errors++;

                                log.warn("Test {} #{}", result.getPath(), result.getId());

                                for (Error error : result.getErrors()) {
                                    log.warn("  {} / {} / {} / {}", error.getType(), error.getId(), error.getMessage(), error.getCount());
                                }
                            }
                        }
                    } catch (SchUnitException e) {
                        log.error("Unable to handle {}: {}", p, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return;
        }

        log.info("Result: {}/{}", tests - errors, tests);

        if (errors > 0)
            System.exit(1);
    }
}
