<?xml version="1.0"?>
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias"
                xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                exclude-result-prefixes="axsl svrl">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <xsl:namespace-alias stylesheet-prefix="axsl" result-prefix="xsl"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xsl:template[svrl:fired-rule]/xsl:choose/xsl:when">
        <axsl:when test="{@test}">
            <svrl:successful-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                <xsl:copy-of select="..//xsl:attribute[@name='id']"/>
                <xsl:copy-of select="..//xsl:attribute[@name='location']"/>
                <xsl:copy-of select="..//svrl:text"/>
            </svrl:successful-assert>
        </axsl:when>
    </xsl:template>

</xsl:stylesheet>