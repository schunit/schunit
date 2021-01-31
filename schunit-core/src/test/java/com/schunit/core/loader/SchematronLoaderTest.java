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

import com.schunit.core.api.Schematron;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.model.Content;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;

@Guice
public class SchematronLoaderTest {

    @Inject
    private SchematronLoader loader;

    @Test
    public void simple() throws SchunitException, IOException {
        Schematron schematron = loader.load(Paths.get("src/test/resources/project/simple/sch/SCH-001.sch"));

        Assert.assertNotNull(schematron);

        Content test = Content.ofResource("/project/simple/test/valid.xml");

        schematron.validate(test);
    }
}
