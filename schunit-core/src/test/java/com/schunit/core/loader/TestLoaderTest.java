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

import com.schunit.core.lang.SchUnitException;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.nio.file.Paths;
import java.util.List;

@Guice
public class TestLoaderTest {

    @Inject
    private TestLoader loader;

    @Test
    public void simple() throws SchUnitException {
        List<com.schunit.core.api.Test> tests = loader.load(Paths.get("src/test/resources/project/simple/unit/TEST-R001.xml"));

        Assert.assertEquals(tests.size(), 2);
    }

}
