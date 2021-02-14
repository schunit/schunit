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
                xmlns:t="urn:fdc:schunit.com:2020:v1"
                xmlns="urn:fdc:schunit.com:2020:v1"
                exclude-result-prefixes="t">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <xsl:param name="context"/>

    <xsl:variable name="detectedContext"
                  select="if (/t:Tests/t:Context) then normalize-space(/t:Tests/t:Context) else $context"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="t:Tests">
        <Tests>
            <xsl:for-each select="t:Test">
                <Test>
                    <xsl:attribute name="id" select="if (@id) then @id else position()"/>

                    <xsl:apply-templates select="if (not(t:Description) and ../t:Description) then ../t:Description else t:Description" mode="force"/>
                    <xsl:apply-templates select="if (not(t:Scope) and ../t:Scope) then ../t:Scope else t:Scope" mode="force"/>

                    <xsl:apply-templates select="node()"/>
                </Test>
            </xsl:for-each>
        </Tests>
    </xsl:template>

    <xsl:template match="@*|node()" mode="force">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="t:Description | t:Scope"/>

    <xsl:template match="t:Scope" mode="force">
        <Scope><xsl:value-of select="if (normalize-space(text()) = '') then $detectedContext else normalize-space(text())"/></Scope>
    </xsl:template>

    <xsl:template match="t:Success">
        <Success>
            <xsl:if test="@count">
                <xsl:attribute name="count" select="@count"/>
            </xsl:if>
            <xsl:value-of select="if (normalize-space(text()) = '') then $detectedContext else normalize-space(text())"/>
        </Success>
    </xsl:template>

    <xsl:template match="t:Trigger">
        <Trigger>
            <xsl:if test="@count">
                <xsl:attribute name="count" select="@count"/>
            </xsl:if>
            <xsl:if test="@flag">
                <xsl:attribute name="flag" select="@flag"/>
            </xsl:if>
            <xsl:value-of select="if (normalize-space(text()) = '') then $detectedContext else normalize-space(text())"/>
        </Trigger>
    </xsl:template>

</xsl:stylesheet>