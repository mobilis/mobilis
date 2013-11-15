//
//  MXiService.m
//  MXi
//
//  Created by Martin Weißbach on 10/16/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiService.h"
#import "XMPPJID.h"

@implementation MXiService

+ (id)serviceWithName:(NSString *)serviceName namespace:(NSString *)serviceNamespace version:(NSInteger)serviceVersion jabberID:(NSString *)jabberID
{
    return [[self alloc] initWithName:serviceName namespace:serviceNamespace version:serviceVersion jabberID:jabberID];
}

- (id)initWithName:(NSString *)serviceName namespace:(NSString *)serviceNamespace version:(NSInteger)serviceVersion jabberID:(NSString *)jabberID
{
    self = [super init];
    if (self) {
        self.name = serviceName;
        self.namespace = serviceNamespace;
        self.serviceVersion = serviceVersion;
        self.jid = [XMPPJID jidWithString:jabberID];
    }

    return self;
}

- (BOOL)isEqalToService:(MXiService *)otherService
{
    if (otherService == nil || ![otherService isKindOfClass:[self class]])
        return NO;

    if (    [self.jid isEqualToJID:otherService.jid] &&
            [self.name isEqualToString:otherService.name] &&
            [self.namespace isEqualToString:otherService.namespace] &&
            self.serviceVersion == otherService.serviceVersion) {
        return YES;
    } else return NO;
}


@end
