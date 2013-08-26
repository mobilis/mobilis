<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns:fn="http://www.w3.org/2005/xpath-functions" 
		xmlns:msdl="http://mobilis.inf.tu-dresden.de/msdl/"
		xmlns:xmpp="http://mobilis.inf.tu-dresden.de/xmpp/">

	<xsl:output method="text" version="2.0" encoding="UTF-8" indent="yes"/>

	<!-- User defined variables -->
	<xsl:variable name="outputFolder" select="'client/'" />
	<xsl:variable name="serviceXMLNS" select="'mns'" />
	
	<!-- Internal variables - do not edit these unless you know exactly what you're doing -->
	<xsl:variable name="space" select="' '" />
	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>
	<xsl:variable name="tab">
		<xsl:text>	</xsl:text>
	</xsl:variable>
	
	<xsl:template match="/msdl:description/msdl:binding/msdl:operation">
		
		<xsl:variable name="fullOperation" select="./@ref" />
		<xsl:variable name="operationName" select="substring-after(./@ref, ':')" />
		
		<xsl:for-each select="/msdl:description/msdl:interface/msdl:operation[@name=$operationName]/*">
		
			<xsl:variable name="className" select="substring-after(./@element, ':')" />
			<xsl:variable name="headerFileName" select="concat($outputFolder, $className, '.h')" />
			<xsl:variable name="implFileName" select="concat($outputFolder, $className, '.m')" />
			<xsl:variable name="direction" select="local-name()" />
			
			<!--
				Header file
			-->
			<xsl:result-document href="{$headerFileName}">
<xsl:text>#import &lt;MXi/MXi.h&gt;
</xsl:text>

<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name = $className]/xs:complexType/xs:sequence/xs:element">
	<xsl:if test="starts-with(./@type, $serviceXMLNS)">
		<xsl:text>#import "</xsl:text><xsl:value-of select="substring-after(./@type, ':')" />
		<xsl:text>.h"</xsl:text><xsl:value-of select="$newline" />
	</xsl:if>
	<xsl:if test="not(./@type)">
		<xsl:text>#import "</xsl:text>
		<xsl:call-template name="makeFirstLetterUpperCase">
			<xsl:with-param name="elementName" select="./@name" />
		</xsl:call-template>
		<xsl:text>.h"</xsl:text><xsl:value-of select="$newline" />
	</xsl:if>
</xsl:for-each>

<xsl:text>
@interface </xsl:text>
				<xsl:value-of select="$className" />
<xsl:text> : MXiBean &lt;</xsl:text>
				<xsl:if test="$direction = 'input'">MXiOutgoingBean</xsl:if>
				<xsl:if test="$direction = 'output'">MXiIncomingBean</xsl:if>
<xsl:text>&gt;

</xsl:text>
				<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$className]/xs:complexType/xs:sequence/xs:element">

					<xsl:text>@property (nonatomic</xsl:text>
					<xsl:choose>
						<xsl:when test="./@maxOccurs != '1'">
							<xsl:text>, strong) NSMutableArray* </xsl:text>
							<xsl:value-of select="./@name" />
							<xsl:text>;</xsl:text>
							<xsl:value-of select="$newline" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="./@type = 'xs:string'">
									<xsl:text>, strong) NSString*</xsl:text>
								</xsl:when>
								<xsl:when test="./@type = 'xs:int'">
									<xsl:text>) NSInteger</xsl:text>
								</xsl:when>
								<xsl:when test="./@type = 'xs:long'">
									<xsl:text>) NSInteger</xsl:text>
								</xsl:when>
								<xsl:when test="./@type = 'xs:boolean'">
									<xsl:text>) BOOL</xsl:text>
								</xsl:when>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<xsl:text>, strong) </xsl:text><xsl:value-of select="substring-after(./@type, ':')" /><xsl:text>*</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, strong) </xsl:text>
									<xsl:call-template name="makeFirstLetterUpperCase">
										<xsl:with-param name="elementName" select="./@name" />
									</xsl:call-template>
									<xsl:text>*</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							
							<xsl:value-of select="$space" />
							<xsl:value-of select="./@name" />
							<xsl:text>;</xsl:text>
							<xsl:value-of select="$newline" />
						</xsl:otherwise>
					</xsl:choose>
					
				</xsl:for-each>
<xsl:text>
- (id)init;

@end</xsl:text>
			</xsl:result-document>
			
			<!--
				Implementation file
			-->
			<xsl:result-document href="{$implFileName}">
<xsl:text>#import "</xsl:text><xsl:value-of select="concat($className, '.h')" /><xsl:text>"

@implementation </xsl:text><xsl:value-of select="$className" /><xsl:text>

@synthesize </xsl:text>
				<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$className]/xs:complexType/xs:sequence/xs:element">
					<xsl:value-of select="./@name" />
					<xsl:if test="position() != last()"><xsl:text>, </xsl:text></xsl:if>
				</xsl:for-each>
<xsl:text>;

- (id)init {
	self = [super initWithBeanType:</xsl:text>
				<xsl:variable name="fullDirection" select="concat('msdl:', $direction)" />
				<xsl:variable name="iqType" select="/msdl:description/msdl:binding/msdl:operation[@ref=$fullOperation]/*[name()=$fullDirection]/@xmpp:type" />
				
				<xsl:value-of select="upper-case($iqType)" />
<xsl:text>];
	
	return self;
}
</xsl:text>
				
				<!-- Bean to XML conversion -->
				<xsl:if test="$direction = 'input'">
