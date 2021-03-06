<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:yaasl="yaasl.server.export.XsltHelper"
                xmlns="http://www.w3.org/1999/XSL/Format">

    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="translations"/>

    <xsl:variable name="textSize">8pt</xsl:variable>
    <xsl:variable name="tableTextSize">6pt</xsl:variable>
    <xsl:variable name="headerBackgroundColor">#F5F5F5</xsl:variable>
    <xsl:variable name="headerBorderColor">#C0C0C0</xsl:variable>
    <xsl:variable name="headerTextSize">10pt</xsl:variable>
    <xsl:variable name="apos">'</xsl:variable>

    <xsl:template match="/data">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="first"
                                       margin-right="1.3cm"
                                       margin-left="1.3cm"
                                       margin-bottom="2cm"
                                       margin-top="1cm"
                                       page-width="29.7cm"
                                       page-height="21.0cm">
                    <fo:region-body margin-top="1cm"/>
                    <fo:region-before extent="1cm"/>
                    <fo:region-after extent="1.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="first">
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block line-height="10pt" text-align="end" margin-top="2cm">
                        <xsl:attribute name="font-size">
                            <xsl:value-of select="$textSize"/>
                        </xsl:attribute>
                        Seite
                        <fo:page-number/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-weight="bold" margin-top="1mm" xmlns:fo="http://www.w3.org/1999/XSL/Format">
                        <xsl:attribute name="font-size">
                            <xsl:value-of select="$textSize"/>
                        </xsl:attribute>
                        <xsl:value-of select="concat(yaasl:translate('location', $translations), ': ', location)"/>
                    </fo:block>
                    <fo:block font-weight="bold" margin-top="1mm" xmlns:fo="http://www.w3.org/1999/XSL/Format">
                        <xsl:attribute name="font-size">
                            <xsl:value-of select="$textSize"/>
                        </xsl:attribute>
                        <xsl:value-of select="concat(yaasl:translate('date', $translations), ': ', yaasl:formatDate(date, 'yyyy.MM.dd'))"/>
                    </fo:block>
                    <fo:table table-layout="fixed" width="100%" margin-top="2mm">
                        <xsl:attribute name="font-size">
                            <xsl:value-of select="$tableTextSize"/>
                        </xsl:attribute>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-column column-width="2.1cm"/>
                        <fo:table-header font-weight="bold" margin-bottom="2mm">
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('call.sign', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('start.time', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('start.location', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('landing.time', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('landing.location', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('pilot.1', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('pilot.role', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('pilot.2', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('pilot.role', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('call.sign.tow.plane', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('tow.pilot', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('landing.time', $translations)"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block><xsl:value-of select="yaasl:translate('cost.sharing', $translations)"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="flights/flight">
                                <fo:table-row margin-top="2mm">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="aircraft/callSign"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="substring(./startTime, 12, 5)"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./startLocation/icao"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="substring(./landingTime, 12, 5)"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./landingLocation/icao"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./pilot1/name"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./pilot1Role/description"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./pilot2/name"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./pilot2Role/description"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./towPlane/callSign"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="./towPilot/name"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="substring(./towPlaneLandingTime, 12, 5)"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <fo:block><xsl:value-of select="yaasl:translate(./costSharing/i18n, $translations)"/></fo:block>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <xsl:if test="./comment">
                                    <fo:table-row margin-top="2mm">
                                        <fo:table-cell number-columns-spanned="13">
                                            <fo:block>
                                                <xsl:value-of select="./comment"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:if>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template name="header">
        <xsl:param name="title"/>
        <fo:block border-style="solid" border-width="1pt" line-height="12pt" font-size="10pt" padding-left="2mm"
                  margin-top="3mm" margin-bottom="3mm" xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="background-color">
                <xsl:value-of select="$headerBackgroundColor"/>
            </xsl:attribute>
            <xsl:attribute name="border-color">
                <xsl:value-of select="$headerBorderColor"/>
            </xsl:attribute>
            <xsl:value-of select="$title"/>
        </fo:block>
    </xsl:template>

    <xsl:template name="title">
        <xsl:param name="title"/>
        <fo:block font-weight="bold" margin-top="1mm" xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="font-size">
                <xsl:value-of select="$textSize"/>
            </xsl:attribute>
            <xsl:value-of select="$title"/>
        </fo:block>
    </xsl:template>

    <xsl:template name="text">
        <xsl:param name="text"/>
        <fo:block margin-top="1mm" xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="font-size">
                <xsl:value-of select="$tableTextSize"/>
            </xsl:attribute>
            <xsl:value-of select="$text"/>
        </fo:block>
    </xsl:template>

    <xsl:template name="cell">
        <xsl:param name="value"/>
        <fo:table-cell xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:block>
                <xsl:value-of select="$value"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>

</xsl:stylesheet>