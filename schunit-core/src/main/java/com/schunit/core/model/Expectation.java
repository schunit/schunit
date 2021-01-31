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

package com.schunit.core.model;

import com.schunit.core.jaxb.v1.ExpectationType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Expectation {

    private final Map<String, Integer> registery;

    private final Result result;

    public Expectation(Result result, List<ExpectationType> expectations) {
        this.result = result;
        this.registery = expectations.stream()
                .collect(Collectors.toMap(ExpectationType::getValue, ExpectationType::getCount));
    }

    public void register(String id, String message, Error.Type errorType) {
        if (registery.containsKey(id)) {
            if (registery.get(id) > 0)
                // Expected
                registery.put(id, registery.get(id) - 1);
            else
                // Expected but not this many times
                result.addError(errorType, id, message);
        } else
            // Not expected
            result.addError(errorType, id, message);
    }

    public void finish(Error.Type errorType) {
        for (String id : registery.keySet())
            if (registery.get(id) != 0)
                // Expected but not seen
                result.addError(errorType, id, null, registery.get(id));
    }
}
