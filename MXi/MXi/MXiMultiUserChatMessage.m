//
//  MXiMultiUserChatMessage.m
//  MXi
//
//  Created by Martin Weißbach on 8/31/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiMultiUserChatMessage.h"

@implementation MXiMultiUserChatMessage

+ (id)initWithBody:(NSString *)body
{
    XMPPMessage *message = [XMPPMessage message];
    NSXMLElement *bodyElement = [NSXMLElement elementWithName:@"body" stringValue:body];
    
    [message addChild:bodyElement];
    
    return message;
}

@end
