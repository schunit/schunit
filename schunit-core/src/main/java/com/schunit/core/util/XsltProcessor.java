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

import com.schunit.core.lang.SchunitException;
import com.schunit.core.model.Content;
import net.sf.saxon.s9api.*;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XsltProcessor {

    private final Processor processor;

    private final List<XsltExecutable> executables;

    public XsltProcessor(Processor processor, List<XsltExecutable> executables) {
        this.processor = processor;
        this.executables = executables;
    }

    public XsltProcessor append(XsltProcessor xsltProcessor) {
        List<XsltExecutable> executables = new ArrayList<>(this.executables);
        executables.addAll(xsltProcessor.executables);

        return new XsltProcessor(processor, executables);
    }

    public Content process(Path path) throws SchunitException {
        return process(path, Collections.emptyMap());
    }

    public Content process(Path path, Map<String, String> parameters) throws SchunitException {
        return process(new StreamSource(path.toFile()), parameters);
    }

    public Content process(Content content) throws SchunitException {
        return process(content.asSource(), Collections.emptyMap());
    }

    public Content process(Content content, Map<String, String> parameters) throws SchunitException {
        return process(content.asSource(), parameters);
    }

    private Content process(Source source, Map<String, String> parameters) throws SchunitException {
        List<XsltTransformer> xsltTransformers = executables.stream()
                .map(XsltExecutable::load)
                .collect(Collectors.toList());

        for (int i = 0; i < xsltTransformers.size(); i++) {
            XsltTransformer xsltTransformer = xsltTransformers.get(i);

            if (i < xsltTransformers.size() - 1)
                xsltTransformer.setDestination(xsltTransformers.get(i + 1));

            for (Map.Entry<String, String> param : parameters.entrySet())
                xsltTransformer.setParameter(new QName(param.getKey()), new XdmAtomicValue(param.getValue()));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        xsltTransformers.get(0).setSource(source);
        xsltTransformers.get(xsltTransformers.size() - 1).setDestination(processor.newSerializer(baos));

        try {
            xsltTransformers.get(0).transform();
        } catch (SaxonApiException e) {
            throw new SchunitException(e);
        }

        return Content.of(baos);
    }
}
