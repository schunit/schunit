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

package com.schunit.core.api;

import com.schunit.core.jaxb.v1.internal.ResultType;
import com.schunit.core.lang.SchUnitException;
import com.schunit.core.model.Content;

import java.nio.file.Path;
import java.util.List;

/**
 * Representation of Schematron instance.
 */
public interface Schematron {

    Path getPath();

    List<String> getRules();

    ResultType validate(Content content) throws SchUnitException;

}
