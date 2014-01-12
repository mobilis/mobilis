//
//  ConnectionHandler.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "MXiConnectionHandler.h"

#import "MXiConnection.h"

@implementation MXiConnectionHandler

#pragma mark - Singleton stack

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    __strong static MXiConnectionHandler *shared = nil;
    dispatch_once(&onceToken, ^{
        shared = [(MXiConnectionHandler *)[super allocWithZone:NULL] initUniqueInstance];
    });
    return shared;
}

+ (id)allocWithZone:(struct _NSZone *)zone
{
    return [self sharedInstance];
}

- (id)initUniqueInstance
{
    return [self init];
}

@end
