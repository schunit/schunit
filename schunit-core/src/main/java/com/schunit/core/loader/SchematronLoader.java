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

import com.google.inject.Singleton;
import com.schunit.core.api.Schematron;
import com.schunit.core.jaxb.v1.internal.ExtractType;
import com.schunit.core.jaxb.v1.internal.ResultType;
import com.schunit.core.lang.SchUnitException;
import com.schunit.core.model.Content;
import com.schunit.core.util.JaxbInstance;
import com.schunit.core.util.SaxonHelper;
import com.schunit.core.util.XsltProcessor;
import lombok.Getter;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;

/**
 * This loader performs all loading of Schematron instances.
 */
@Singleton
public class SchematronLoader {

    @Inject
    private SaxonHelper saxon;

    private XsltProcessor preapreProcessor;
    private XsltProcessor extractProcessor;
    private XsltProcessor compileProcessor;
    private XsltProcessor svrlProcessor;

    private JaxbInstance jaxbInstance;

    @Inject
    public void init() throws SchUnitException {
        // Loading JAXB context
        jaxbInstance = JaxbInstance.of(ExtractType.class, ResultType.class);

        // Loading processors
        preapreProcessor = saxon.loadProcessor(
                "/xslt/iso-schematron/iso_dsdl_include.sef",
                "/xslt/iso-schematron/iso_abstract_expand.sef");
        extractProcessor = saxon.loadProcessor(
                "/xslt/schunit/sch-extract.xslt");
        compileProcessor = saxon.loadProcessor(
                "/xslt/iso-schematron/iso_svrl_for_xslt2.sef",
                "/xslt/schunit/sch-instrument.xslt");
        svrlProcessor = saxon.loadProcessor(
                "/xslt/schunit/svrl-processor.xslt");
    }

    public Schematron load(Path path) throws SchUnitException {
        // Load and prepare Schematron file
        Content processed = preapreProcessor.process(path);

        // Extract list of rules
        List<String> rules = extractProcessor.process(processed).as(jaxbInstance, ExtractType.class).getRule();

        // Compile and instrument Schematron
        Content compiled = compileProcessor.process(processed);

        // Load new instance
        return new Instance(path, rules, saxon.loadProcessor(compiled.asSource()));
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class Instance implements Schematron {

        @Getter
        private final Path path;

        @Getter
        private final List<String> rules;

        private final XsltProcessor xsltProcessor;

        public Instance(Path path, List<String> rules, XsltProcessor xsltProcessor) {
            this.path = path;
            this.rules = rules;
            this.xsltProcessor = xsltProcessor;
        }

        @Override
        public ResultType validate(Content content) throws SchUnitException {
            return xsltProcessor.append(svrlProcessor).process(content).as(jaxbInstance, ResultType.class);
        }
    }
}
