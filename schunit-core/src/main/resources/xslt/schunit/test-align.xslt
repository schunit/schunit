<?xml version="1.0" encoding="UTF-8"?>
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
                xmlns:t="urn:fdc:schunit.com:2020:v1"
                xmlns:vefa="http://difi.no/xsd/vefa/validator/1.0"
                xmlns="urn:fdc:schunit.com:2020:v1"
                exclude-result-prefixes="t vefa">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <!-- SchUnit: Tests -->

    <xsl:template match="/t:Tests">
        <xsl:copy-of select="."/>
    </xsl:template>


    <!-- SchUnit: Test -->

    <xsl:template match="/t:Test">
        <Tests>
            <xsl:copy-of select="."/>
        </Tests>
    </xsl:template>


    <!-- VEFA Validator: test -->

    <xsl:template match="/vefa:test">
        <Tests>
            <Test>
                <xsl:if test="@id">
                    <xsl:attribute name="id" select="@id"/>
                </xsl:if>

                <xsl:apply-templates select="element()" mode="vefa"/>
            </Test>
        </Tests>
    </xsl:template>


    <!-- VEFA Validator: testSet -->

    <xsl:template match="/vefa:testSet">
        <Tests>
            <xsl:apply-templates select="element()" mode="vefa"/>
        </Tests>
    </xsl:template>

    <xsl:template match="vefa:test" mode="vefa">
        <Test>
            <xsl:if test="@id">
                <xsl:attribute name="id" select="@id"/>
            </xsl:if>

            <xsl:apply-templates select="element()" mode="vefa"/>
        </Test>
    </xsl:template>

    <xsl:template match="vefa:assert" mode="vefa">
        <xsl:apply-templates select="element()" mode="vefa"/>
    </xsl:template>

    <xsl:template match="vefa:description" mode="vefa">
        <Description><xsl:value-of select="text()"/></Description>
    </xsl:template>

    <xsl:template match="vefa:scope" mode="vefa">
        <Scope><xsl:value-of select="text()"/></Scope>
    </xsl:template>

    <xsl:template match="vefa:error | vefa:warning" mode="vefa">
        <Trigger>
            <xsl:if test="@number">
                <xsl:attribute name="count" select="@number"/>
            </xsl:if>
            <xsl:value-of select="text()"/>
        </Trigger>
    </xsl:template>

    <xsl:template match="vefa:success" mode="vefa">
        <Success>
            <xsl:attribute name="count" select="if (@number) then @number else -1"/>
            <xsl:value-of select="text()"/>
        </Success>
    </xsl:template>

    <xsl:template match="*[namespace-uri() != 'http://difi.no/xsd/vefa/validator/1.0']" mode="vefa">
        <xsl:copy-of select="."/>
    </xsl:template>


    <!-- Other -->

    <xsl:template match="element()">
        <Tests>
            <Test>
                <xsl:apply-templates select="//comment()[1]" mode="parser"/>
                <Success>#ignore</Success>
                <xsl:copy-of select="."/>
            </Test>
        </Tests>
    </xsl:template>

    <xsl:template match="comment()" mode="parser">
        <xsl:for-each select="tokenize(replace(string-join(for $l in tokenize(string(.), '\n') return normalize-space($l), '||'), '^[|]+(.+)[|]+$', '$1'), '[|]{4,}')">
            <xsl:choose>
                <xsl:when test="starts-with(lower-case(current()), 'errors:') or starts-with(lower-case(current()), 'error:') or starts-with(lower-case(current()), 'warnings:') or starts-with(lower-case(current()), 'warning:') or starts-with(lower-case(current()), 'trigger:') or starts-with(lower-case(current()), 'triggers:')">
                    <xsl:for-each select="tokenize(current(), '[|]+')">
                        <xsl:if test="normalize-space(current()) and position() > 1 and not(lower-case(normalize-space(current())) = 'none')">
                            <xsl:choose>
                                <xsl:when test="contains(current(), ' x ')">
                                    <Trigger>
                                        <xsl:attribute name="count"><xsl:value-of select="tokenize(current(), ' x ')[2]"/></xsl:attribute>
                                        <xsl:value-of select="tokenize(current(), ' x ')[1]"/>
                                    </Trigger>
                                </xsl:when>
                                <xsl:otherwise>
                                    <Trigger><xsl:value-of select="current()"/></Trigger>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:when test="starts-with(lower-case(current()), 'content:') or starts-with(lower-case(current()), 'description:')">
                    <Description><xsl:value-of select="replace(replace(current(), '^(.+?)[|]+', ''), '[|]{2,}', ' ')" /></Description>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>