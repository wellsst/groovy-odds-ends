<xsl:stylesheet version='1.0'
                xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                xmlns:msxsl="urn:schemas-microsoft-com:xslt">

    <xsl:output method="text"/>
    <xsl:variable name="theParmNodes" select="//namespace::*[local-name() = 'myNamespace']"/>
    <xsl:template match="/">
        <xsl:variable name="theResult">
            <xsl:for-each select="$theParmNodes">
                <xsl:variable name="theNode" select="."/>
                <xsl:for-each select="$theNode | $theNode/ancestor-or-self::node()[..]">
                    <xsl:element name="slash">/</xsl:element>
                    <xsl:choose>
                        <xsl:when test="self::*">
                            <xsl:element name="nodeName">
                                <xsl:value-of select="name()"/>
                                <xsl:variable name="thisPosition" select="count(preceding-sibling::*[name(current()) = name()])"/>
                                <xsl:variable name="numFollowing" select="count(following-sibling::*[name(current()) = name()])"/>
                                <xsl:if test="$thisPosition + $numFollowing > 0">
                                    <xsl:value-of select="concat('[', $thisPosition + 1, ']')"/>
                                </xsl:if>
                            </xsl:element>
                        </xsl:when>
                        <xsl:otherwise> <!-- This node is not an element -->
                            <xsl:choose>
                                <xsl:when test="count(. | ../@*) = count(../@*)">
                                    <!-- Attribute -->
                                    <xsl:element name="nodeName">
                                        <xsl:value-of select="concat('@',name())"/>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:when test="self::text()">  <!-- Text -->
                                    <xsl:element name="nodeName">
                                        <xsl:value-of select="'text()'"/>
                                        <xsl:variable name="thisPosition" select="count(preceding-sibling::text())"/>
                                        <xsl:variable name="numFollowing" select="count(following-sibling::text())"/>
                                        <xsl:if test="$thisPosition + $numFollowing > 0">
                                            <xsl:value-of select="concat('[', $thisPosition +
                                                           1, ']')"/>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:when test="self::processing-instruction()">
                                    <!-- Processing Instruction -->
                                    <xsl:element name="nodeName">
                                        <xsl:value-of select="'processing-instruction()'"/>
                                        <xsl:variable name="thisPosition" select="count(preceding-sibling::processing-instruction())"/>
                                        <xsl:variable name="numFollowing" select="count(following-sibling::processing-instruction())"/>
                                        <xsl:if test="$thisPosition + $numFollowing > 0">
                                            <xsl:value-of select="concat('[', $thisPosition + 1, ']')"/>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:when test="self::comment()">   <!-- Comment -->
                                    <xsl:element name="nodeName">
                                        <xsl:value-of select="'comment()'"/>
                                        <xsl:variable name="thisPosition" select="count(preceding-sibling::comment())"/>
                                        <xsl:variable name="numFollowing" select="count(following-sibling::comment())"/>
                                        <xsl:if test="$thisPosition + $numFollowing > 0">
                                            <xsl:value-of select="concat('[', $thisPosition + 1, ']')"/>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:when>
                                <!-- Namespace: -->
                                <xsl:when test="count(. | ../namespace::*) = count(../namespace::*)">

                                    <xsl:variable name="apos">'</xsl:variable>
                                    <xsl:element name="nodeName">
                                        <xsl:value-of select="concat('namespace::*', '[local-name() = ', $apos, local-name(), $apos, ']')"/>
                                    </xsl:element>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="msxsl:node-set($theResult)"/>
    </xsl:template>
</xsl:stylesheet>