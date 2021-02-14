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

package com.schunit.cli.loader;

import com.schunit.cli.model.Plan;
import com.schunit.core.lang.SchUnitException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigLoader {

    private static final String FILENAME = ".schunit.yaml";

    private static final Representer REPRESENTER = new Representer() {{
        getPropertyUtils().setSkipMissingProperties(true);
    }};

    private static final Yaml YAML = new Yaml(REPRESENTER);

    public static List<Plan> recursiveLoad(Path path) throws SchUnitException {
        try {
            List<Plan> plans = new ArrayList<>();

            for (Path p : Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> FILENAME.equals(p.toFile().getName()))
                    .map(Path::getParent)
                    .collect(Collectors.toList()))

                plans.addAll(load(p));

            return plans;
        } catch (IOException e) {
            throw new SchUnitException(e.getMessage(), e);
        }
    }

    public static List<Plan> load(Path path) throws SchUnitException {
        Path configPath = path.resolve(FILENAME);

        if (Files.notExists(configPath))
            return Collections.emptyList();

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            Map<String, Object> config = YAML.load(inputStream);

            Plan plan = new Plan(path);

            if (config.containsKey("name"))
                plan.setName((String) config.get("name"));

            for (String s : parseList(config.get("schematron")))
                plan.addSchematron(s);

            for (String s : parseList(config.get("test")))
                plan.addTest(s);

            return Collections.singletonList(plan);
        } catch (IOException e) {
            throw new SchUnitException(e.getMessage(), e);
        } catch (SchUnitException e) {
            throw new SchUnitException(String.format("Unable to parse '%s'.", configPath), e);
        }
    }

    private static List<String> parseList(Object o) throws SchUnitException {
        if (o == null)
            return Collections.emptyList();
        else if (o instanceof String)
            return Collections.singletonList((String) o);
        else if (o instanceof List)
            return (List<String>) o;

        throw new SchUnitException("Unable to parse property.");
    }
}
