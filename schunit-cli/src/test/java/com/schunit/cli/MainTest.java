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

import com.schunit.core.lang.SchUnitException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MainTest {

    @Test
    public void simple() throws ParseException, SchUnitException {
        CommandLine cmd = Main.parseArgs(Main.loadOptions(), "-v", "-r", "-p", "..");
        Assert.assertEquals(Main.run(cmd), 0);
    }

    @Test(expectedExceptions = SchUnitException.class)
    public void simpleError() throws ParseException, SchUnitException {
        CommandLine cmd = Main.parseArgs(Main.loadOptions(), "-v");
        Assert.assertEquals(Main.run(cmd), 1);
    }
}
