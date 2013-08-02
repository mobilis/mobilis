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
						<xsl:when test="@maxOccurs = 'unbounded'">
							<xsl:variable name="lowerName" select="lower-case(./@name)" />
							
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
									<xsl:call-template name="getAnonymousComplexTypeName">
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
<xsl:text>#import "</xsl:text><xsl:value-of select="substring-after($headerFileName, '/')" /><xsl:text>"

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
						<xsl:when test="./@maxOccurs = 'unbounded'">
							<!-- NSArray -->
							<xsl:call-template name="convertToListElement">
								<xsl:with-param name="elementName" select="$elementName" />
								<xsl:with-param name="elementToAddTo" select="'beanElement'" />
								<xsl:with-param name="collectionName" select="./@name" />
								<xsl:with-param name="indent">
									<xsl:text>	</xsl:text>
								</xsl:with-param>
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
												<xsl:with-param name="indent"><xsl:text>	</xsl:text></xsl:with-param>
												<xsl:with-param name="elementToAddTo" select="'beanElement'" />
												<xsl:with-param name="wholeName" select="./@name" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<!-- Simple type -->
											<xsl:call-template name="convertToSimpleAtomicXMLElement">
												<xsl:with-param name="elementName" select="$elementName " />
												<xsl:with-param name="typeName" select="./@type" />
												<xsl:with-param name="valueName" select="concat('[self ', ./@name, ']')" />
												<xsl:with-param name="elementToAddTo" select="'beanElement'" />
												<xsl:with-param name="indent"><xsl:text>	</xsl:text></xsl:with-param>
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
										<xsl:with-param name="indent">
											<xsl:text>	</xsl:text>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:value-of select="$newline" />
					
				</xsl:for-each>
				
<xsl:text>	return beanElement;
}
</xsl:text>
				</xsl:if>
				
				<!-- XML to Bean conversion -->
				<xsl:if test="$direction = 'output'">
					
					
<xsl:text>
- (void)fromXML:(NSXMLElement* )xml {
</xsl:text>
					
					<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$className]/xs:complexType/xs:sequence/xs:element">
						<xsl:variable name="elementName" select="concat(./@name, 'Element')" />
						
						<xsl:text>	NSXMLElement* </xsl:text><xsl:value-of select="$elementName" />
						<xsl:text> = (NSXMLElement*) [xml childAtIndex:</xsl:text><xsl:value-of select="position()-1" />
						<xsl:text>];</xsl:text><xsl:value-of select="$newline" />
						
						<xsl:choose>
							<xsl:when test="./@type">
								<xsl:choose>
									<xsl:when test="starts-with(./@type, 'xs:')">
										<xsl:call-template name="convertFromSimpleAtomicXMLElement">
											<xsl:with-param name="memberName" select="./@name" />
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="starts-with(./@type, $serviceXMLNS)">
										
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>&lt;unknown&gt;</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								
							</xsl:otherwise>
						</xsl:choose>
						
					</xsl:for-each>

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
				<xsl:when test="./@maxOccurs = 'unbounded'">
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
								<xsl:with-param name="collectionName" select="./@name" />
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
									<xsl:call-template name="convertToCustomNamedAtomicElement">
										<xsl:with-param name="elementName" select="$subElementName" />
										<xsl:with-param name="wholeName" select="$wholeName" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="indent" select="$indent" />
									</xsl:call-template>
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
												<!-- TODO the following is not flexible enough for nested complex types -->
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
										<xsl:with-param name="collectionName" select="./@name" />
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
		
		<xsl:value-of select="$indent" />
		<xsl:text>NSXMLElement* </xsl:text><xsl:value-of select="$elementName" />
		<xsl:text> = [NSXMLElement elementWithName:@"</xsl:text><xsl:value-of select="./@name" />
		<xsl:text>"];</xsl:text>
		<xsl:value-of select="$newline" />
		
		<!-- Look up the schema definition for the type and recursively call the appropriate code generation -->
		<xsl:variable name="customTypeName" select="./@type" />
		<xsl:for-each select="/msdl:description/msdl:types/xs:schema/xs:element[@name = substring-after($customTypeName, ':')]/xs:complexType/xs:sequence/xs:element">
			<xsl:variable name="subElementName" select="concat(./@name, 'Element')" />
			
			<xsl:choose>
				<xsl:when test="./@maxOccurs = 'unbounded'">
					<!-- Array -->
					<xsl:call-template name="convertToListElement">
						<xsl:with-param name="elementName" select="$subElementName" />
						<xsl:with-param name="elementToAddTo" select="$elementName" />
						<xsl:with-param name="collectionName" select="./@name" />
						<xsl:with-param name="indent" select="$indent" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="./@type">
							<!-- Simple atomic type -->
							<xsl:choose>
								<xsl:when test="starts-with(./@type, $serviceXMLNS)">
									<!-- Custom atomic named type -->
									<xsl:call-template name="convertToCustomNamedAtomicElement">
										<xsl:with-param name="elementName" select="$subElementName" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="indent" select="$indent" />
										<xsl:with-param name="wholeName" select="$wholeName" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<!-- (Probably) simple predefined type -->
									<xsl:call-template name="convertToSimpleAtomicXMLElement">
										<xsl:with-param name="elementName" select="$subElementName" />
										<!-- TODO the following is not flexible enough for nested complex types -->
										<xsl:with-param name="valueName" select="concat('[', $wholeName, ' ', ./@name, ']')" />
										<xsl:with-param name="typeName" select="./@type" />
										<xsl:with-param name="elementToAddTo" select="$elementName" />
										<xsl:with-param name="indent" select="$indent" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<!-- Anonymous complex type -->
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
				<xsl:call-template name="getAnonymousComplexTypeName">
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
	
	<xsl:template name="getAnonymousComplexTypeName">
		<xsl:param name="elementName" as="xs:string" />
		
		<xsl:variable name="upperCaseFirstLetter" select="upper-case(substring($elementName, 1, 1))" />
		<xsl:variable name="lowerCaseRest" select="substring($elementName, 2, string-length($elementName)-1)" />
		<xsl:variable name="partClassName" select="concat($upperCaseFirstLetter, $lowerCaseRest)" />
		
		<xsl:value-of select="$partClassName" />
	</xsl:template>
	
	<!-- BEGIN Sub templates for xml to bean conversion -->
	
	<xsl:template name="convertFromSimpleAtomicXMLElement">
		<xsl:param name="memberName" as="xs:string" />
		<xsl:variable name="elementName" select="concat($memberName, 'Element')" />
		
		<xsl:text>	</xsl:text><xsl:value-of select="$memberName" />
		<xsl:text> = [</xsl:text><xsl:value-of select="$elementName" />
		<xsl:text> stringValue];</xsl:text><xsl:value-of select="$newline" />
		<xsl:text>}</xsl:text><xsl:value-of select="$newline" />
	</xsl:template>
	
	<!-- END Sub templates for xml to bean conversion -->
	
</xsl:stylesheet>