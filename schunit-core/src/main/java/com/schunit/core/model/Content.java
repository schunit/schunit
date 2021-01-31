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

import com.google.common.io.ByteStreams;
import com.schunit.core.lang.SchunitException;
import com.schunit.core.util.JaxbInstance;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class to provide some convenience while working with content.
 */
public class Content {

    /**
     * Byte content of instance.
     */
    private final byte[] bytes;

    public static Content ofResource(String resource) throws IOException {
        InputStream inputStream = Content.class.getResourceAsStream(resource);

        if (inputStream == null)
            throw new IOException(String.format("Unable to load resource '%s'.", resource));

        return of(inputStream);
    }

    public static Content of(byte[] bytes) {
        return new Content(bytes);
    }

    public static Content of(InputStream inputStream) throws IOException {
        return of(ByteStreams.toByteArray(inputStream));
    }

    public static Content of(ByteArrayOutputStream byteArrayOutputStream) {
        return of(byteArrayOutputStream.toByteArray());
    }

    public static Content of(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return of(inputStream);
        }
    }

    /**
     * Simple private constructor taking content.
     *
     * @param bytes Actual content.
     */
    private Content(byte[] bytes) {
        this.bytes = bytes;
    }

    public InputStream asStream() {
        return new ByteArrayInputStream(bytes);
    }

    public Source asSource() {
        return new StreamSource(asStream());
    }

    public <T> T as(JaxbInstance jaxbInstance, Class<T> cls) throws SchunitException {
        return jaxbInstance.unmarshal(this, cls);
    }

    public String toString() {
        return new String(bytes);
    }
}
