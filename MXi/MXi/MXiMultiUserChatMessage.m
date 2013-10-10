//
//  MXiMultiUserChatMessage.m
//  MXi
//
//  Created by Martin Weißbach on 8/31/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiMultiUserChatMessage.h"

@implementation MXiMultiUserChatMessage

+ (id)messageWithBody:(NSString *)body
{
    return [[self alloc] initWithBody:body];
}

- (id)initWithBody:(NSString *)body
{
    self = [super init];
    
    NSXMLElement *bodyElement = [NSXMLElement elementWithName:@"body" stringValue:body];
    
    [self addChild:bodyElement];
    
    return self;
}

- (NSString *)bodyContent
{
    return [[self elementForName:@"body"] stringValue];
}


@end
