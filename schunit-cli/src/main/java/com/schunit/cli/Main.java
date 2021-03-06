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
import com.schunit.cli.loader.ConfigLoader;
import com.schunit.cli.model.Plan;
import com.schunit.cli.model.Properties;
import com.schunit.cli.runner.DefaultRunner;
import com.schunit.core.SchUnitClient;
import com.schunit.core.lang.SchUnitException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String... args) throws ParseException {
        // Define cli arguments
        Options options = loadOptions();

        // Parse arguments
        CommandLine cmd = parseArgs(options, args);

        // Print help if requested
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("schunit", options);
            return;
        }

        // Print version information
        if (cmd.hasOption("version")) {
            System.out.printf("SchUnit %s%n", SchUnitClient.version());
            return;
        }

        try {
            System.exit(run(cmd));
        } catch (SchUnitException e) {
            log.error(e.getMessage(), e);
            System.exit(2);
        }
    }

    public static CommandLine parseArgs(Options options, String... args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public static Options loadOptions() {
        return new Options()
                .addOption(Option.builder("h")
                        .longOpt("help")
                        .desc("View help")
                        .build())
                .addOption(Option.builder("p")
                        .longOpt("Path")
                        .desc("Project path")
                        .hasArg()
                        .build())
                .addOption(Option.builder("r")
                        .longOpt("recursive")
                        .desc("Look for .schunit.yaml recursively")
                        .build())
                .addOption(Option.builder()
                        .longOpt("runner")
                        .desc("Override runner")
                        .hasArg()
                        .build())
                .addOption(Option.builder("s")
                        .longOpt("schematron")
                        .desc("Schematron file/folder")
                        .hasArgs()
                        .build())
                .addOption(Option.builder("v")
                        .longOpt("verbose")
                        .desc("Verbose")
                        .build())
                .addOption(Option.builder("t")
                        .longOpt("test")
                        .desc("Test file/folder")
                        .hasArgs()
                        .build())
                .addOption(Option.builder()
                        .longOpt("version")
                        .desc("Display version")
                        .build());
    }

    public static int run(CommandLine cmd) throws SchUnitException {
        // Create config
        Properties properties = new Properties();
        properties.setVerbose(cmd.hasOption("verbose"));

        // Load plans
        List<Plan> plans;

        if (cmd.hasOption("r")) {
            // Detect and load all .schunit.yaml files recursively
            plans = ConfigLoader.recursiveLoad(Paths.get(cmd.getOptionValue("p", "")));
        } else {
            if (cmd.hasOption("s")) {
                // Schematron is set, to make a plan based on arguments.
                Plan plan = new Plan(null);
                plan.addSchematron(cmd.getOptionValues("s"));
                plan.addTest(cmd.getOptionValues("t"));

                plans = Collections.singletonList(plan);
            } else {
                // Load .schunit.yaml found in current folder
                plans = ConfigLoader.load(Paths.get(cmd.getOptionValue("p", "")));
            }
        }

        // Make sure loading was successful
        if (plans.isEmpty())
            throw new SchUnitException("Found no configuration.");

        // Create and execute runner
        Runner runner = loadRunner(cmd, properties);
        return runner.execute(plans);
    }

    private static Runner loadRunner(CommandLine cmd, Properties properties) throws SchUnitException {
        if (cmd.hasOption("runner")) {
            try {
                return (Runner) Class.forName(cmd.getOptionValue("runner"))
                        .getDeclaredConstructor(Properties.class)
                        .newInstance(properties);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new SchUnitException(String.format("Unable to load runner '%s'.", cmd.getOptionValue("runner")), e);
            }
        } else {
            return new DefaultRunner(properties);
        }
    }
}
