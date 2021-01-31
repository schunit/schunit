<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright 2020 Erlend Klakegg Bergheim
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        schemaVersion="iso" queryBinding="xslt2">

    <title>SCH-001</title>

    <ns prefix="t" uri="test"/>

    <pattern>
        <rule context="t:Document">
            <assert id="TEST-R001"
                    test="t:Header">Document MUST contain header.</assert>
            <assert id="TEST-R002"
                    test="t:Author">Document MUST contain author.</assert>
            <assert id="TEST-R003"
                    test="t:Date">Document MUST contain date.</assert>
        </rule>
    </pattern>
</schema>