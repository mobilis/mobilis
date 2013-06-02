//
//  MXiBeanToXML.m
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiBeanToXML.h"

@implementation MXiBeanToXML

+ (NSXMLElement *)beanToXML:(MXiBean<MXiOutgoingBean> *)outBean {
	NSXMLElement* beanElement = [NSXMLElement elementWithName:[outBean elementName] xmlns:[outBean iqNamespace]];
	NSXMLElement* payload = [outBean payloadToXML];
	if (payload) {
		[beanElement addChild:[outBean payloadToXML]];
	}
	
	NSXMLElement* iq = [NSXMLElement elementWithName:@"iq" xmlns:[outBean iqNamespace]];
	[iq addAttributeWithName:@"to" stringValue:[[outBean to] full]];
	[iq addAttributeWithName:@"from" stringValue:[[outBean from] full]];
	[iq addAttributeWithName:@"type" stringValue:[MXiIQTypeLookup stringValueForIQType:[outBean beanType]]];
	
	[iq addChild:beanElement];
	return iq;
}

@end