<xsl:text>
- (NSXMLElement* )toXML {
	NSXMLElement* beanElement = [NSXMLElement elementWithName:[[self class] elementName]
														xmlns:[[self class] iqNamespace]];

</xsl:text>
					
				<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$className]/xs:complexType/xs:sequence/xs:element">
					<xsl:variable name="elementName" select="concat(./@name, 'Element')" />
					
					<xsl:choose>
						<xsl:when test="./@maxOccurs != '1'">
							<!-- NSArray -->
							<xsl:call-template name="convertToListElement">
								<xsl:with-param name="elementName" select="$elementName" />
								<xsl:with-param name="elementToAddTo" select="'beanElement'" />
								<xsl:with-param name="collectionName" select="concat('[self ', ./@name, ']')" />
								<xsl:with-param name="indent" select="$tab" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<!-- Atomic type -->
							<xsl:choose>
								<xsl:when test="./@type">
									<!-- Element type that is defined externally -->
									<xsl:choose>
										<xsl:when test="starts-with(./@type, $serviceXMLNS)">
											<!-- Custom type -->
											<xsl:call-template name="convertToCustomNamedAtomicElement">
												<xsl:with-param name="elementName" select="$elementName" />
												<xsl:with-param name="indent" select="$tab" />
												<xsl:with-param name="elementToAddTo" select="'beanElement'" />
												<xsl:with-param name="wholeName" select="./@name" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<!-- Simple type -->
											<xsl:call-template name="convertToSimpleAtomicXMLElement">
												<xsl:with-param name="elementName" select="$elementName" />
												<xsl:with-param name="typeName" select="./@type" />
												<xsl:with-param name="valueName" select="concat('[self ', ./@name, ']')" />
												<xsl:with-param name="elementToAddTo" select="'beanElement'" />
												<xsl:with-param name="indent" select="$tab" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<!-- Element structure is defined with complexType subelement -->
									<xsl:call-template name="convertToCustomAnonymousAtomicElement">
										<xsl:with-param name="elementName" select="$elementName" />
										<xsl:with-param name="wholeName" select="./@name" />
										<xsl:with-param name="elementToAddTo" select="'beanElement'" />
										<xsl:with-param name="collectionName" select="./@name" />
										<xsl:with-param name="indent" select="$tab" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:value-of select="$newline" />
					
				</xsl:for-each>
				
<xsl:text>	return beanElement;
}</xsl:text>
				</xsl:if>
				
				<!-- XML to Bean conversion -->
				<xsl:if test="$direction = 'output'">
					
					
<xsl:text>
- (void)fromXML:(NSXMLElement* )xml {
</xsl:text>
					
					<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$className]/xs:complexType/xs:sequence/xs:element">
						<xsl:variable name="elementName" select="concat(./@name, 'Element')" />
						
						<xsl:choose>
							<xsl:when test="./@maxOccurs != '1'">
								<!-- array -->
								<xsl:call-template name="convertFromListElement">
									<xsl:with-param name="indent" select="$tab" />
									<xsl:with-param name="memberName" select="./@name" />
									<xsl:with-param name="objectToWorkOn" select="'self'" />
									<xsl:with-param name="xmlVariableName" select="'xml'" />
									<xsl:with-param name="type" select="./@type" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="./@type">
										<xsl:choose>
											<xsl:when test="starts-with(./@type, 'xs:')">
												<!-- simple type -->
												<xsl:call-template name="convertFromSimpleAtomicXMLElement">
													<xsl:with-param name="indent" select="$tab" />
													<xsl:with-param name="objectToWorkOn" select="'self'" />
													<xsl:with-param name="memberName" select="./@name" />
													<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
													<xsl:with-param name="xmlElementName" select="./@name" />
													<xsl:with-param name="xmlVariableName" select="'xml'" />
													<xsl:with-param name="type" select="./@type" />
												</xsl:call-template>
											</xsl:when>
											<xsl:when test="starts-with(./@type, $serviceXMLNS)">
												<!-- custom type -->
												<xsl:call-template name="convertFromCustomNamedAtomicElement">
													<xsl:with-param name="indent" select="$tab" />
													<xsl:with-param name="memberName" select="./@name" />
													<xsl:with-param name="type" select="./@type" />
													<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
													<xsl:with-param name="xmlElementName" select="./@name" />
													<xsl:with-param name="xmlVariableName" select="'xml'" />
													<xsl:with-param name="objectToWorkOn" select="'self'" />
												</xsl:call-template>
											</xsl:when>
											<xsl:otherwise>
												<!-- unknown type -->
												<xsl:text>&lt;unknown&gt;</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<!-- anomymous -->
										<xsl:call-template name="convertFromCustomAnonymousAtomicElement">
											<xsl:with-param name="memberName" select="./@name" />
											<xsl:with-param name="objectToWorkOn" select="'self'" />
											<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
											<xsl:with-param name="xmlVariableName" select="'xml'" />
											<xsl:with-param name="indent" select="$tab" />
											<xsl:with-param name="xmlElementName" select="./@name" />
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
						
						<xsl:value-of select="$newline" />
						<xsl:if test="position() != last()">
							<xsl:value-of select="$newline" />
						</xsl:if>
						
					</xsl:for-each>

<xsl:text>}</xsl:text>

				</xsl:if>

<xsl:text>

+ (NSString* )elementName {
	return @"</xsl:text><xsl:value-of select="$className" /><xsl:text>";
}

+ (NSString* )iqNamespace {
	return @"</xsl:text><xsl:value-of select="/msdl:description/msdl:binding/msdl:operation[@ref=$fullOperation]/@xmpp:ident" /><xsl:text>";
}

@end</xsl:text>	
			</xsl:result-document>
			
		</xsl:for-each>
		
	</xsl:template>
	
	<xsl:template match="/">
		<xsl:apply-templates />
		
		<!--
			Generate all element classes that are not beans (i.e. not mentioned as input or output of an operation),
			but are defined in the schema area
		-->
		<xsl:variable name="allClassNames" select="/msdl:description/msdl:types/xs:schema/xs:complexType/@name" />
		<xsl:variable name="allBeanNames" select="/msdl:description/msdl:interface/msdl:operation/*/@element" />
		
		<xsl:for-each select="$allClassNames">
			<xsl:variable name="className" select="." />
			<xsl:variable name="namespacifiedClassName" select="concat($serviceXMLNS, ':', .)" />
			
			<!-- If $allBeanNames does not contain $namespacifiedClassName -->
			<xsl:if test="not($allBeanNames = $namespacifiedClassName)">
				
				<!-- Generate header file -->
				<xsl:result-document href="{concat($outputFolder, $className, '.h')}">
