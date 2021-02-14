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
                xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                xmlns="urn:fdc:schunit.com:2020:v1:internal"
                exclude-result-prefixes="svrl">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <!-- Use parameters to limit reported events. -->
    <xsl:param name="scope" select="''"/>
    <xsl:param name="ignore_success" select="'false'"/>


    <xsl:variable name="scopeList" select="tokenize($scope, ',')"/>
    <xsl:variable name="ignoreSuccess" select="$ignore_success = 'true'"/>


    <xsl:template match="svrl:*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|element()|text()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="svrl:schematron-output">
        <Result>
            <xsl:value-of select="$ignoreSuccess"/>
            <xsl:apply-templates select="node()"/>
        </Result>
    </xsl:template>

    <xsl:template match="svrl:successful-assert">
        <xsl:if test="not($ignoreSuccess) and (count($scopeList) = 0 or (some $s in $scopeList satisfies $s = normalize-space(@id)))">
            <Assert status="success">
                <xsl:attribute name="id" select="@id"/>
                <xsl:attribute name="location" select="@location"/>
                <xsl:value-of select="normalize-space(svrl:text)"/>
            </Assert>
        </xsl:if>
    </xsl:template>

    <xsl:template match="svrl:failed-assert">
        <xsl:if test="count($scopeList) = 0 or (some $s in $scopeList satisfies $s = normalize-space(@id))">
            <Assert status="trigger">
                <xsl:attribute name="id" select="@id"/>
                <xsl:attribute name="location" select="@location"/>
                <xsl:if test="@flag">
                    <xsl:attribute name="flag" select="@flag"/>
                </xsl:if>

                <xsl:value-of select="normalize-space(svrl:text)"/>
            </Assert>
        </xsl:if>
    </xsl:template>

    <xsl:template match="comment() | svrl:active-pattern | svrl:fired-rule | svrl:ns-prefix-in-attribute-values"/>

</xsl:stylesheet>