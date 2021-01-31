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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbInstance {

    private final JAXBContext jaxbContext;

    public static JaxbInstance of(Class<?>... classes) throws SchunitException {
        try {
            return new JaxbInstance(JAXBContext.newInstance(classes));
        } catch (JAXBException e) {
            throw new SchunitException("Unable to load JAXB context.");
        }
    }

    private JaxbInstance(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public Unmarshaller unmarshaller() throws SchunitException {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new SchunitException(e);
        }
    }

    public <T> T unmarshal(Content content, Class<T> cls) throws SchunitException {
        try {
            return unmarshaller().unmarshal(content.asSource(), cls).getValue();
        } catch (JAXBException e) {
            throw new SchunitException(e);
        }
    }

    public Marshaller marshaller() throws SchunitException {
        try {
            return jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            throw new SchunitException(e);
        }
    }
}