<!-- Imports -->
<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:complexType[@name = $className]/xs:sequence/xs:element">
	<xsl:if test="starts-with(./@type, $serviceXMLNS)">
		<xsl:text>#import "</xsl:text><xsl:value-of select="substring-after(./@type, ':')" />
		<xsl:text>.h"</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:if test="position() = last()">
			<xsl:value-of select="$newline" />
		</xsl:if>
	</xsl:if>
	<xsl:if test="not(./@type)">
		<xsl:text>#import "</xsl:text>
		<xsl:call-template name="makeFirstLetterUpperCase">
			<xsl:with-param name="elementName" select="./@name" />
		</xsl:call-template>
		<xsl:text>.h"</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:if test="position() = last()">
			<xsl:value-of select="$newline" />
		</xsl:if>
		
		<!--
			The class that is being imported here also needs
			to be generated
		-->
		<xsl:call-template name="generateAnonymouslyGivenClass">
			<xsl:with-param name="element" select="." />
		</xsl:call-template>
	</xsl:if>
</xsl:for-each>
					
<!-- Class definition -->
<xsl:text>@interface </xsl:text><xsl:value-of select="$className" /><xsl:text> : NSObject</xsl:text>
<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
					
<!-- Properties -->
<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:complexType[@name = $className]/xs:sequence/xs:element">
	<xsl:text>@property (nonatomic</xsl:text>
	<xsl:choose>
		<xsl:when test="./@maxOccurs != '1'">
			<xsl:text>, strong) NSMutableArray* </xsl:text>
			<xsl:value-of select="./@name" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="./@type = 'xs:string'">
					<xsl:text>, strong) NSString*</xsl:text>
				</xsl:when>
				<xsl:when test="./@type = 'xs:int'">
					<xsl:text>) NSInteger</xsl:text>
				</xsl:when>
				<xsl:when test="./@type = 'xs:long'">
					<xsl:text>) NSInteger</xsl:text>
				</xsl:when>
				<xsl:when test="./@type = 'xs:boolean'">
					<xsl:text>) BOOL</xsl:text>
				</xsl:when>
				<xsl:when test="starts-with(./@type, $serviceXMLNS)">
					<xsl:text>, strong) </xsl:text><xsl:value-of select="substring-after(./@type, ':')" /><xsl:text>*</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>, strong) </xsl:text>
					<xsl:call-template name="makeFirstLetterUpperCase">
						<xsl:with-param name="elementName" select="./@name" />
					</xsl:call-template>
					<xsl:text>*</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:value-of select="$space" />
			<xsl:value-of select="./@name" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text>;</xsl:text><xsl:value-of select="$newline" />
	
	<xsl:if test="position() = last()">
		<xsl:value-of select="$newline" />
	</xsl:if>
</xsl:for-each>
					
<!-- End of header definition -->
<xsl:text>@end</xsl:text>
				</xsl:result-document>
				
				<!-- Generate implementation file -->
				<xsl:result-document href="{concat($outputFolder, $className, '.m')}">
<!-- Import header file -->
<xsl:text>#import "</xsl:text><xsl:value-of select="$className" /><xsl:text>.h"</xsl:text>
<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
					
<!-- Class definition -->
<xsl:text>@implementation </xsl:text><xsl:value-of select="$className" />
<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
					
<!-- Synthesize properties -->
<xsl:text>@synthesize </xsl:text>
<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:complexType[@name = $className]/xs:sequence/xs:element">
	<xsl:value-of select="./@name" />
	<xsl:if test="position() != last()"><xsl:text>, </xsl:text></xsl:if>
	<xsl:if test="position() = last()"><xsl:text>;</xsl:text></xsl:if>
</xsl:for-each>
<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
					
