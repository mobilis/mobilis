//
//  MXiBeanToXML.m
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiBeanConverter.h"

@implementation MXiBeanConverter

+ (NSXMLElement *)beanToIQ:(MXiBean<MXiOutgoingBean> *)outBean {
	NSXMLElement* beanElement = [outBean toXML];
	
	NSXMLElement* iq = [NSXMLElement elementWithName:@"iq" xmlns:[[outBean class] iqNamespace]];
	[iq addAttributeWithName:@"to" stringValue:[[outBean to] full]];
	[iq addAttributeWithName:@"from" stringValue:[[outBean from] full]];
	[iq addAttributeWithName:@"type" stringValue:[MXiIQTypeLookup stringValueForIQType:[outBean beanType]]];
	
	[iq addChild:beanElement];
	return iq;
}

+ (void)beanFromIQ:(XMPPIQ *)iq
		   intoBean:(MXiBean<MXiIncomingBean> *)inBean {
	[inBean setTo:[XMPPJID jidWithString:[iq attributeStringValueForName:@"to"]]];
	[inBean setFrom:[XMPPJID jidWithString:[iq attributeStringValueForName:@"from"]]];
	
	[inBean fromXML:[iq childElement]];
}

@end
