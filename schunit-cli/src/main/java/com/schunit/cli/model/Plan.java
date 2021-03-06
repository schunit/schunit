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

package com.schunit.cli.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plan {

    @Setter
    private String name;

    @Getter
    private final List<Path> schematrons = new ArrayList<>();

    @Getter
    private final List<Path> tests = new ArrayList<>();

    @Getter
    private final Path project;

    public Plan(Path project) {
        this.project = project;
    }

    public String getName() {
        return name != null ? name : (project != null ? project.toString() : "Current");
    }

    public void addSchematron(String... paths) {
        if (paths != null)
            addSchematron(Arrays.stream(paths).map(Paths::get).toArray(Path[]::new));
    }

    public void addSchematron(Path... paths) {
        if (paths != null)
            addSchematron(Arrays.asList(paths));
    }

    public void addSchematron(List<Path> paths) {
        for (Path path : paths)
            if (project == null)
                schematrons.add(path);
            else
                schematrons.add(project.resolve(path));
    }

    public void addTest(String... paths) {
        if (paths != null)
            addTest(Arrays.stream(paths).map(Paths::get).toArray(Path[]::new));
    }

    public void addTest(Path... paths) {
        if (paths != null)
            addTest(Arrays.asList(paths));
    }

    public void addTest(List<Path> paths) {
        for (Path path : paths)
            if (project == null)
                tests.add(path);
            else
                tests.add(project.resolve(path));
    }

    public Path subpath(Path path) {
        if (project == null)
            return path;
        else if (path.startsWith(path))
            return path.subpath(project.getNameCount(), path.getNameCount());
        return path;
    }
}
