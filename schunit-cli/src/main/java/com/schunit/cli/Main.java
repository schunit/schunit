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

import com.schunit.cli.api.Runner;
import com.schunit.cli.model.Plan;
import com.schunit.cli.runner.DefaultRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class Main {

    public static void main(String... args) throws ParseException {
        // log.info("Args: {}", Arrays.toString(args));

        // Define cli arguments
        Options options = new Options()
                .addOption(Option.builder("h")
                        .longOpt("help")
                        .desc("View help")
                        .build())
                .addOption(Option.builder("s")
                        .longOpt("schematron")
                        .desc("Schematron file/folder")
                        .hasArgs()
                        .build())
                .addOption(Option.builder("t")
                        .longOpt("test")
                        .desc("Test file/folder")
                        .hasArgs()
                        .build());

        // Parse arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Print help if requested
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("schunit", options);
            return;
        }

        // Create plan
        Plan plan = new Plan(null);
        plan.addSchematron(cmd.getOptionValues("s"));
        plan.addTest(cmd.getOptionValues("t"));

        // Create runner and execute plan
        Runner runner = new DefaultRunner();
        System.exit(runner.execute(plan));
    }
}
