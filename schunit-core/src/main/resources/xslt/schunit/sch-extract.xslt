<?xml version="1.0" encoding="UTF-8" ?>
<!--
  ~ Copyright 2021 Erlend Klakegg Bergheim
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://purl.oclc.org/dsdl/schematron"
                xmlns="urn:fdc:schunit.com:2020:v1:internal"
                exclude-result-prefixes="sch"
                version="2.0">

    <xsl:template match="sch:schema">
        <Extract>
            <xsl:for-each select="distinct-values(//sch:assert/@id)">
                <xsl:sort/>

                <Rule><xsl:value-of select="current()"/></Rule>
            </xsl:for-each>
        </Extract>
    </xsl:template>

</xsl:stylesheet>