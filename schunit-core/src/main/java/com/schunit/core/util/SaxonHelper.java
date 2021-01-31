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

import com.google.inject.Singleton;
import com.schunit.core.lang.SchunitException;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SaxonHelper {

    private final Processor processor = new Processor(false);

    public XsltProcessor loadProcessor(String... resources) throws SchunitException {
        return loadProcessor(Arrays.stream(resources)
                .map(getClass()::getResourceAsStream)
                .map(StreamSource::new)
                .collect(Collectors.toList()));
    }

    public XsltProcessor loadProcessor(InputStream... inputStreams) throws SchunitException {
        return loadProcessor(Arrays.stream(inputStreams)
                .map(StreamSource::new)
                .collect(Collectors.toList()));
    }

    public XsltProcessor loadProcessor(Path... paths) throws SchunitException {
        return loadProcessor(Arrays.stream(paths)
                .map(Path::toFile)
                .map(StreamSource::new)
                .collect(Collectors.toList()));
    }

    public XsltProcessor loadProcessor(Source... sources) throws SchunitException {
        return loadProcessor(Arrays.asList(sources));
    }

    private XsltProcessor loadProcessor(List<Source> sources) throws SchunitException {
        try {
            List<XsltExecutable> executables = new ArrayList<>();

            XsltCompiler compiler = processor.newXsltCompiler();

            for (Source source : sources)
                executables.add(compiler.compile(source));

            return new XsltProcessor(processor, executables);
        } catch (SaxonApiException e) {
            throw new SchunitException(e);
        }
    }

}