<!-- End of class definition -->
<xsl:text>@end</xsl:text>
				</xsl:result-document>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="generateAnonymouslyGivenClass">
		<xsl:param name="element" required="yes" />
		
		<xsl:variable name="className">
			<xsl:call-template name="makeFirstLetterUpperCase">
				<xsl:with-param name="elementName" select="$element/@name" />
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Header file -->
		<xsl:result-document href="{concat($outputFolder, $className, '.h')}">
			
			<!-- Imports -->
			<xsl:for-each select="./xs:complexType/xs:sequence/xs:element">
				<xsl:if test="not(./@type)">
					<xsl:text>#import "</xsl:text>
					<xsl:call-template name="makeFirstLetterUpperCase">
						<xsl:with-param name="elementName" select="./@name" />
					</xsl:call-template>
					<xsl:text>.h"</xsl:text>
					
					<!-- The imported class needs to be generated as well -->
					<xsl:call-template name="generateAnonymouslyGivenClass">
						<xsl:with-param name="element" select="." />
					</xsl:call-template>
					
					<xsl:value-of select="$newline" />
					<xsl:if test="position() = last()">
						<xsl:value-of select="$newline" />
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
			
			<!-- Class definition -->
			<xsl:text>@interface </xsl:text><xsl:value-of select="$className" />
			<xsl:text> : NSObject</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<!-- Properties -->
			<xsl:for-each select="./xs:complexType/xs:sequence/xs:element">
				<xsl:text>@property (nonatomic</xsl:text>
				<xsl:choose>
					<xsl:when test="./@maxOccurs != '1'">
						<xsl:text>, strong) NSMutableArray* </xsl:text>
						<xsl:value-of select="./@name" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="./@type = 'xs:string'">
								<xsl:text>, strong) NSString*</xsl:text>
							</xsl:when>
							<xsl:when test="./@type = 'xs:int'">
								<xsl:text>) NSInteger</xsl:text>
							</xsl:when>
							<xsl:when test="./@type = 'xs:long'">
								<xsl:text>) NSInteger</xsl:text>
							</xsl:when>
							<xsl:when test="./@type = 'xs:boolean'">
								<xsl:text>) BOOL</xsl:text>
							</xsl:when>
							<xsl:when test="starts-with(./@type, $serviceXMLNS)">
								<xsl:text>, strong) </xsl:text><xsl:value-of select="substring-after(./@type, ':')" /><xsl:text>*</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>, strong) </xsl:text>
								<xsl:call-template name="makeFirstLetterUpperCase">
									<xsl:with-param name="elementName" select="./@name" />
								</xsl:call-template>
								<xsl:text>*</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						
						<xsl:value-of select="$space" />
						<xsl:value-of select="./@name" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>;</xsl:text><xsl:value-of select="$newline" />
				
				<xsl:if test="position() = last()">
					<xsl:value-of select="$newline" />
				</xsl:if>
			</xsl:for-each>
			
			<!-- End of header definition -->
			<xsl:text>@end</xsl:text>
			
		</xsl:result-document>
		
		<!-- Implementation file -->
		<xsl:result-document href="{concat($outputFolder, $className, '.m')}">
			
			<!-- Import header file -->
			<xsl:text>#import "</xsl:text><xsl:value-of select="$className" />
			<xsl:text>.h"</xsl:text><xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<!-- Class definition -->
			<xsl:text>@implementation </xsl:text><xsl:value-of select="$className" />
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<!-- Synthesize properties -->
			<xsl:text>@synthesize </xsl:text>
			<xsl:for-each select="./xs:complexType/xs:sequence/xs:element">
				<xsl:value-of select="./@name" />
				<xsl:if test="position() = last()">;</xsl:if>
				<xsl:if test="position() != last()">, </xsl:if>
			</xsl:for-each>
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<!-- End of class definition -->
			<xsl:text>@end</xsl:text>
			
		</xsl:result-document>
	</xsl:template>

	<!-- BEGIN Sub templates for bean to xml conversion -->

	<xsl:template name="convertToSimpleAtomicXMLElement">
		<xsl:param name="elementName" as="xs:string" />
		<xsl:param name="valueName" as="xs:string" />
		<xsl:param name="typeName" as="xs:string" />
		<xsl:param name="elementToAddTo" as="xs:string" />
		<xsl:param name="indent" as="xs:string" />
		
		<xsl:value-of select="$indent" /><xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$elementName" />
		<xsl:text> = [NSXMLElement elementWithName:@"</xsl:text><xsl:value-of select="./@name" />
		<xsl:text>"];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" /><xsl:text>[</xsl:text><xsl:value-of select="$elementName" />
		<xsl:text> setStringValue:</xsl:text>
		
		<xsl:choose>
			<xsl:when test="$typeName = 'xs:string'">
				<xsl:value-of select="$valueName" />
			</xsl:when>
			<xsl:when test="$typeName = 'xs:int' or $typeName = 'xs:long'">
				<xsl:text>[NSString stringWithFormat:@"%d", </xsl:text>
				<xsl:value-of select="$valueName" />
				<xsl:text>]</xsl:text>
			</xsl:when>
			<xsl:when test="$typeName = 'xs:boolean'">
				<xsl:value-of select="$valueName" />
				<xsl:text> ? @"true" : @"false"</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&lt;unknown&gt;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:text>];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[</xsl:text><xsl:value-of select="$elementToAddTo" />
		<xsl:text> addChild:</xsl:text><xsl:value-of select="$elementName" />
		<xsl:text>];</xsl:text>
		<xsl:value-of select="$newline" />
	</xsl:template>
	
	<xsl:template name="convertToCustomAnonymousAtomicElement">
		<xsl:param name="elementName" as="xs:string" required="yes" />
		<xsl:param name="indent" as="xs:string" required="yes" />
		<xsl:param name="wholeName" as="xs:string" required="yes" />
		<xsl:param name="elementToAddTo" as="xs:string" required="yes" />
		<xsl:param name="collectionName" required="no" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$elementName" />
		<xsl:text> = [NSXMLElement elementWithName:@"</xsl:text><xsl:value-of select="./@name" />
		<xsl:text>"];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Jump into the schema definition for the type and recursively call the appropriate code generation -->
		<xsl:for-each select="./xs:complexType/xs:sequence/xs:element">
			<xsl:variable name="subElementName" select="concat(./@name, 'Element')" />
			
			<xsl:choose>
				<xsl:when test="./@maxOccurs != '1'">
					<!-- Array -->
					<xsl:choose>
						<xsl:when test="$collectionName">
							<xsl:call-template name="convertToListElement">
								<xsl:with-param name="elementName" select="$subElementName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="elementToAddTo" select="$elementName" />
								<xsl:with-param name="collectionName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="convertToListElement">
								<xsl:with-param name="elementName" select="$subElementName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="elementToAddTo" select="$elementName" />
								<xsl:with-param name="collectionName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<!-- Simple atomic type -->
							<xsl:choose>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<!-- Custom atomic named type -->
									<xsl:choose>
										<xsl:when test="$collectionName">
											<xsl:call-template name="convertToCustomNamedAtomicElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="wholeName" select="$wholeName" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
												<xsl:with-param name="collectionName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="convertToCustomNamedAtomicElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="wholeName" select="$wholeName" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
												<xsl:with-param name="collectionName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<!-- (Probably) simple predefined type -->
									<xsl:choose>
										<xsl:when test="$collectionName">
											<xsl:call-template name="convertToSimpleAtomicXMLElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="valueName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
												<xsl:with-param name="typeName" select="./@type" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="convertToSimpleAtomicXMLElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="valueName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
												<xsl:with-param name="typeName" select="./@type" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
									
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<!-- Anonymous complex type -->
							<xsl:choose>
								<xsl:when test="$collectionName">
									<xsl:call-template name="convertToCustomAnonymousAtomicElement">
										<xsl:with-param name="wholeName" select="$wholeName" />
										<xsl:with-param name="elementName" select="$subElementName" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="collectionName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
										<xsl:with-param name="indent" select="$indent" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="convertToCustomAnonymousAtomicElement">
										<xsl:with-param name="wholeName" select="$wholeName" />
										<xsl:with-param name="elementName" select="$subElementName" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="collectionName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
										<xsl:with-param name="indent" select="$indent" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			
		</xsl:for-each>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[</xsl:text><xsl:value-of select="$elementToAddTo" />
		<xsl:text> addChild:</xsl:text>
		<xsl:value-of select="./@name" />
		<xsl:text>Element];</xsl:text>
		<xsl:value-of select="$newline" />
	</xsl:template>
	
	<xsl:template name="convertToCustomNamedAtomicElement">
		<xsl:param name="elementName" as="xs:string" />
		<xsl:param name="wholeName" as="xs:string" />
		<xsl:param name="elementToAddTo" as="xs:string" />
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="collectionName" required="no" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$elementName" />
		<xsl:text> = [NSXMLElement elementWithName:@"</xsl:text><xsl:value-of select="./@name" />
		<xsl:text>"];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Look up the schema definition for the type and recursively call the appropriate code generation -->
		<xsl:variable name="customTypeName" select="./@type" />
		<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:complexType[@name = substring-after($customTypeName, ':')]/xs:sequence/xs:element">
			<xsl:variable name="subElementName" select="concat(./@name, 'Element')" />
			
			<xsl:choose>
				<xsl:when test="./@maxOccurs != '1'">
					<!-- Array -->
					<xsl:choose>
						<xsl:when test="$collectionName">
							<xsl:call-template name="convertToListElement">
								<xsl:with-param name="elementName" select="$subElementName" />
								<xsl:with-param name="elementToAddTo" select="$elementName" />
								<xsl:with-param name="collectionName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
								<xsl:with-param name="indent" select="$indent" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="convertToListElement">
								<xsl:with-param name="elementName" select="$subElementName" />
								<xsl:with-param name="elementToAddTo" select="$elementName" />
								<xsl:with-param name="collectionName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
								<xsl:with-param name="indent" select="$indent" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<!-- Simple atomic type -->
							<xsl:choose>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<!-- Custom atomic named type -->
									<xsl:choose>
										<xsl:when test="$collectionName">
											<xsl:call-template name="convertToCustomNamedAtomicElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
												<xsl:with-param name="wholeName" select="$wholeName" />
												<xsl:with-param name="collectionName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="convertToCustomNamedAtomicElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
												<xsl:with-param name="wholeName" select="$wholeName" />
												<xsl:with-param name="collectionName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
									
								</xsl:when>
								<xsl:otherwise>
									<!-- (Probably) simple predefined type -->
									<xsl:choose>
										<xsl:when test="$collectionName">
											<xsl:call-template name="convertToSimpleAtomicXMLElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="valueName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
												<xsl:with-param name="typeName" select="./@type" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="convertToSimpleAtomicXMLElement">
												<xsl:with-param name="elementName" select="$subElementName" />
												<xsl:with-param name="valueName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
												<xsl:with-param name="typeName" select="./@type" />
												<xsl:with-param name="elementToAddTo" select="$elementName" />
												<xsl:with-param name="indent" select="$indent" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<!-- Anonymous complex type -->
							<xsl:choose>
								<xsl:when test="$collectionName">
									<xsl:call-template name="convertToCustomAnonymousAtomicElement">
										<xsl:with-param name="wholeName" select="$wholeName" />
										<xsl:with-param name="elementName" select="$subElementName" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="collectionName" select="concat('[', $collectionName, ' ', ./@name, ']')" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="convertToCustomAnonymousAtomicElement">
										<xsl:with-param name="wholeName" select="$wholeName" />
										<xsl:with-param name="elementName" select="$subElementName" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="collectionName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[</xsl:text><xsl:value-of select="$elementToAddTo" />
		<xsl:text> addChild:</xsl:text>
		<xsl:value-of select="./@name" />
		<xsl:text>Element];</xsl:text>
		<xsl:value-of select="$newline" />
	</xsl:template>
	
	<xsl:template name="convertToListElement">
		<xsl:param name="elementName" as="xs:string" />
		<xsl:param name="elementToAddTo" as="xs:string" />
		<xsl:param name="collectionName" as="xs:string" />
		<xsl:param name="indent" as="xs:string" />
		
		<xsl:variable name="partName" select="concat(./@name, 'Part')" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>for (</xsl:text>
		<xsl:choose>
			<xsl:when test="./@type">
				<!-- Type name given -->
				<xsl:choose>
					<xsl:when test="./@type = 'xs:string'">
						<xsl:text>NSString* </xsl:text>
					</xsl:when>
					<xsl:when test="./@type = 'xs:int'">
						<xsl:text>NSInteger </xsl:text>
					</xsl:when>
					<xsl:when test="./@type = 'xs:long'">
						<xsl:text>NSInteger </xsl:text>
					</xsl:when>
					<xsl:when test="./@type = 'xs:boolean'">
						<xsl:text>BOOL </xsl:text>
					</xsl:when>
					<xsl:when test="starts-with(./@type, $serviceXMLNS)">
						<xsl:value-of select="substring-after(./@type, ':')" />
						<xsl:text>* </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&lt;unknown&gt; </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<!--
					No type given by name, but rather defined as inner complex type.
					The element name with its first letter turned to upper case serves as the class and therefor type name
				-->
				<xsl:call-template name="makeFirstLetterUpperCase">
					<xsl:with-param name="elementName" select="./@name" />
				</xsl:call-template>
				<xsl:text>* </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$partName" />
		<xsl:text> in </xsl:text>
		<xsl:value-of select="$collectionName" />
		<xsl:text>) {</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Recursion -->
		<xsl:choose>
			<xsl:when test="./@type">
				<!-- Atomic type -->
				<xsl:choose>
					<xsl:when test="starts-with(./@type, $serviceXMLNS)">
						<!-- Custom type -->
						<xsl:call-template name="convertToCustomNamedAtomicElement">
							<xsl:with-param name="elementName" select="$elementName" />
							<xsl:with-param name="wholeName" select="$partName" />
							<xsl:with-param name="indent" select="concat($indent, $tab)" />
							<xsl:with-param name="elementToAddTo" select="$elementToAddTo" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<!-- Simple type -->
						<xsl:call-template name="convertToSimpleAtomicXMLElement">
							<xsl:with-param name="elementName" select="$elementName" />
							<xsl:with-param name="valueName" select="$partName" />
							<xsl:with-param name="typeName" select="./@type" />
							<xsl:with-param name="elementToAddTo" select="$elementToAddTo" />
							<xsl:with-param name="indent" select="concat($indent, $tab)" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<!-- Complex inner type -->
				<xsl:call-template name="convertToCustomAnonymousAtomicElement">
					<xsl:with-param name="elementName" select="$elementName" />
					<xsl:with-param name="wholeName" select="$partName" />
					<xsl:with-param name="elementToAddTo" select="$elementToAddTo" />
					<xsl:with-param name="indent" select="concat($indent, $tab)" />
					<xsl:with-param name="collectionName" select="$partName" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />
	</xsl:template>
	
	<!-- END Sub templates for bean to xml conversion -->
	
	<xsl:template name="makeFirstLetterUpperCase">
		<xsl:param name="elementName" as="xs:string" />
		
		<xsl:variable name="upperCaseFirstLetter" select="upper-case(substring($elementName, 1, 1))" />
		<xsl:variable name="lowerCaseRest" select="substring($elementName, 2, string-length($elementName)-1)" />
		<xsl:variable name="partClassName" select="concat($upperCaseFirstLetter, $lowerCaseRest)" />
		
		<xsl:value-of select="$partClassName" />
	</xsl:template>
	
	<!-- BEGIN Sub templates for xml to bean conversion -->
	
	<xsl:template name="convertFromSimpleAtomicXMLElement">
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="objectToWorkOn" as="xs:string" />
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="codeElementName" as="xs:string" />
		<xsl:param name="xmlElementName" as="xs:string" />
		<xsl:param name="xmlVariableName" as="xs:string" />
		<xsl:param name="type" as="xs:string" />
		
		<xsl:variable name="upperMemberName">
			<xsl:call-template name="makeFirstLetterUpperCase">
				<xsl:with-param name="elementName" select="$memberName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="setterName" select="concat('set', $upperMemberName)" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$codeElementName" />
		<xsl:text> = [</xsl:text><xsl:value-of select="$xmlVariableName" />
		<xsl:text> elementForName:@"</xsl:text><xsl:value-of select="$xmlElementName" />
		<xsl:text>"];</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[</xsl:text><xsl:value-of select="$objectToWorkOn" />
		<xsl:text> </xsl:text><xsl:value-of select="$setterName" />
		<xsl:text>:</xsl:text>
		
		<xsl:choose>
			<xsl:when test="$type = 'xs:string'">
				<xsl:text>[</xsl:text><xsl:value-of select="$codeElementName" />
				<xsl:text> stringValue]</xsl:text>
			</xsl:when>
			<xsl:when test="$type = 'xs:int' or $type = 'xs:long'">
				<xsl:text>[[</xsl:text><xsl:value-of select="$codeElementName" />
				<xsl:text> stringValue] integerValue]</xsl:text>
			</xsl:when>
			<xsl:when test="$type = 'xs:boolean'">
				<xsl:text>[[[</xsl:text><xsl:value-of select="$codeElementName" />
				<xsl:text> stringValue] lowercaseString] isEqualToString:@"true"] ? YES : NO</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&lt;unknown&gt;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:text>];</xsl:text>
	</xsl:template>
	
	<xsl:template name="convertFromSimpleAtomicElementInsideArray">
		<xsl:param name="objectToWorkOn" as="xs:string" />
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="codeElementName" as="xs:string" />
		<xsl:param name="type" as="xs:string" />
		<xsl:param name="indent" as="xs:string" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[[</xsl:text><xsl:value-of select="$objectToWorkOn" />
		<xsl:text> </xsl:text><xsl:value-of select="$memberName" />
		<xsl:text>] addObject:</xsl:text>
		
		<xsl:choose>
			<xsl:when test="$type = 'xs:string'">
				<xsl:text>[</xsl:text><xsl:value-of select="$codeElementName" />
				<xsl:text> stringValue]</xsl:text>
			</xsl:when>
			<xsl:when test="$type = 'xs:int' or $type = 'xs:long'">
				<xsl:text>[[</xsl:text><xsl:value-of select="$codeElementName" />
				<xsl:text> stringValue] integerValue]</xsl:text>
			</xsl:when>
			<xsl:when test="$type = 'xs:boolean'">
				<xsl:text>[[[</xsl:text><xsl:value-of select="$codeElementName" />
				<xsl:text> stringValue] lowercaseString] isEqualToString:@"true"] ? YES : NO</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&lt;unknown&gt;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:text>];</xsl:text>
	</xsl:template>
	
	<xsl:template name="convertFromCustomNamedAtomicElement">
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="codeElementName" as="xs:string" />
		<xsl:param name="xmlElementName" as="xs:string" />
		<xsl:param name="xmlVariableName" as="xs:string" />
		<xsl:param name="type" as="xs:string" />
		<xsl:param name="objectToWorkOn" as="xs:string" />
		
		<xsl:variable name="classNameForType" select="substring-after($type, ':')" />
		<xsl:variable name="upperMemberName">
			<xsl:call-template name="makeFirstLetterUpperCase">
				<xsl:with-param name="elementName" select="$memberName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="setterName" select="concat('set', $upperMemberName)" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$codeElementName" />
		<xsl:text> = [</xsl:text><xsl:value-of select="$xmlVariableName" />
		<xsl:text> elementForName:@"</xsl:text><xsl:value-of select="$xmlElementName" />
		<xsl:text>"];</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[</xsl:text><xsl:value-of select="$objectToWorkOn" />
		<xsl:text> </xsl:text><xsl:value-of select="$setterName" />
		<xsl:text>:[[</xsl:text><xsl:value-of select="$classNameForType" />
		<xsl:text> alloc] init]];</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:complexType[@name = $classNameForType]/xs:sequence/xs:element">
			<xsl:choose>
				<xsl:when test="./@maxOccurs != '1'">
					<!-- array -->
					<xsl:call-template name="convertFromListElement">
						<xsl:with-param name="memberName" select="./@name" />
						<xsl:with-param name="indent" select="$indent" />
						<xsl:with-param name="xmlVariableName" select="$codeElementName" />
						<xsl:with-param name="type" select="./@type" />
						<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<xsl:choose>
								<xsl:when test="starts-with(./@type, 'xs:')">
									<!-- simple type -->
									<xsl:call-template name="convertFromSimpleAtomicXMLElement">
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="xmlVariableName" select="$codeElementName" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="type" select="./@type" />
										<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<!-- custom type -->
									<xsl:call-template name="convertFromCustomNamedAtomicElement">
										<xsl:with-param name="xmlVariableName" select="$codeElementName" />
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="type" select="./@type" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<!-- unknown -->
									<xsl:text>&lt;unknown&gt;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<!-- anonymous -->
							<xsl:call-template name="convertFromCustomAnonymousAtomicElement">
								<xsl:with-param name="xmlElementName" select="./@name" />
								<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
								<xsl:with-param name="xmlVariableName" select="$codeElementName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="position() != last()">
				<xsl:value-of select="$newline" />
			</xsl:if>
			
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="convertFromCustomNamedAtomicElementInsideArray">
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="type" as="xs:string" />
		<xsl:param name="objectToWorkOn" as="xs:string" />
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="xmlVariableName" as="xs:string" />
		
		<xsl:variable name="memberObjectName" select="concat($memberName, 'Object')" />
		<xsl:variable name="classNameForType" select="substring-after($type, ':')" />
		
		<xsl:value-of select="$indent" />
		<xsl:value-of select="$memberObjectName" /><xsl:text> = [[</xsl:text>
		<xsl:value-of select="$classNameForType" /><xsl:text> alloc] init];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Recursion -->
		<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:complexType[@name=substring-after($type, ':')]/xs:sequence/xs:element">
			<xsl:choose>
				<xsl:when test="./@maxOccurs != '1'">
				<!-- Array -->
					<xsl:call-template name="convertFromListElement">
						<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
						<xsl:with-param name="memberName" select="./@name" />
						<xsl:with-param name="indent" select="$indent" />
						<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
						<xsl:with-param name="type" select="./@type" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<xsl:choose>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
								<!-- Custom type -->
									<xsl:call-template name="convertFromCustomNamedAtomicElement">
										<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="type" select="./@type" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="starts-with(./@type, 'xs:')">
								<!-- Simple type -->
									<xsl:call-template name="convertFromSimpleAtomicXMLElement">
										<xsl:with-param name="type" select="./@type" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&lt;unknow&gt;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
						<!-- Anonymous complex type -->
							<xsl:call-template name="convertFromCustomAnonymousAtomicElement">
								<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
								<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="xmlElementName" select="./@name" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="$newline" />
		</xsl:for-each>
		
		<xsl:value-of select="$indent" />
		<xsl:text>[[</xsl:text><xsl:value-of select="$objectToWorkOn" /><xsl:text> </xsl:text>
		<xsl:value-of select="$memberName" /><xsl:text>] addObject:</xsl:text>
		<xsl:value-of select="$memberObjectName" /><xsl:text>];</xsl:text>
	</xsl:template>
	
	<xsl:template name="convertFromCustomAnonymousAtomicElement">
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="codeElementName" as="xs:string" />
		<xsl:param name="xmlElementName" as="xs:string" />
		<xsl:param name="xmlVariableName" as="xs:string" />
		<xsl:param name="objectToWorkOn" as="xs:string" />
		
		<xsl:variable name="upperMemberName">
			<xsl:call-template name="makeFirstLetterUpperCase">
				<xsl:with-param name="elementName" select="$memberName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="classNameForType" select="$upperMemberName" />
		<xsl:variable name="setterName" select="concat('set', $upperMemberName)" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$codeElementName" />
		<xsl:text> = [</xsl:text><xsl:value-of select="$xmlVariableName" />
		<xsl:text> elementForName:@"</xsl:text><xsl:value-of select="$xmlElementName" />
		<xsl:text>"];</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" />
		<xsl:text>[</xsl:text><xsl:value-of select="$objectToWorkOn" />
		<xsl:text> </xsl:text><xsl:value-of select="$setterName" />
		<xsl:text>:[[</xsl:text><xsl:value-of select="$classNameForType" />
		<xsl:text> alloc] init]];</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:for-each select="./xs:complexType/xs:sequence/xs:element">
			<xsl:choose>
				<xsl:when test="./@maxOccurs != '1'">
					<!-- array -->
					<xsl:choose>
						<xsl:when test="./@type">
							<xsl:call-template name="convertFromListElement">
								<xsl:with-param name="type" select="./@type" />
								<xsl:with-param name="xmlVariableName" select="$codeElementName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="convertFromListElement">
								<xsl:with-param name="xmlVariableName" select="$codeElementName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<xsl:choose>
								<xsl:when test="starts-with(./@type, 'xs:')">
									<!-- simple type -->
									<xsl:call-template name="convertFromSimpleAtomicXMLElement">
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="xmlVariableName" select="$codeElementName" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="type" select="./@type" />
										<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<!-- custom type -->
									<xsl:call-template name="convertFromCustomNamedAtomicElement">
										<xsl:with-param name="xmlVariableName" select="$codeElementName" />
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="type" select="./@type" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<!-- unknown -->
									<xsl:text>&lt;unknown&gt;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<!-- anonymous -->
							<xsl:call-template name="convertFromCustomAnonymousAtomicElement">
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="xmlVariableName" select="$codeElementName" />
								<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
								<xsl:with-param name="xmlElementName" select="./@name" />
								<xsl:with-param name="objectToWorkOn" select="concat('[', $objectToWorkOn, ' ', $memberName, ']')" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="position() != last()">
				<xsl:value-of select="$newline" />
			</xsl:if>
			
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="convertFromCustomAnonymousAtomicElementInsideArray">
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="objectToWorkOn" as="xs:string" />
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="xmlVariableName" as="xs:string" />
		
		<xsl:variable name="memberObjectName" select="concat($memberName, 'Object')" />
		<xsl:variable name="classNameForType">
			<xsl:call-template name="makeFirstLetterUpperCase">
				<xsl:with-param name="elementName" select="$memberName" />
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:value-of select="$indent" />
		<xsl:value-of select="$memberObjectName" /><xsl:text> = [[</xsl:text>
		<xsl:value-of select="$classNameForType" /><xsl:text> alloc] init];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Recursion -->
		<xsl:for-each select="./xs:complexType/xs:sequence/xs:element">
			<xsl:choose>
				<xsl:when test="./@maxOccurs != '1'">
					<!-- Array -->
					<xsl:choose>
						<xsl:when test="./@type">
							<xsl:call-template name="convertFromListElement">
								<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
								<xsl:with-param name="type" select="./@type" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="convertFromListElement">
								<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<xsl:choose>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<!-- Custom type -->
									<xsl:call-template name="convertFromCustomNamedAtomicElement">
										<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="type" select="./@type" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="starts-with(./@type, 'xs:')">
									<!-- Simple type -->
									<xsl:call-template name="convertFromSimpleAtomicXMLElement">
										<xsl:with-param name="type" select="./@type" />
										<xsl:with-param name="memberName" select="./@name" />
										<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
										<xsl:with-param name="xmlElementName" select="./@name" />
										<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&lt;unknow&gt;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<!-- Anonymous complex type -->
							<xsl:call-template name="convertFromCustomAnonymousAtomicElement">
								<xsl:with-param name="codeElementName" select="concat(./@name, 'Element')" />
								<xsl:with-param name="xmlVariableName" select="$xmlVariableName" />
								<xsl:with-param name="indent" select="$indent" />
								<xsl:with-param name="xmlElementName" select="./@name" />
								<xsl:with-param name="memberName" select="./@name" />
								<xsl:with-param name="objectToWorkOn" select="$memberObjectName" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="$newline" />
		</xsl:for-each>
		
		<xsl:value-of select="$indent" />
		<xsl:text>[[</xsl:text><xsl:value-of select="$objectToWorkOn" /><xsl:text> </xsl:text>
		<xsl:value-of select="$memberName" /><xsl:text>] addObject:</xsl:text>
		<xsl:value-of select="$memberObjectName" /><xsl:text>];</xsl:text>
	</xsl:template>
	
	<xsl:template name="convertFromListElement">
		<xsl:param name="indent" as="xs:string" />
		<xsl:param name="memberName" as="xs:string" />
		<xsl:param name="objectToWorkOn" as="xs:string" />
		<xsl:param name="xmlVariableName" as="xs:string" />
		<xsl:param name="type" required="no" />
		
		<xsl:variable name="upperMemberName">
			<xsl:call-template name="makeFirstLetterUpperCase">
				<xsl:with-param name="elementName" select="$memberName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="setterName" select="concat('set', $upperMemberName)" />
		<xsl:variable name="collectionName" select="concat($memberName, 'Elements')" />
		<xsl:variable name="partName" select="concat($memberName, 'Element')" />
		
		<xsl:value-of select="$indent" /><xsl:text>[</xsl:text>
		<xsl:value-of select="$objectToWorkOn" /><xsl:text> </xsl:text>
		<xsl:value-of select="$setterName" /><xsl:text>:[NSMutableArray array]];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" /><xsl:text>NSArray* </xsl:text>
		<xsl:value-of select="$collectionName" /><xsl:text> = [</xsl:text>
		<xsl:value-of select="$xmlVariableName" /><xsl:text> elementsForName:@"</xsl:text>
		<xsl:value-of select="$memberName" /><xsl:text>"];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" /><xsl:text>for (NSXMLElement* </xsl:text>
		<xsl:value-of select="$partName" /><xsl:text> in </xsl:text>
		<xsl:value-of select="$collectionName" /><xsl:text>) {</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Recursion -->
		<xsl:choose>
			<xsl:when test="$type">
				<xsl:choose>
					<xsl:when test="starts-with($type, 'xs:')">
						<!-- simple type -->
						<xsl:call-template name="convertFromSimpleAtomicElementInsideArray">
							<xsl:with-param name="objectToWorkOn" select="$objectToWorkOn" />
							<xsl:with-param name="type" select="$type" />
							<xsl:with-param name="memberName" select="$memberName" />
							<xsl:with-param name="codeElementName" select="$partName" />
							<xsl:with-param name="indent" select="concat($tab, $indent)" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="starts-with($type, $serviceXMLNS)">
						<!-- custom type -->
						<xsl:call-template name="convertFromCustomNamedAtomicElementInsideArray">
							<xsl:with-param name="memberName" select="$memberName" />
							<xsl:with-param name="xmlVariableName" select="$partName" />
							<xsl:with-param name="indent" select="concat($tab, $indent)" />
							<xsl:with-param name="objectToWorkOn" select="$objectToWorkOn" />
							<xsl:with-param name="type" select="$type" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<!-- unknown -->
						<xsl:text>&lt;unknown&gt;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<!-- anonymous -->
				<xsl:call-template name="convertFromCustomAnonymousAtomicElementInsideArray">
					<xsl:with-param name="memberName" select="$memberName" />
					<xsl:with-param name="objectToWorkOn" select="$objectToWorkOn" />
					<xsl:with-param name="indent" select="concat($tab, $indent)" />
					<xsl:with-param name="xmlVariableName" select="$partName" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$newline" />
		
		<xsl:value-of select="$indent" /><xsl:text>}</xsl:text>
	</xsl:template>
	
	<!-- END Sub templates for xml to bean conversion -->
	
</xsl:stylesheet>