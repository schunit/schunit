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

import com.schunit.core.lang.SchunitException;
import com.schunit.core.model.Result;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class SchunitClientTest {

    @Test
    public void simple() throws SchunitException, IOException {
        try (SchunitClient client = SchunitClient.newInstance(Paths.get("src/test/resources/project/simple"))) {
            client.schematron(Paths.get("src/test/resources/project/simple/sch"));

            List<Result> tests = client.test(Paths.get("src/test/resources/project/simple/test"));
            Assert.assertNotNull(tests);
            Assert.assertEquals(tests.size(), 1);

            List<Result> units = client.test(Paths.get("src/test/resources/project/simple/unit"));
            Assert.assertNotNull(units);
            Assert.assertEquals(units.size(), 5);
        }
    }

    @Test
    public void simple2() throws SchunitException, IOException {
        try (SchunitClient client = SchunitClient.newInstance(Paths.get("src/test/resources/config/simple2"))) {
            client.schematron(Paths.get("src/test/resources/config/simple2/main.sch"));
            client.test(Paths.get("src/test/resources/config/simple2/test"));
        }
    }
}
