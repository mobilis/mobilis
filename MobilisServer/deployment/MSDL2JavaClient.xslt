<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:msdl="http://mobilis.inf.tu-dresden.de/msdl/" xmlns:xmpp="http://mobilis.inf.tu-dresden.de/xmpp/">
	
	<!-- prints indent like they are in code -->
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<!-- the output folder where the classes will be created. this folder has to be create before -->
	<xsl:variable name="outputFolder" select="'Code/'"/>
	
	<!-- set package namespace if required. default = '' -->
	<xsl:variable name="packageNamespace" select="'de.tud.inf.mobsda.services.myservicename.proxy'" />
	
	<!-- path of the service name -->
	<xsl:variable name="serviceName" select="/msdl:description/msdl:service/@name"/>
	<!-- path of the service namespace -->
	<xsl:variable name="serviceNS" select="/msdl:description/msdl:service/@ident"/>
	<!-- name of the incoming interface -->
	<xsl:variable name="IIncomingInterfaceName" select="concat('I',$serviceName,'Incoming')"/>
	<!-- name of the outgoing interface -->
	<xsl:variable name="IOutgoingInterfaceName" select="concat('I',$serviceName,'Outgoing')"/>
	<!-- name of the callback interface -->
	<xsl:variable name="xmppCallbackInterfaceName" select="'IXMPPCallback'"/>
	<!-- name of the xmpp bean class -->
	<xsl:variable name="xmppBeanClassName" select="'XMPPBean'"/>
	
	<!-- message exchange pattern definitions like there are in msdl and wsdl 2.0 -->
	<xsl:variable name="mepInOut" select="'http://www.w3.org/ns/wsdl/in-out'"/>
	<xsl:variable name="mepOutIn" select="'http://www.w3.org/ns/wsdl/out-in'"/>
	<xsl:variable name="mepInOnly" select="'http://www.w3.org/ns/wsdl/in-only'"/>
	<xsl:variable name="mepOutOnly" select="'http://www.w3.org/ns/wsdl/out-only'"/>
	
	<!-- if a list should be created this will be the concrete list type-->
	<xsl:variable name="concreteListType" select="'ArrayList'"/>
	
	<!-- creates a new line -->
	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>
	
	<!-- creates an indent -->
	<xsl:variable name="indent">
	<xsl:text>	</xsl:text>
	</xsl:variable>
	
	<!-- entry point of the script -->
	<xsl:template match="/">	
	
	<xsl:text>Generated Codefiles of Service &lt; </xsl:text>
		<xsl:value-of select="$serviceName"/>
	<xsl:text> &gt;:</xsl:text><xsl:value-of select="$newline" />
	
	<!-- generate proxy class -->
	<xsl:apply-templates select="/" mode="generateProxyClass" /><xsl:value-of select="$newline" />	 
	 
	 <!-- generate xmpp bean class and info interface -->
	<xsl:apply-templates select="/" mode="generateXMPPInfoInterface" /><xsl:value-of select="$newline" />
	<xsl:apply-templates select="/" mode="generateXMPPBeanClass" /><xsl:value-of select="$newline" />	 
	 
	<!-- creates incoming and outgoing interfaces -->
	<xsl:apply-templates select="/" mode="generateIIncomingInterface" /><xsl:value-of select="$newline" />
	<xsl:apply-templates select="/" mode="generateIOutgoingInterface" /><xsl:value-of select="$newline" />
	
	<!-- creates callback interface -->
	<xsl:apply-templates select="/" mode="generateXMPPCallbackInterface" /><xsl:value-of select="$newline" />
	 
	<!-- creates all specialized bean classes -->
	<xsl:apply-templates select="/msdl:description/msdl:binding/msdl:operation" mode="generateTypeBeanClass" /><xsl:value-of select="$newline" />
	
	<!-- generates all xmpp info type classes -->
	<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:complexType" mode="generateTypeInfoClass" />
	 
	</xsl:template>
    
    
    <!-- generate proxy class -->
    <xsl:template match="/" mode="generateProxyClass" >
		<xsl:variable name="className" select="concat($serviceName,'Proxy')" />
		<xsl:variable name="fileName" select="concat($outputFolder,$className,'.java')" />
		<xsl:value-of select="$fileName" />
		
		<!-- creates a file with the filename -->
		<xsl:result-document href="{$fileName}" >
		
			<!-- if package namespace is set, write it at first into file -->
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
			
			<xsl:text>import java.util.List;</xsl:text>
			<xsl:text>import java.util.ArrayList;</xsl:text>
		
			<!-- begin with class definition -->
			<xsl:text>public class </xsl:text><xsl:value-of select="$className"/><xsl:text> {</xsl:text>
				
				<!-- create attribute _bindingStub -->
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
				<xsl:text>private </xsl:text><xsl:value-of select="$IOutgoingInterfaceName" /><xsl:text> _bindingStub;</xsl:text>
			
				<!-- create constructor -->
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
				<xsl:text>public </xsl:text><xsl:value-of select="$className" /><xsl:text>( </xsl:text><xsl:value-of select="$IOutgoingInterfaceName" /><xsl:text> bindingStub) {</xsl:text>
					<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
					<xsl:text>_bindingStub = bindingStub;</xsl:text>
					<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
				<xsl:text>}</xsl:text>
				
				<!-- create method getBindingStub() -->
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
				<xsl:text>public </xsl:text><xsl:value-of select="$IOutgoingInterfaceName" /><xsl:text> getBindingStub(){</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
					<xsl:text>return _bindingStub;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
				<xsl:text>}</xsl:text>
				
				<!-- for each which should be send, create a proxy method -->
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
				<xsl:apply-templates select="/msdl:description/msdl:interface/msdl:operation[@pattern=$mepInOnly or @pattern=$mepOutIn or @pattern=$mepInOut]" mode="generateProxyMethod"/>
				<xsl:value-of select="$newline" />
				
			<xsl:text>}</xsl:text>

		</xsl:result-document>
    </xsl:template>
    
    <!-- create a proxy method -->
    <xsl:template match="/msdl:description/msdl:interface/msdl:operation" mode="generateProxyMethod">
		<!-- Get operation name -->
		<xsl:variable name="methodName" select="./@name" />
		<!-- Get datatype of input class -->
		<xsl:variable name="outClassName" select="substring-after(msdl:input/@element,':')" />
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>public </xsl:text>
		<xsl:choose>
			<xsl:when test="@pattern=$mepInOut">
				<xsl:text>XMPPBean </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>void </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$methodName" /><xsl:text>( String toJid</xsl:text>
		
		<xsl:if test="@pattern=$mepOutIn">
			<xsl:text>, String packetId</xsl:text>
		</xsl:if>
		
		<xsl:if test="count(/msdl:description/msdl:types/xs:schema/xs:element[@name=$outClassName]/xs:complexType/xs:sequence/xs:element) &gt; 0" >
			<xsl:text>, </xsl:text>
			
			<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$outClassName]/xs:complexType/xs:sequence" mode="generateParameterlistOfElement" >
				<xsl:with-param name="withTypes">1</xsl:with-param>
			</xsl:apply-templates>
		</xsl:if>
		
		<!-- Check if callback is required -->
		<xsl:if test="@pattern=$mepInOut">
			<!-- Looking for $mepInOut patterns-->
			<xsl:variable name="inClassName" select="substring-after(msdl:output/@element,':')" />          
			<!-- If "out"-Element exists, create callback parameter with type; element attribute cannot be empty -->
			<xsl:text>, </xsl:text><xsl:value-of select="$xmppCallbackInterfaceName" /><xsl:text>&lt; </xsl:text><xsl:value-of select="$inClassName" /><xsl:text> &gt;</xsl:text><xsl:text> callback</xsl:text>
		</xsl:if>
	
		<xsl:text> ) {</xsl:text><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>if ( null == _bindingStub</xsl:text>
			
			<!-- Check if callback is required -->
			<xsl:if test="@pattern=$mepInOut">		
				<xsl:text> || null == callback</xsl:text>
			</xsl:if>
			<xsl:text> )</xsl:text>
			
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>return</xsl:text>
				<xsl:if test="@pattern=$mepInOut">
					<xsl:text> null</xsl:text>
				</xsl:if>
				<xsl:text>;</xsl:text>
	
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			
			<!-- Use full contructor of class -->
			<xsl:value-of select="$outClassName" /><xsl:text> out = new </xsl:text><xsl:value-of select="$outClassName" /><xsl:text>( </xsl:text>
			<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$outClassName]/xs:complexType/xs:sequence" mode="generateParameterlistOfElement" />			
			<xsl:text> );</xsl:text>
			
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>out.setTo( toJid );</xsl:text>
			
			<xsl:if test="@pattern=$mepOutIn">
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>out.setId( packetId );</xsl:text>
			</xsl:if>
			
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>_bindingStub.sendXMPPBean( out</xsl:text>
			
			<!-- Check if callback is required -->
			<xsl:if test="@pattern = $mepInOut">
				<xsl:text>, callback</xsl:text>
			</xsl:if>
			
			<xsl:text> );</xsl:text>
			
			<!-- if pattern is inOut return bean -->
			<xsl:if test="@pattern=$mepInOut">
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>return out;</xsl:text>
			</xsl:if>
			
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />
		
    </xsl:template>
    
    <!-- generates a list of parameters, e.g. for constructor call or definition -->
    <xsl:template match="xs:sequence" mode="generateParameterlistOfElement" >
		<xsl:param name="withTypes" />
		<xsl:variable name="i" select="count(./xs:element)" />

		<xsl:for-each select="./xs:element">
			<xsl:if test="$withTypes and $withTypes = 1" >
				<xsl:apply-templates select="." mode="parseElementDatatype"/><xsl:text> </xsl:text>
			</xsl:if>
			
			<xsl:value-of select="./@name" />
				
			<xsl:if test="position() &lt; $i" >
				<xsl:text>, </xsl:text>	
			</xsl:if>
					
		</xsl:for-each>		
    </xsl:template>
    
    <!-- parses a type of element like xs:int to int or to List<Integer> -->
    <xsl:template match="xs:element" mode="parseElementDatatype" >
    	<xsl:param name="asSimple" />
    	<xsl:variable name="elementType" select="./@type" />
    	
    	<xsl:choose>
    		<!-- Generate List datatype if it is one and if it is not explicitly requested -->
			<xsl:when test="compare(./@maxOccurs, 'unbounded') = 0 and number($asSimple) != 1">
				<xsl:text>List&lt; </xsl:text>
				
				<xsl:choose>
					<xsl:when test="contains($elementType,'tns:')">
						<xsl:value-of select="substring-after($elementType,':')" />
					</xsl:when>
		
					<xsl:when test="compare($elementType,'xs:int') = 0"><xsl:text>Integer</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:long') = 0"><xsl:text>Long</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:boolean') = 0"><xsl:text>Boolean</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:double') = 0"><xsl:text>Double</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:float') = 0"><xsl:text>Float</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:byte') = 0"><xsl:text>Byte</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:short') = 0"><xsl:text>Short</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:string') = 0"><xsl:text>String</xsl:text></xsl:when>
					
					<!-- Use "???" if type is unknown -->
					<xsl:otherwise><xsl:text>???</xsl:text></xsl:otherwise>
				
				</xsl:choose>
				<xsl:text> &gt;</xsl:text>
				
			</xsl:when>
				
			<!-- Generate normal datatype -->
			<xsl:otherwise>
				<xsl:choose>
				
					<!-- These types are supported 1:1 -->
					<xsl:when test="contains($elementType,'tns:') or compare($elementType,'xs:int') = 0 or compare($elementType,'xs:long') = 0 or compare($elementType,'xs:boolean') = 0 or compare($elementType,'xs:double') = 0 or compare($elementType,'xs:float') = 0 or compare($elementType,'xs:byte') = 0 or compare($elementType,'xs:short') = 0">
						<xsl:value-of select="substring-after($elementType,':')" />
					</xsl:when>
		
					<!-- Following types have to be parsed -->
					
					<xsl:when test="compare($elementType,'xs:string') = 0">
						<xsl:text>String</xsl:text>
					</xsl:when>
					
					
					<!-- Use "???" if type is unknown -->
					<xsl:otherwise>
						<xsl:text>???</xsl:text>
					</xsl:otherwise>
				
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		
    </xsl:template>
    
    <!-- initializes a value like String = null; -->
    <xsl:template match="xs:element" mode="generateElementDatatypeInitializer" >
    	<xsl:variable name="elementType" select="./@type" />
    	
    	<xsl:choose>
    		<!-- Initialize List -->
    		<xsl:when test="compare(./@maxOccurs, 'unbounded') = 0">
    			<xsl:text>new Array</xsl:text>
    				<xsl:apply-templates select="." mode="parseElementDatatype" />
    			<xsl:text>()</xsl:text>
    		</xsl:when>
    		
    		<!-- Initialize normal datatype -->
	    	<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains($elementType,'tns:')">
						<xsl:text>new </xsl:text><xsl:value-of select="substring-after($elementType,':')" /><xsl:text>()</xsl:text>
					</xsl:when>
		
					<xsl:when test="compare($elementType,'xs:int') = 0"><xsl:text>Integer.MIN_VALUE</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:long') = 0"><xsl:text>Long.MIN_VALUE</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:boolean') = 0"><xsl:text>false</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:double') = 0"><xsl:text>Double.MIN_VALUE</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:float') = 0"><xsl:text>Float.MIN_VALUE</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:byte') = 0"><xsl:text>Byte.MIN_VALUE</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:short') = 0"><xsl:text>Short.MIN_VALUE</xsl:text></xsl:when>
					<xsl:when test="compare($elementType,'xs:string') = 0"><xsl:text>null</xsl:text></xsl:when>
					
					<!-- Use "???" if type is unknown -->
					<xsl:otherwise><xsl:text>???</xsl:text></xsl:otherwise>
				
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

    </xsl:template>
    
	<!-- generates IIncoming Interface -->
    <xsl:template match="/" mode="generateIIncomingInterface" >
		<xsl:variable name="fileName" select="concat($outputFolder,$IIncomingInterfaceName,'.java')" />
		<xsl:value-of select="$fileName" />
		<xsl:result-document href="{$fileName}" >
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
			
			<xsl:text>public interface </xsl:text><xsl:value-of select="$IIncomingInterfaceName" /><xsl:text> {</xsl:text>
			<xsl:value-of select="$newline" />

			<!-- iterate each operation element in interface -->
			<!-- Looking for $mepInOut, $mepOutOnly and $mepOutIn patterns-->
			<xsl:for-each select="msdl:description/msdl:interface/msdl:operation[@pattern=$mepOutOnly or @pattern=$mepOutIn or @pattern=$mepInOut]" >
			
				<!-- Get operation name -->
				<xsl:variable name="methodName" select="./@name" />
				<!-- Get datatype of output class -->
				<xsl:variable name="inClassName" select="substring-after(msdl:output/@element,':')" />
				
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
				
				<xsl:choose>
			        <!-- Looking for $mepOutIn patterns -->
			        <xsl:when test="@pattern=$mepOutIn">
						<!-- <xsl:variable name="outClassName" select="substring-after(msdl:input/@element,':')" />
						<xsl:value-of select="$inClassName" /> -->
						<xsl:value-of select="$xmppBeanClassName" />
			        </xsl:when>
			        <xsl:otherwise>
						<xsl:text>void</xsl:text>
			        </xsl:otherwise>
				</xsl:choose>
				
				
				<xsl:text> on</xsl:text><xsl:value-of select="$methodName" /><xsl:text>( </xsl:text><xsl:value-of select="$inClassName" /><xsl:text> in</xsl:text>
				
				<xsl:text> );</xsl:text>
				
				<xsl:value-of select="$newline" />
				
				<!-- If pattern is in-out an unknown XMPPError can be reponded (e.g. service unavailable) -->
				<xsl:if test="@pattern = $mepInOut">
					<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
					
					<xsl:text>void on</xsl:text><xsl:value-of select="$methodName" /><xsl:text>Error</xsl:text>
						<xsl:text>( </xsl:text><xsl:value-of select="substring-after(msdl:input/@element,':')" /><xsl:text> in);</xsl:text>
					<xsl:value-of select="$newline" />
				</xsl:if>
			
			</xsl:for-each>
				
			<xsl:value-of select="$newline" />

			<xsl:text>}</xsl:text>
		</xsl:result-document>
    </xsl:template>

    <!-- generates IOutgoing Interface -->
	<xsl:template match="/" mode="generateIOutgoingInterface" >
		<xsl:variable name="fileName" select="concat($outputFolder,$IOutgoingInterfaceName,'.java')" />
		<xsl:value-of select="$fileName" />
		<xsl:result-document href="{$fileName}" >
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
			
			<xsl:text>public interface </xsl:text><xsl:value-of select="$IOutgoingInterfaceName" /><xsl:text> {

	void sendXMPPBean( XMPPBean out, IXMPPCallback&lt; ? extends XMPPBean &gt; callback );

	void sendXMPPBean( XMPPBean out );

}</xsl:text>
						
				</xsl:result-document>
			    </xsl:template>
			
	<!-- generates XMPPCallback Interface -->
    <xsl:template match="/" mode="generateXMPPCallbackInterface" >
    	<xsl:variable name="fileName" select="concat($outputFolder,$xmppCallbackInterfaceName,'.java')" />
		<xsl:value-of select="$fileName" />
		<xsl:result-document href="{$fileName}" >
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
					
			<xsl:text>public interface </xsl:text><xsl:value-of select="$xmppCallbackInterfaceName" /><xsl:text>&lt;B extends </xsl:text><xsl:value-of select="$xmppBeanClassName" /><xsl:text>&gt; {</xsl:text>
			
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
			
				<xsl:text>void invoke(B xmppBean);</xsl:text>
			
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<xsl:text>}</xsl:text>

		</xsl:result-document>
    </xsl:template>
    
  
  
	<!-- generates a XMPPInfo class of a type tag -->
    <xsl:template match="xs:complexType" mode="generateTypeInfoClass" >
		<xsl:variable name="namespace" select="./@xmpp:ident" /><!-- serviceNs + '#type:' + classname -->		
		<xsl:variable name="typeInfoClassName" select="./@name" />
		
		<!-- creates output file -->
		<xsl:variable name="fileName" select="concat($outputFolder,$typeInfoClassName,'.java')" />
		<xsl:value-of select="$fileName" />
		<xsl:result-document href="{$fileName}" >
			<!-- if package namespace is set, write it at first -->
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
	
			<xsl:text>import org.xmlpull.v1.XmlPullParser;</xsl:text>
			<xsl:text>import java.util.List;</xsl:text>
			<xsl:text>import java.util.ArrayList;</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						
			<xsl:text>public class </xsl:text><xsl:value-of select="$typeInfoClassName" /><xsl:text> implements XMPPInfo {</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<xsl:if test="count(./xs:sequence/xs:element) > 0">
				<!-- Generate and assign class properties -->
				<xsl:apply-templates select="./xs:sequence/xs:element" mode="generateAndAssignClassProperty" />
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
				
				<!-- Generate Full constructor -->
				<xsl:apply-templates select="./xs:sequence" mode="generateFullConstructor" >
					<xsl:with-param name="typeClassName" select="$typeInfoClassName" />
				</xsl:apply-templates>

				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
			</xsl:if>
			
			<!-- generate empty constructor -->
			<xsl:text>public </xsl:text><xsl:value-of select="$typeInfoClassName" /><xsl:text>(){}</xsl:text>				
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			 
				<xsl:apply-templates select="./xs:sequence" mode="generateFunctionFromXML" />					
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
				
				<xsl:apply-templates select="/" mode="generateFunctionGetChildElement" >
					<xsl:with-param name="childElement"><xsl:value-of select="$typeInfoClassName" /></xsl:with-param>
				</xsl:apply-templates>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
				
				<xsl:apply-templates select="/" mode="generateFunctionGetNamespace" >
					<xsl:with-param name="namespace" >
						<xsl:value-of select="$serviceNS" /><xsl:text>#type:</xsl:text><xsl:value-of select="$typeInfoClassName" />
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />

				<xsl:apply-templates select="./xs:sequence" mode="generateFunctionPayloadToXML" />
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
	
				<xsl:apply-templates select="./xs:sequence/xs:element" mode="generateGetSet" />
		
					<xsl:value-of select="$newline" />
			<xsl:text>}</xsl:text>

		</xsl:result-document>

    </xsl:template>

	<!-- generates a xmpp bean class -->
	<xsl:template match="/msdl:description/msdl:binding/msdl:operation" mode="generateTypeBeanClass" >
		<xsl:variable name="bindingOperationName" select="@ref" />
		<xsl:variable name="interfaceOperationName" select="substring-after(./@ref,':')" />
		<xsl:variable name="namespace" select="./@xmpp:ident" />
		
		<xsl:for-each select="/msdl:description/msdl:interface/msdl:operation[@name=$interfaceOperationName]/*">
			<xsl:variable name="elementName" select="./@element" />
		
			<xsl:choose>
				<xsl:when test="$elementName">
				
					<xsl:variable name="typeBeanClassName" select="substring-after($elementName,':')" />
					<xsl:variable name="operationDirection" select="name()" />
					<xsl:variable name="xmppType" select="/msdl:description/msdl:binding/msdl:operation[@ref = $bindingOperationName]/*[name() = $operationDirection]/@xmpp:type" />
					<xsl:variable name="fileName" select="concat($outputFolder,$typeBeanClassName,'.java')" />
					
					<xsl:value-of select="$fileName" />
					<xsl:result-document href="{$fileName}" >
						<!-- if package namespace is set, write it at first -->
						<xsl:if test="string-length($packageNamespace) > 0">
							<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
							<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						</xsl:if>
					
						<xsl:text>import org.xmlpull.v1.XmlPullParser;</xsl:text>
						<xsl:text>import java.util.List;</xsl:text>
						<xsl:text>import java.util.ArrayList;</xsl:text>
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
				
						<xsl:text>public class </xsl:text><xsl:value-of select="$typeBeanClassName" /><xsl:text> extends </xsl:text><xsl:value-of select="$xmppBeanClassName" /><xsl:text> {</xsl:text>
						
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />						
						<xsl:if test="count(/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence/xs:element) > 0">							
							<!-- Generate and assign class properties -->
							<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence/xs:element" mode="generateAndAssignClassProperty" />
							<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />							
							
							<!-- Generate Full constructor -->
							<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence" mode="generateFullConstructor" >
								<xsl:with-param name="typeClassName" select="$typeBeanClassName" />
								<xsl:with-param name="xmppType" select="$xmppType" />
							</xsl:apply-templates>
							
							<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						</xsl:if>
						<xsl:value-of select="$indent" />
						
						<!-- generate empty constructor -->
						<xsl:text>public </xsl:text><xsl:value-of select="$typeBeanClassName" /><xsl:text>(){</xsl:text>
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />

							<xsl:text>this.setType( </xsl:text>
								<xsl:apply-templates select="/" mode="parseXmppTypeToBeanType" >
									<xsl:with-param name="xmppType" ><xsl:value-of select="$xmppType" /></xsl:with-param>
								</xsl:apply-templates>
							<xsl:text> );</xsl:text>

						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
						<xsl:text>}</xsl:text>					
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$newline" />

						<!-- generate parsing part -->
						<xsl:if test="count(/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence) = 0">
							<xsl:text>	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}</xsl:text>
						</xsl:if>
						<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence" mode="generateFunctionFromXML" >
							<xsl:with-param name="withError" select="1" />
						</xsl:apply-templates>					
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						
						<xsl:apply-templates select="/" mode="generateFunctionGetChildElement" >
							<xsl:with-param name="childElement"><xsl:value-of select="$typeBeanClassName" /></xsl:with-param>
						</xsl:apply-templates>
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						
						<xsl:apply-templates select="/" mode="generateFunctionGetNamespace" >
							<xsl:with-param name="namespace" ><xsl:value-of select="$namespace" /></xsl:with-param>
						</xsl:apply-templates>
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						
						<xsl:apply-templates select="/msdl:description/msdl:interface/msdl:operation/*[@element=$elementName]" mode="generateFunctionClone" />
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						
						<xsl:if test="count(/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence) = 0">
							<xsl:text>	@Override
	public String payloadToXML() { return ""; }</xsl:text>
						</xsl:if>
						<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence" mode="generateFunctionPayloadToXML" >
							<xsl:with-param name="withError" select="1" />
						</xsl:apply-templates>
						<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						
						<xsl:if test="../@pattern = $mepOutIn and compare(name(),'msdl:output') = 0">
							<xsl:apply-templates select=".." mode="generateFaultFunctions" >
								<xsl:with-param name="className" select="$typeBeanClassName" />
							</xsl:apply-templates>
							<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
						</xsl:if>						
						
						<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$typeBeanClassName]/xs:complexType/xs:sequence/xs:element" mode="generateGetSet" />
						
						<xsl:value-of select="$newline" />
						<xsl:text>}</xsl:text>
		
					</xsl:result-document>
					<xsl:value-of select="$newline" />	
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>

    </xsl:template>
    
    
    
	<!-- parses a xmpp type string to xmpp bean type -->
	<xsl:template match="/" mode="parseXmppTypeToBeanType" >
		<xsl:param name="xmppType" />
		
		<xsl:choose>
			<xsl:when test="compare(lower-case($xmppType),'set') = 0"><xsl:text>XMPPBean.TYPE_SET</xsl:text></xsl:when>
			<xsl:when test="compare(lower-case($xmppType),'get') = 0"><xsl:text>XMPPBean.TYPE_GET</xsl:text></xsl:when>
			<xsl:when test="compare(lower-case($xmppType),'result') = 0"><xsl:text>XMPPBean.TYPE_RESULT</xsl:text></xsl:when>
			<xsl:when test="compare(lower-case($xmppType),'chat') = 0"><xsl:text>XMPPBean.TYPE_GET</xsl:text></xsl:when><!-- TODO: muss in chat umgewandelt werden, wenn xmppbean neu ist -->
			<xsl:when test="compare(lower-case($xmppType),'error') = 0"><xsl:text>XMPPBean.TYPE_ERROR</xsl:text></xsl:when>
			<xsl:otherwise>XMPPBean.TYPE_GET</xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    
    <!-- generates a class property assigment like private String str = "hi"; -->
    <xsl:template match="xs:element" mode="generateAndAssignClassProperty" >
		
		<xsl:value-of select="$indent" />
		<xsl:text>private </xsl:text>
			<xsl:apply-templates select="." mode="parseElementDatatype" /><xsl:text> </xsl:text>
			<xsl:value-of select="./@name" /><xsl:text> = </xsl:text>
			<xsl:apply-templates select="." mode="generateElementDatatypeInitializer"/><xsl:text>;</xsl:text>
			
		<xsl:value-of select="$newline" />	
    </xsl:template>
    
    <!-- generates a full constructor with all parameters used -->
    <xsl:template match="xs:sequence" mode="generateFullConstructor" >
    	<xsl:param name="typeClassName"></xsl:param>
    	<xsl:param name="xmppType"></xsl:param>
		
		<xsl:value-of select="$indent" />
		<xsl:text>public </xsl:text><xsl:value-of select="$typeClassName" /><xsl:text>( </xsl:text>
			<xsl:apply-templates select="." mode="generateParameterlistOfElement" >
				<xsl:with-param name="withTypes">1</xsl:with-param>
			</xsl:apply-templates>
		<xsl:text> ) {</xsl:text><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
		<xsl:text>super();</xsl:text><xsl:value-of select="$newline" />
		
		<xsl:apply-templates select="./xs:element" mode="generateParameterAssignment" />
		
		<xsl:if test="$xmppType and string-length($xmppType) > 0">
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>this.setType( </xsl:text>
					<xsl:apply-templates select="/" mode="parseXmppTypeToBeanType" >
						<xsl:with-param name="xmppType" select="$xmppType" />
					</xsl:apply-templates>
				<xsl:text> );</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
		
		<xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
    </xsl:template>
    
    <!-- generates an assignment of paratemters like this.property = newProperty -->
    <xsl:template match="xs:element" mode="generateParameterAssignment" >
		<xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
		
		<xsl:choose>
			<!-- If property is List -->
			<xsl:when test="compare(./@maxOccurs, 'unbounded') = 0">
					<xsl:text>for ( </xsl:text>
						<xsl:apply-templates select="." mode="parseElementDatatype">
    						<xsl:with-param name="asSimple" select="1" />
    					</xsl:apply-templates>
    				<xsl:text> entity : </xsl:text><xsl:value-of select="@name" /><xsl:text> ) {</xsl:text>
    				
					<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
					<xsl:text>this.</xsl:text><xsl:value-of select="@name" /><xsl:text>.add( entity );</xsl:text>
					
					<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
					<xsl:text>}</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>this.</xsl:text><xsl:value-of select="@name" /><xsl:text> = </xsl:text><xsl:value-of select="@name" /><xsl:text>;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$newline" />
    </xsl:template>
    
    <!-- generate the XMPPBean function fromXML which is used to parse a XMPP message to XMPPBean -->
    <xsl:template match="xs:complexType/xs:sequence" mode="generateFunctionFromXML" >
    	<xsl:param name="withError" />
    	<xsl:value-of select="$indent" />
    	
		<xsl:text>@Override</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<xsl:text>public void fromXML( XmlPullParser parser ) throws Exception {</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			
			<xsl:text>boolean done = false;
			
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				
				if (tagName.equals(getChildElement())) {
					parser.next();
				}</xsl:text>					
			
			<!-- create a case for each property of the bean and parse/assign value -->
			<xsl:for-each select="./xs:element">
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				
				<xsl:text>else if (tagName.equals( </xsl:text>
				
				<xsl:choose>
					<xsl:when test="starts-with(@type,'tns:')">
							<xsl:value-of select="substring-after(@type,'tns:')" />
						<xsl:text>.CHILD_ELEMENT</xsl:text>
					</xsl:when>
					
					<xsl:otherwise>
						<xsl:text>"</xsl:text><xsl:value-of select="./@name" /><xsl:text>"</xsl:text>
					</xsl:otherwise>							
				</xsl:choose>
				<xsl:text> ) ) {</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				
				<xsl:choose>
					<!-- If property is List -->
					<xsl:when test="compare(./@maxOccurs, 'unbounded') = 0">
						<xsl:choose>
							<!-- If property is a XMPPInfo, then delegate parsing -->
							<xsl:when test="starts-with(./@type,'tns:')">
															
								<xsl:value-of select="substring-after(@type,'tns:')" />
									<xsl:text> entity = new </xsl:text><xsl:value-of select="substring-after(@type,'tns:')" /><xsl:text>();</xsl:text>
								<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
								
								<xsl:text>entity.fromXML( parser );</xsl:text>
								<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
								
								<xsl:text>this.</xsl:text> <xsl:value-of select="@name" /><xsl:text>.add( entity );</xsl:text>
								<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
								
								<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
								<xsl:text>parser.next();</xsl:text>
							</xsl:when>
							
							<!-- Else, if property is a list of strings -->
							<xsl:when test="contains(@type,'xs:string')">
								<xsl:value-of select="@name" /><xsl:text>.add( parser.nextText() );</xsl:text>
							</xsl:when>
							
							<!-- Else -->
							<xsl:otherwise>
								<xsl:value-of select="@name" /><xsl:text>.add( </xsl:text>
									<xsl:apply-templates select="." mode="generateDatatypeParsePrefix"/>
								<xsl:text>( parser.nextText() ) );</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					
					<xsl:otherwise>					
						<xsl:text>this.</xsl:text><xsl:value-of select="./@name" />
						
						<xsl:choose>
							<!-- If property is a XMPPInfo, then delegate parsing -->
							<xsl:when test="starts-with(./@type,'tns:')">
								<xsl:text>.fromXML( parser );</xsl:text>
							</xsl:when>
							
							<!-- Else, if property is string -->
							<xsl:when test="contains(@type,'xs:string')">
								<xsl:text> = parser.nextText();</xsl:text>
							</xsl:when>
							
							<!-- Else, if property is no string, parse datatype -->
							<xsl:otherwise>
								<xsl:text> = </xsl:text>
									<xsl:apply-templates select="." mode="generateDatatypeParsePrefix"/>
								<xsl:text>( parser.nextText() );</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						
					</xsl:otherwise>				
				</xsl:choose>
				
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />				
				<xsl:text>}</xsl:text>
			</xsl:for-each>
			
			<!-- if this function is generated for a XMPPBean, parse errors too. For XMPPInfo it's not necessary -->
			<xsl:if test="number($withError) = 1">
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				
				<xsl:text>else if (tagName.equals("error")) {</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				
					<xsl:text>parser = parseErrorAttributes(parser);</xsl:text>
					<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				
				<xsl:text>}</xsl:text>
			</xsl:if>
			
			
			<xsl:text>
				else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(getChildElement()))
					done = true;
				else
					parser.next();
				break;
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			default:
				parser.next();
			}
		} while (!done);</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>

    </xsl:template>
    
    <!-- parsing datatypes beside string requires special parsing funcitons like for an integer:= Integer.parseInt( str ) -->
     <xsl:template match="xs:element" mode="generateDatatypeParsePrefix" >
    	<xsl:variable name="elementType" select="./@type" />

		<xsl:choose>
			<xsl:when test="contains($elementType,'tns:')">
				<xsl:value-of select="substring-after($elementType,':')" />
			</xsl:when>

			<xsl:when test="compare($elementType,'xs:int') = 0"><xsl:text>Integer.parseInt</xsl:text></xsl:when>
			<xsl:when test="compare($elementType,'xs:long') = 0"><xsl:text>Long.parseLong</xsl:text></xsl:when>
			<xsl:when test="compare($elementType,'xs:boolean') = 0"><xsl:text>Boolean.parseBoolean</xsl:text></xsl:when>
			<xsl:when test="compare($elementType,'xs:double') = 0"><xsl:text>Double.parseDouble</xsl:text></xsl:when>
			<xsl:when test="compare($elementType,'xs:float') = 0"><xsl:text>Float.parseFloat</xsl:text></xsl:when>
			<xsl:when test="compare($elementType,'xs:byte') = 0"><xsl:text>Byte.parseByte</xsl:text></xsl:when>
			<xsl:when test="compare($elementType,'xs:short') = 0"><xsl:text>Short.parseShort</xsl:text></xsl:when>
			
			<!-- Use "???" if type is unknown -->
			<xsl:otherwise><xsl:text>???</xsl:text></xsl:otherwise>
		
		</xsl:choose>				
		
    </xsl:template>
    
    <!-- generate the XMPPBean function getChildElement using the static property -->
    <xsl:template match="/" mode="generateFunctionGetChildElement" >
    	<xsl:param name="childElement" />
		<xsl:value-of select="$indent" />
		
		<xsl:text>public static final String CHILD_ELEMENT = "</xsl:text>
			<xsl:value-of select="$childElement" /><xsl:text>";</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<xsl:text>@Override</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<xsl:text>public String getChildElement() {</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>return CHILD_ELEMENT;</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
    </xsl:template>
    
    <!-- generate the XMPPBean function getNamespace using the static property -->
    <xsl:template match="/" mode="generateFunctionGetNamespace" >
		<xsl:param name="namespace" />
		<xsl:value-of select="$indent" />
		
		<xsl:text>public static final String NAMESPACE = "</xsl:text>
			<xsl:value-of select="$namespace" /><xsl:text>";</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<xsl:text>@Override</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<xsl:text>public String getNamespace() {</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>return NAMESPACE;</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
    </xsl:template>
    
    <!-- generate XMPPBean function clone to clone a bean -->
    <xsl:template match="/msdl:description/msdl:interface/msdl:operation/*" mode="generateFunctionClone" >
		<xsl:variable name="className" select="substring-after(./@element,':')" />
    	<xsl:value-of select="$indent" />
		
		<xsl:text>@Override</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<xsl:text>public XMPPBean clone() {</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:value-of select="$className" /><xsl:text> clone = new </xsl:text><xsl:value-of select="$className" /><xsl:text>( </xsl:text>
				<xsl:apply-templates select="/msdl:description/msdl:types/xs:schema/xs:element[@name=$className]/xs:complexType/xs:sequence" mode="generateParameterlistOfElement" />
			<xsl:text> );</xsl:text>
			
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>clone.cloneBasicAttributes( clone );</xsl:text>
		
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>return clone;</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>

    </xsl:template>
    
    <!-- generate XMPPBean function payloadToXML to serialize a XMPPBean -->
    <xsl:template match="xs:sequence" mode="generateFunctionPayloadToXML" >
    	<xsl:param name="withError"></xsl:param>
        <xsl:value-of select="$indent" />
		
		<xsl:text>@Override</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		
		<!-- differ between XMPPInfo and XMPPBean using different identifiers -->
		<xsl:text>public String </xsl:text>
		<xsl:choose>
			<xsl:when test="number($withError) = 1"><xsl:text>payloadT</xsl:text></xsl:when>
			<xsl:otherwise><xsl:text>t</xsl:text></xsl:otherwise>
		</xsl:choose>
		
		<xsl:text>oXML() {</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			
			<xsl:text>StringBuilder sb = new StringBuilder();</xsl:text>
			<xsl:value-of select="$newline" />
			
			<xsl:apply-templates select="./xs:element" mode="appendParameterToXml" />
			
			<xsl:if test="number($withError) = 1">
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>sb = appendErrorPayload(sb);</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
		
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>return sb.toString();</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>

    </xsl:template>
    
    <!-- generates a xml tag of a XMPPBean parameter regarding the type of property (simple, list, XMPPInfo) -->
    <xsl:template match="xs:element" mode="appendParameterToXml" >
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
		
		<xsl:choose>
			<!-- If element is a list -->
			<xsl:when test="compare(./@maxOccurs, 'unbounded') = 0">
				<xsl:text>for( </xsl:text>
    				<xsl:apply-templates select="." mode="parseElementDatatype">
    					<xsl:with-param name="asSimple" select="1" />
    				</xsl:apply-templates>
					<xsl:text> entry : this.</xsl:text><xsl:value-of select="./@name" /><xsl:text> ) {</xsl:text>
				
				<xsl:choose>
					<!-- if list consist of simple datatypes like int -->
					<xsl:when test="contains(./@type,'tns:')">
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
						<xsl:text>sb.append( "&lt;" + </xsl:text>
							<xsl:apply-templates select="." mode="parseElementDatatype">
		    					<xsl:with-param name="asSimple" select="1" />
		    				</xsl:apply-templates>
						<xsl:text>.CHILD_ELEMENT + "&gt;" );</xsl:text>
						
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
						<xsl:text>sb.append( entry.toXML() );</xsl:text>
						
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
						<xsl:text>sb.append( "&lt;/" + </xsl:text>
							<xsl:apply-templates select="." mode="parseElementDatatype">
		    					<xsl:with-param name="asSimple" select="1" />
		    				</xsl:apply-templates>
						<xsl:text>.CHILD_ELEMENT + "&gt;" );</xsl:text>
					</xsl:when>
					
					<!-- if list consist of XMPPInfo type -->
					<xsl:otherwise>
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
						<xsl:text>sb.append( "&lt;</xsl:text>
							<xsl:value-of select="./@name" />
						<xsl:text>&gt;" );</xsl:text>
						
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
						<xsl:text>sb.append( entry );</xsl:text>
						
						<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
						<xsl:text>sb.append( "&lt;/</xsl:text>
							<xsl:value-of select="./@name" />
						<xsl:text>&gt;" );</xsl:text>
					</xsl:otherwise>
				</xsl:choose>

				
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>}</xsl:text>
			</xsl:when>
			
			
			<!-- Else if property is no list -->
			<xsl:otherwise>
				<xsl:text>sb.append( "&lt;</xsl:text>				
				<xsl:choose>
					<!-- if property is of type XMPPInfo -->
					<xsl:when test="contains(./@type,'tns:')">
						<xsl:text>" + this.</xsl:text><xsl:value-of select="./@name" /><xsl:text>.getChildElement() + "</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="./@name" />
					</xsl:otherwise>
				</xsl:choose>		
				<xsl:text>&gt;" )</xsl:text>
				
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>.append( this.</xsl:text><xsl:value-of select="./@name" />
					<xsl:if test="contains(./@type,'tns:')">
						<xsl:text>.toXML()</xsl:text>
					</xsl:if>
				<xsl:text> )</xsl:text>
				
				<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
				<xsl:text>.append( "&lt;/</xsl:text>
				<xsl:choose>
					<!-- if property is of type XMPPInfo -->
					<xsl:when test="starts-with(./@type,'tns:')">
						<xsl:text>" + this.</xsl:text><xsl:value-of select="./@name" /><xsl:text>.getChildElement() + "</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="./@name" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>&gt;" );</xsl:text>
			</xsl:otherwise>
		</xsl:choose> 		
		
		<xsl:value-of select="$newline" />

    </xsl:template>
    
    <!-- generates all predefined xmpp erros as functions -->
    <xsl:template match="msdl:interface/msdl:operation" mode="generateFaultFunctions">
    	<xsl:param name="className" />
    
    	<xsl:for-each select="./msdl:infault" >
			<xsl:variable name="faultRef" select="@ref" />
			<xsl:apply-templates select="/msdl:description/msdl:binding/msdl:fault[@ref = $faultRef]" mode="generateFunctionBuildFault" >
				<xsl:with-param name="className" select="$className" />
			</xsl:apply-templates>
			<xsl:value-of select="$newline" />
		</xsl:for-each>
    </xsl:template>
    
    <!-- generates a special fault function to simplify the usage of creating a predifend error message -->
    <xsl:template match="msdl:binding/msdl:fault" mode="generateFunctionBuildFault">
    	<xsl:param name="className" />
    	<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
    	
    	<xsl:text>public </xsl:text><xsl:value-of select="$className" />
    		<xsl:text> build</xsl:text><xsl:value-of select="substring-after(@ref,':')" /><xsl:text>(String detailedErrorText){</xsl:text>
    		
    		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
    		<xsl:value-of select="$className" /><xsl:text> fault = ( </xsl:text>
    			<xsl:value-of select="$className" /><xsl:text> )this.clone();</xsl:text>
    			
    		<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
    		
    		<xsl:text>fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( </xsl:text><xsl:value-of select="$xmppBeanClassName" /><xsl:text>.TYPE_ERROR );</xsl:text>
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
		
			<xsl:text>fault.errorType = "</xsl:text><xsl:value-of select="@xmpp:errortype" /><xsl:text>";</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			
			<xsl:text>fault.errorCondition = "</xsl:text><xsl:value-of select="@xmpp:errorcondition" /><xsl:text>";</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			
			<xsl:text>fault.errorText = "</xsl:text><xsl:value-of select="@xmpp:errortext" /><xsl:text>";</xsl:text>
			<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			
			<xsl:text>if(null != detailedErrorText &amp;&amp; detailedErrorText.length() &gt; 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}</xsl:text>
	
	<xsl:value-of select="$newline" />
    </xsl:template>
    
    <!-- generates all getters and setters -->
    <xsl:template match="xs:element" mode="generateGetSet" >
		<!-- Getter -->
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>public </xsl:text>
			<xsl:apply-templates select="." mode="parseElementDatatype"/>
		<xsl:text> get</xsl:text><xsl:value-of select="concat(translate(substring(./@name, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), substring(@name, 2))" /><xsl:text>() {</xsl:text>
	
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>return this.</xsl:text><xsl:value-of select="./@name" /><xsl:text>;</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
		
		<!-- Setter -->
		<xsl:value-of select="$newline" /><xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>public void set</xsl:text><xsl:value-of select="concat(translate(substring(./@name, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), substring(@name, 2))" /><xsl:text>( </xsl:text>
			<xsl:apply-templates select="." mode="parseElementDatatype"/><xsl:text> </xsl:text><xsl:value-of select="./@name" />
		<xsl:text> ) {</xsl:text>
	
			<xsl:value-of select="$newline" /><xsl:value-of select="$indent" /><xsl:value-of select="$indent" />
			<xsl:text>this.</xsl:text><xsl:value-of select="./@name" /><xsl:text> = </xsl:text><xsl:value-of select="./@name" /><xsl:text>;</xsl:text>
		
		<xsl:value-of select="$newline" /><xsl:value-of select="$indent" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />
		
    </xsl:template>
    
    
    
    
    
    
    <!-- generates the old known XMPPInfo interface -->
    <xsl:template match="/" mode="generateXMPPInfoInterface">
		<xsl:variable name="fileName" select="concat($outputFolder,'XMPPInfo.java')" />
			<xsl:value-of select="$fileName" />
			<xsl:result-document href="{$fileName}" >
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
			
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Benjamin Söllner, Robert Lübke
 */
public interface XMPPInfo extends Serializable {
	/**
	 * Parses the XML String of an XMPP stanza and saves all neccessary information. 
	 * @param parser XML Parser to use for parsing the XML String
	 * @throws Exception
	 */
	public void fromXML(XmlPullParser parser) throws Exception;
	/**
	 * Converts this XMPP stanza into its representation as XML string.
	 * @return XML string representation of this XMPP stanza 
	 */
	public String toXML(); 
	/**
	 * @return Child Element of this XMPP stanza
	 */
	public String getChildElement();
	/**
	 * @return Namespace of this XMPP stanza
	 */
	public String getNamespace();
}
		</xsl:result-document>
    </xsl:template>
    
    <!-- generates the old known XMPPBean class -->
    <xsl:template match="/" mode="generateXMPPBeanClass">
    <xsl:variable name="fileName" select="concat($outputFolder,$xmppBeanClassName,'.java')" />
			<xsl:value-of select="$fileName" />
			<xsl:result-document href="{$fileName}" >
			<xsl:if test="string-length($packageNamespace) > 0">
				<xsl:text>package </xsl:text><xsl:value-of select="$packageNamespace" /><xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" /><xsl:value-of select="$newline" />
			</xsl:if>
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Benjamin Söllner, Robert Lübke
 */
public abstract class <xsl:value-of select="$xmppBeanClassName" /> implements Cloneable, XMPPInfo {

	private static final long serialVersionUID = 1L;
	public static final int TYPE_SET = 0;
	public static final int TYPE_GET = 1;
	public static final int TYPE_RESULT = 2;
	public static final int TYPE_ERROR = 3;

	public static int currentId = 0;

	protected int type;
	protected String id;
	protected String from;
	protected String to;

	public String errorType, errorCondition, errorText;

	public void setType( int type ) {
		this.type = type;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public void setFrom( String from ) {
		this.from = from;
	}

	public void setTo( String to ) {
		this.to = to;
	}

	public int getType() {
		return this.type;
	}

	public String getId() {
		return this.id;
	}

	public String getFrom() {
		return this.from;
	}

	public String getTo() {
		return this.to;
	}

	public <xsl:value-of select="$xmppBeanClassName" />() {
		this.id = "mobilis_" + <xsl:value-of select="$xmppBeanClassName" />.currentId;
		<xsl:value-of select="$xmppBeanClassName" />.currentId++;
	}

	/**
	 * Constructor for type=ERROR. For more information about the parameters of
	 * an error IQ see http://xmpp.org/rfcs/rfc3920.html#stanzas.
	 * 
	 * @param errorType
	 *            Error type
	 * @param errorCondition
	 *            Error condition
	 * @param errorText
	 *            descriptive error text
	 */
	public <xsl:value-of select="$xmppBeanClassName" />(String errorType, String errorCondition, String errorText) {
		this.id = "mobilis_" + <xsl:value-of select="$xmppBeanClassName" />.currentId;
		<xsl:value-of select="$xmppBeanClassName" />.currentId++;

		this.errorType = errorType;
		this.errorCondition = errorCondition;
		this.errorText = errorText;
		this.type = <xsl:value-of select="$xmppBeanClassName" />.TYPE_ERROR;
	}

	/**
	 * Appends XML Payload information about an error to the given StringBuilder
	 * 
	 * @param sb
	 * @return the changed StringBuilder
	 */
	public StringBuilder appendErrorPayload( StringBuilder sb ) {
		// Error element:
		if ( this.errorCondition != null &amp;&amp; this.errorText != null &amp;&amp; this.errorType != null ) {
			sb.append( "&lt;error type=\"" + errorType + "\"&gt;" )
					.append(
							"&lt;" + errorCondition
									+ " xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\" /&gt;" )
					.append( "&lt;text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"&gt;" )
					.append( errorText ).append( "&lt;/text&gt;" ).append( "&lt;/error&gt;" );
		}
		return sb;
	}

	public <xsl:value-of select="$xmppBeanClassName" /> cloneBasicAttributes( <xsl:value-of select="$xmppBeanClassName" /> twin ) {
		twin.errorCondition = this.errorCondition;
		twin.errorText = this.errorText;
		twin.errorType = this.errorType;

		twin.id = this.id;
		twin.from = this.from;
		twin.to = this.to;
		twin.type = this.type;
		return twin;
	}

	/**
	 * Parses and saves the error attributes (type, condition and text).
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public XmlPullParser parseErrorAttributes( XmlPullParser parser )
			throws XmlPullParserException, IOException {
		if ( parser.getAttributeName( 0 ).equals( "type" ) )
			errorType = parser.getAttributeValue( 0 );
		parser.next();
		// Now the parser is at START_TAG of error condition
		errorCondition = parser.getName();
		parser.next();
		parser.next();
		// Now the parser is at START_TAG of error text
		errorText = parser.nextText();
		return parser;
	}

	public String toXML() {
		String childElement = this.getChildElement();
		String namespace = this.getNamespace();
		return new StringBuilder().append( "&lt;" ).append( childElement ).append( " xmlns=\"" )
				.append( namespace ).append( "\"&gt;" ).append( this.payloadToXML() ).append( "&lt;/" )
				.append( childElement ).append( "&gt;" ).toString();
	}

	public abstract <xsl:value-of select="$xmppBeanClassName" /> clone();

	/**
	 * Converts all payload information into XML format.
	 * 
	 * @return XML representation of the payload.
	 */
	public abstract String payloadToXML();

	public String toString() {
		String type = "no type";
		switch ( this.type ) {
		case <xsl:value-of select="$xmppBeanClassName" />.TYPE_GET:
			type = "GET";
			break;
		case <xsl:value-of select="$xmppBeanClassName" />.TYPE_SET:
			type = "SET";
			break;
		case <xsl:value-of select="$xmppBeanClassName" />.TYPE_RESULT:
			type = "RESULT";
			break;
		case <xsl:value-of select="$xmppBeanClassName" />.TYPE_ERROR:
			type = "ERROR";
			break;
		}
		return "packetID:" + id + " type:" + type + "childelement:" + this.getChildElement();
	}
}
		</xsl:result-document>
    </xsl:template>
	
</xsl:stylesheet>
