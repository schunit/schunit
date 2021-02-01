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

package com.schunit.core.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extra functionality for file handling.
 */
public interface ExtraFiles {

    /**
     * Expand path to list of file in the directory when provided a directory,
     * otherwise return singleton list of the path.
     *
     * @param path Path to be expanded.
     * @return List of paths.
     * @throws IOException Indicates problems related to IO.
     */
    static List<Path> expand(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Collections.singletonList(path);
        } else if (Files.isDirectory(path)) {
            return Files.list(path)
                    .filter(Files::isRegularFile)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            throw new IOException(String.format("Path '%s' not found.", path));
        }
    }
}
