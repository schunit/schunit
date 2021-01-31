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
import com.schunit.core.api.Test;
import com.schunit.core.jaxb.v1.TestType;
import com.schunit.core.jaxb.v1.TestsType;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.model.Content;
import com.schunit.core.util.JaxbInstance;
import com.schunit.core.util.SaxonHelper;
import com.schunit.core.util.XsltProcessor;
import lombok.Getter;

import javax.inject.Inject;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class TestLoader {

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    @Inject
    private SaxonHelper saxon;

    private XsltProcessor processor;

    private JaxbInstance jaxbInstance;

    @Inject
    public void init() throws SchunitException {
        // Loading JAXB context
        jaxbInstance = JaxbInstance.of(TestsType.class);

        // Loading processors
        processor = saxon.loadProcessor(
                "/xslt/schunit/test-align.xslt",
                "/xslt/schunit/test-prepare.xslt");
    }

    public List<Test> load(Path path) throws SchunitException {
        TestsType tests = processor.process(path).as(jaxbInstance, TestsType.class);

        List<Test> result = new ArrayList<>();

        for (TestType source : tests.getTest())
            if (source.isEnabled())
                result.add(new Instance(path, source, extractXml(source)));

        return result;
    }

    private Content extractXml(TestType source) throws SchunitException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.transform(new DOMSource((org.w3c.dom.Node) source.getAny()), new StreamResult(baos));
        } catch (Exception e) {
            throw new SchunitException(e);
        }

        return Content.of(baos);
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class Instance implements Test {

        @Getter
        private final Path path;

        private final String id;
        private final String description;
        private final Content xml;

        public Instance(Path path, TestType source, Content xml) {
            this.path = path;

            this.id = source.getId();
            this.description = source.getDescription();
            this.xml = xml;
        }

        @Override
        public List<String> getScope() {
            return Collections.emptyList();
        }
    }
}