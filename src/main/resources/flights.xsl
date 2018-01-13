<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsltHelper="ch.visana.bpm.erfa.util.XsltHelper"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/XSL/Format">

    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="textSize">8pt</xsl:variable>
    <xsl:variable name="tableTextSize">6pt</xsl:variable>
    <xsl:variable name="headerBackgroundColor">#F5F5F5</xsl:variable>
    <xsl:variable name="headerBorderColor">#C0C0C0</xsl:variable>
    <xsl:variable name="headerTextSize">10pt</xsl:variable>
    <xsl:variable name="apos">'</xsl:variable>

    <xsl:template match="/flights">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="first"
                                       margin-right="1.5cm"
                                       margin-left="1.5cm"
                                       margin-bottom="1.5cm"
                                       margin-top="1.5cm"
                                       page-width="29.7cm"
                                       page-height="21cm">
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
                    <xsl:apply-templates select="."/>

                    <xsl:if test="count(entscheidungenOfferte) > 0">
                        <xsl:call-template name="header">
                            <xsl:with-param name="title">History</xsl:with-param>
                        </xsl:call-template>
                        <fo:table table-layout="fixed" width="100%" text-align="left">
                            <xsl:attribute name="font-size">
                                <xsl:value-of select="$tableTextSize"/>
                            </xsl:attribute>
                            <fo:table-column column-width="2.5cm"/>
                            <fo:table-column column-width="2.5cm"/>
                            <fo:table-column column-width="4cm"/>
                            <fo:table-column column-width="4cm"/>
                            <fo:table-column column-width="9cm"/>
                            <fo:table-body>
                                <xsl:apply-templates select="entscheidungenOfferte"/>
                            </fo:table-body>
                        </fo:table>
                    </xsl:if>

                    <xsl:if test="count(schadendaten) > 0">
                        <xsl:call-template name="header">
                            <xsl:with-param name="title">Schadendaten</xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="title">
                            <xsl:with-param name="title">Stand</xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="text">
                            <xsl:with-param name="text">
                                <xsl:value-of select="xsltHelper:format(schadendatenStand, 'yyyy-MM-dd')"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="title">
                            <xsl:with-param name="title">VK Satz</xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="text">
                            <xsl:with-param name="text">
                                <xsl:value-of select="schadendaten[1]/vkSatz"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:apply-templates select="schadendaten"/>
                        <xsl:if test="/offerte/schadendatenKommentar">
                            <xsl:call-template name="title">
                                <xsl:with-param name="title">Kommentar</xsl:with-param>
                            </xsl:call-template>
                            <xsl:call-template name="text">
                                <xsl:with-param name="text">
                                    <xsl:value-of select="/offerte/schadendatenKommentar"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:if>
                    </xsl:if>

                    <xsl:if test="count(erfadaten) > 0">
                        <xsl:call-template name="header">
                            <xsl:with-param name="title">Erfa</xsl:with-param>
                        </xsl:call-template>
                        <xsl:apply-templates select="erfadaten"/>
                    </xsl:if>

                    <xsl:if test="count(lohngruppen) > 0">
                        <xsl:variable name="totalPraemie">
                            <xsl:value-of
                                    select="sum(/offerte/erfadaten[/offerte/bevorzugteVariante + 1]/tarifierungen/praemie)"/>
                        </xsl:variable>
                        <xsl:variable name="anzahlJahre">
                            <xsl:value-of
                                    select="count(/offerte/erfadaten[/offerte/bevorzugteVariante + 1]/tarifierungen)"/>
                        </xsl:variable>
                        <xsl:variable name="totalTarifpraemien">
                            <xsl:value-of select="sum(/offerte/lohngruppen/tarifpraemie)"/>
                        </xsl:variable>
                        <xsl:call-template name="header">
                            <xsl:with-param name="title">Lohngruppen</xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="title">
                            <xsl:with-param name="title">Zielprämie</xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="text">
                            <xsl:with-param name="text">
                                <xsl:value-of
                                        select="translate(format-number($totalPraemie div $anzahlJahre, '###,###,###,###'), ',', $apos)"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="title">
                            <xsl:with-param name="title">Differenz zu Zielprämie</xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="text">
                            <xsl:with-param name="text">
                                <xsl:value-of
                                        select="translate(format-number(($totalPraemie div $anzahlJahre) - $totalTarifpraemien, '###,###,###,###'), ',', $apos)"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="title">
                            <xsl:with-param name="title">Lohngruppen</xsl:with-param>
                        </xsl:call-template>
                        <xsl:apply-templates select="lohngruppen"/>
                    </xsl:if>

                    <xsl:call-template name="header">
                        <xsl:with-param name="title">Firma</xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates select="firma"/>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="item">
        <xsl:call-template name="text">
            <xsl:with-param name="text" select="aircraft/callSign"/>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text" select="startTime"/>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text" select="startLocation"/>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text" select="landingTime"/>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text" select="landingLocation"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="entscheidungenOfferte">
        <fo:table-row xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:call-template name="cell">
                <xsl:with-param name="value">
                    <xsl:value-of select="xsltHelper:format(zeitpunkt, 'HH:mm:ss yyyy-MM-dd')"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="cell">
                <xsl:with-param name="value">
                    <xsl:value-of select="xsltHelper:entscheidFuerOfferteName(entscheid)"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="cell">
                <xsl:with-param name="value">
                    <xsl:value-of select="concat(vollerName, '(', user, ')')"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="cell">
                <xsl:with-param name="value">
                    <xsl:value-of select="group"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="cell">
                <xsl:with-param name="value">
                    <xsl:value-of select="kommentar"/>
                </xsl:with-param>
            </xsl:call-template>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="schadendaten">
        <xsl:variable name="anzahlSchadendaten"
                      select="count(preceding-sibling::schadendaten) + count(following-sibling::schadendaten) + 1"/>
        <xsl:variable name="position" select="count(preceding-sibling::schadendaten)"/>
        <xsl:variable name="title">
            <xsl:choose>
                <xsl:when test="$anzahlSchadendaten > 1">
                    <xsl:choose>
                        <xsl:when test="$position = 0">
                            Summe
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat('Firma ', $position)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    Firma
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="title">
            <xsl:with-param name="title">
                <xsl:value-of select="$title"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="tarifierungen">
            <xsl:with-param name="tarifierungen" select="tarifierungen"/>
            <xsl:with-param name="zeigeFaelle">ja</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="erfadaten">
        <xsl:variable name="position" select="count(preceding-sibling::erfadaten)"/>
        <xsl:variable name="title">
            <xsl:choose>
                <xsl:when test="$position = /offerte/bevorzugteVariante">
                    <xsl:value-of select="concat('Variante ', $position + 1, ' (bevorzugt)')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('Variante ', $position + 1)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <fo:block font-weight="bold" margin-top="2mm" xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="font-size">
                <xsl:value-of select="$headerTextSize"/>
            </xsl:attribute>
            <xsl:value-of select="$title"/>
        </fo:block>
        <xsl:call-template name="title">
            <xsl:with-param name="title">Faktor Erfa</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text">
                <xsl:value-of select="erfaFactor"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="title">
            <xsl:with-param name="title">VK Satz</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text">
                <xsl:value-of select="vkSatz"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="tarifierungen">
            <xsl:with-param name="tarifierungen" select="tarifierungen"/>
            <xsl:with-param name="zeigeFaelle">nein</xsl:with-param>
        </xsl:call-template>
        <fo:table table-layout="fixed" width="100%" text-align="left" margin-top="2mm"
                  xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="font-size">
                <xsl:value-of select="$tableTextSize"/>
            </xsl:attribute>
            <fo:table-column column-width="2.38cm"/>
            <fo:table-column column-width="2.38cm"/>
            <fo:table-column column-width="2.38cm"/>
            <fo:table-column column-width="2.38cm"/>
            <fo:table-column column-width="2.38cm"/>
            <fo:table-column column-width="2.38cm"/>
            <fo:table-body>
                <fo:table-row margin-top="1mm">
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Bedarfsprämiensatz
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="concat(format-number(bedarfspraemiensatz, '#.##'), '%')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row margin-top="1mm">
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Deckung
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="deckung"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Wartefrist
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="wartefrist"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Leistungsdauer
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="leistungsdauer"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        <xsl:if test="kommentar">
            <xsl:call-template name="title">
                <xsl:with-param name="title">Kommentar</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="text">
                <xsl:with-param name="text">
                    <xsl:value-of select="kommentar"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="lohngruppen">
        <xsl:variable name="position" select="count(preceding-sibling::lohngruppen)"/>
        <fo:table table-layout="fixed" width="100%" text-align="right" margin-top="2mm"
                  xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="font-size">
                <xsl:value-of select="$tableTextSize"/>
            </xsl:attribute>
            <fo:table-column column-width="1cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <xsl:if test="$position = 0">
                <fo:table-header font-weight="bold" margin-bottom="2mm">
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>Gruppe</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Lohnsummen</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Total</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Tarifprämie</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Anteil Gruppe an Totalprämie gem. Tarif</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Prämienanteil Differenz zu Zielprämie</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Total Prämie</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>Anzuwendender Prämiensatz*</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
            </xsl:if>
            <fo:table-body>
                <fo:table-row margin-top="1mm">
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            <xsl:value-of select="concat('Gruppe ', $position + 1)"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row margin-top="2mm">
                    <fo:table-cell>
                        <fo:block>
                            Männer
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="translate(format-number(maenner, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row margin-top="1mm">
                    <fo:table-cell>
                        <fo:block>
                            Frauen
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="translate(format-number(frauen, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number(maenner + frauen, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number(tarifpraemie, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="concat(format-number(anteilGruppeAnTotalpraemie, '#.##'), '%')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number(praemienanteilAnDifferenzZielpraemie, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number(totalPraemie, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="concat(format-number(anzuwendenderPraemiensatz, '#.##'), '%')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <xsl:if test="count(following-sibling::lohngruppen) = 0">
                    <fo:table-row margin-top="3mm">
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block/>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block/>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(sum(/offerte/lohngruppen/maenner) + sum(/offerte/lohngruppen/frauen), '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(sum(/offerte/lohngruppen/tarifpraemie), '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block/>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(sum(/offerte/lohngruppen/praemienanteilAnDifferenzZielpraemie), '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(sum(/offerte/lohngruppen/totalPraemie), '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-top="solid" border-top-width="thin">
                            <fo:block/>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:if>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="firma">
        <xsl:call-template name="title">
            <xsl:with-param name="title">Firma</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text">
                <xsl:value-of select="partnernummer"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text">
                <xsl:value-of select="name"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:choose>
            <xsl:when test="/offerte/tarifanpassung = 'true'">
                <xsl:call-template name="text">
                    <xsl:with-param name="text">Mit Tarifanpassung</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="text">
                    <xsl:with-param name="text">Ohne Tarifanpassung</xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="count(/offerte/dateien) > 0">
            <xsl:call-template name="title">
                <xsl:with-param name="title">Dokumente</xsl:with-param>
            </xsl:call-template>
            <fo:table table-layout="fixed" width="100%" text-align="left" margin-top="2mm"
                      xmlns:fo="http://www.w3.org/1999/XSL/Format">
                <xsl:attribute name="font-size">
                    <xsl:value-of select="$tableTextSize"/>
                </xsl:attribute>
                <fo:table-column column-width="10cm"/>
                <fo:table-column column-width="8cm"/>
                <xsl:for-each select="/offerte/dateien">
                    <fo:table-body>
                        <fo:table-row margin-top="1mm">
                            <fo:table-cell>
                                <fo:block>
                                    <xsl:value-of select="name"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block>
                                    <xsl:value-of select="xsltHelper:dateitypname(typ)"/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </xsl:for-each>
            </fo:table>
        </xsl:if>
        <xsl:call-template name="title">
            <xsl:with-param name="title">Lohnsumme</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text">
                <xsl:value-of select="translate(format-number(/offerte/lohnsumme, '###,###,###,###'), ',', $apos)"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="title">
            <xsl:with-param name="title">Fälligkeitsdatum</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="text">
            <xsl:with-param name="text">
                <xsl:value-of select="xsltHelper:format(/offerte/faelligkeitsdatum, 'yyyy-MM-dd')"/>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:if test="/offerte/interessant = 'false' or /offerte/entscheidungenOfferte[last()]/entscheid = 'ABLEHNEN'">
            <xsl:if test="/offerte/ablehnungsgrundKategorie">
                <xsl:call-template name="title">
                    <xsl:with-param name="title">Ablehnungsgrund</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="text">
                    <xsl:with-param name="text"
                                    select="xsltHelper:ablehnungsgrundkategoriename(/offerte/ablehnungsgrundKategorie)"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="/offerte/ablehnungsgrundSupervisor">
                <xsl:call-template name="title">
                    <xsl:with-param name="title">Ablehnungsbegründung Supervisor:</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="text">
                    <xsl:with-param name="text" select="/offerte/ablehnungsgrundSupervisor"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="/offerte/ablehnungsgrundSachbearbeiter">
                <xsl:call-template name="title">
                    <xsl:with-param name="title">Ablehnungsbegründung Underwriter:</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="text">
                    <xsl:with-param name="text" select="/offerte/ablehnungsgrundSachbearbeiter"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="tarifierungen">
        <xsl:param name="tarifierungen"/>
        <xsl:param name="zeigeFaelle"/>
        <fo:table table-layout="fixed" width="100%" text-align="right" margin-top="2mm"
                  xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <xsl:attribute name="font-size">
                <xsl:value-of select="$tableTextSize"/>
            </xsl:attribute>
            <fo:table-column column-width="1cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-column column-width="1.9cm"/>
            <xsl:if test="$zeigeFaelle = 'ja'">
                <fo:table-column column-width="1.9cm"/>
            </xsl:if>
            <fo:table-column column-width="1.9cm"/>
            <fo:table-header font-weight="bold" margin-bottom="2mm">
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block>Zeitpunkt/Jahr</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Prämie in CHF</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Leistungen in CHF</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Loss ratio in %</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Berücksichtigte Rückstellungen in CHF</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Sonstige VK in CHF</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Total Kosten in CHF</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>Gewinn/Verlust in CHF</fo:block>
                    </fo:table-cell>
                    <xsl:if test="$zeigeFaelle = 'ja'">
                        <fo:table-cell>
                            <fo:block>Anzahl Fälle</fo:block>
                        </fo:table-cell>
                    </xsl:if>
                    <fo:table-cell>
                        <fo:block>Combined Ratio in %</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-header>
            <fo:table-body>
                <xsl:for-each select="$tarifierungen">
                    <fo:table-row margin-top="1mm">
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="jahr"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(praemie, '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(leistungen, '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="concat(format-number(lossRatio, '#'), '%')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(rueckstellungen, '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="translate(format-number(vk, '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(totalKosten, '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of
                                        select="translate(format-number(resultat, '###,###,###,###'), ',', $apos)"/>
                            </fo:block>
                        </fo:table-cell>
                        <xsl:if test="$zeigeFaelle = 'ja'">
                            <fo:table-cell>
                                <fo:block>
                                    <xsl:value-of select="faelle"/>
                                </fo:block>
                            </fo:table-cell>
                        </xsl:if>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="concat(format-number(combinedRatio, '#'), '%')"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
                <fo:table-row margin-top="1mm">
                    <xsl:variable name="totalPraemie" select="sum($tarifierungen/praemie)"/>
                    <xsl:variable name="totalLeistungen" select="sum($tarifierungen/leistungen)"/>
                    <xsl:variable name="totalKosten" select="sum($tarifierungen/totalKosten)"/>
                    <xsl:variable name="totalLossratio">
                        <xsl:choose>
                            <xsl:when test="$totalPraemie > 0">
                                <xsl:value-of select="100 * ($totalLeistungen div $totalPraemie)"/>
                            </xsl:when>
                            <xsl:otherwise>
                                0
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="totalCombinedratio">
                        <xsl:choose>
                            <xsl:when test="$totalPraemie > 0">
                                <xsl:value-of select="100 * ($totalKosten div $totalPraemie)"/>
                            </xsl:when>
                            <xsl:otherwise>
                                0
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$totalPraemie > 0">
                            <xsl:variable name="totalLossratio" select="100 * ($totalLeistungen div $totalPraemie)"/>
                            <xsl:variable name="totalCombinedratio" select="100 * ($totalKosten div $totalPraemie)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="totalLossratio" select="0"/>
                            <xsl:variable name="totalCombinedratio" select="0"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <fo:table-cell font-weight="bold">
                        <fo:block>
                            Total
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number($totalPraemie, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number($totalLeistungen, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="concat(format-number($totalLossratio, '#'), '%')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:variable name="totalRueckstellungen" select="sum($tarifierungen/rueckstellungen)"/>
                            <xsl:value-of
                                    select="translate(format-number($totalRueckstellungen, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:variable name="totalVk" select="sum($tarifierungen/vk)"/>
                            <xsl:value-of select="translate(format-number($totalVk, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of
                                    select="translate(format-number($totalKosten, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:variable name="totalResultat" select="sum($tarifierungen/resultat)"/>
                            <xsl:value-of
                                    select="translate(format-number($totalResultat, '###,###,###,###'), ',', $apos)"/>
                        </fo:block>
                    </fo:table-cell>
                    <xsl:if test="$zeigeFaelle = 'ja'">
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="sum($tarifierungen/faelle)"/>
                            </fo:block>
                        </fo:table-cell>
                    </xsl:if>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="concat(format-number($totalCombinedratio, '#'), '%')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
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