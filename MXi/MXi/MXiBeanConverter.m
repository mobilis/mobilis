//
//  MXiBeanToXML.m
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiBeanConverter.h"

@implementation MXiBeanConverter

+ (NSXMLElement *)beanToIQ:(MXiBean *)outBean {
	NSXMLElement* beanElement = [outBean toXML];

    NSXMLElement* iq = [[MXiBeanConverter class] p_headerFromOutBean:outBean forStanzaElement:IQ];
	
	[iq addChild:beanElement];
	return iq;
}

+ (NSXMLElement *)beanToMessage:(MXiBean *)outBean
{
    NSXMLElement *beanElement = [outBean toXML];

    NSXMLElement *message = [[MXiBeanConverter class] p_headerFromOutBean:outBean forStanzaElement:MESSAGE];

    NSXMLElement *body = [NSXMLElement elementWithName:@"body"];

    [body addChild:beanElement];
    [message addChild:body];
    return message;
}

+ (void)beanFromIQ:(XMPPIQ *)iq
		   intoBean:(MXiBean *)inBean {
	[inBean setTo:[XMPPJID jidWithString:[iq attributeStringValueForName:@"to"]]];
	[inBean setFrom:[XMPPJID jidWithString:[iq attributeStringValueForName:@"from"]]];

	[inBean fromXML:[iq childElement]];
}

+ (void)beanFromMessage:(XMPPMessage *)message intoBean:(MXiBean *)inBean
{
    // TODO if it is working like this, merge to messages to one
    [inBean setTo:[XMPPJID jidWithString:[message attributeStringValueForName:@"to"]]];
    [inBean setFrom:[XMPPJID jidWithString:[message attributeStringValueForName:@"from"]]];

    NSError *error = nil;
    [inBean fromXML:[[NSXMLElement alloc] initWithXMLString:message.body error:&error]]; // TODO check if this is working
    if (error != nil)
    {
        inBean = nil; // TODO guess this is not allowed from a mm perspective, it looks ugly nonetheless
    }
}

#pragma mark - Helper

+ (NSXMLElement *)p_headerFromOutBean:(MXiBean *)outBean forStanzaElement:(StanzaElement)stanzaElement
{
    NSXMLElement *headerElement = nil;
    switch (stanzaElement) // No default case is intentional to get compiler warnings when enumeration has grown.
    {
        case IQ:
            headerElement = [NSXMLElement elementWithName:@"iq"];
            break;
        case MESSAGE:
            headerElement = [NSXMLElement elementWithName:@"message"];
            break;
        case UNKNOWN_STANZA:
        case PRESENCE:
            break;
    }

    [headerElement addAttributeWithName:@"to" stringValue:[[outBean to] full]];
    [headerElement addAttributeWithName:@"from" stringValue:[[outBean from] full]];
    [headerElement addAttributeWithName:@"type" stringValue:[MXiIQTypeLookup stringValueForIQType:[outBean beanType]]];

    return headerElement;
}

@end
