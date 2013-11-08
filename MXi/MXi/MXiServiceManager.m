//
//  MXiServiceManager.m
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiServiceManager.h"
#import "MXiServiceTypeDiscovery.h"
#import "MXiConnection.h"

@interface MXiServiceManager () <MXiServiceTypeDiscoveryDelegate>

@property (readwrite) NSArray *services;

@property (nonatomic, readwrite, weak) MXiConnection *connection;
@property (nonatomic, readwrite) ServiceType serviceType;
@property (nonatomic, readwrite) NSString *namespace;

@end

@implementation MXiServiceManager
{
    __strong MXiServiceTypeDiscovery *_serviceTypeDiscovery;
}

+ (instancetype)serviceManagerWithConnection:(MXiConnection *)connection serviceType:(ServiceType)serviceType namespace:(NSString *)namespace
{
    return [[self alloc] initWithConnection:connection serviceType:serviceType namespace:namespace];
}

- (instancetype)initWithConnection:(MXiConnection *)connection serviceType:(ServiceType)serviceType namespace:(NSString *)namespace
{
    if (!connection) [NSException raise:NSInvalidArgumentException format:@"The connection object must not be nil"];
    if (!namespace || [namespace isEqualToString:@""]) [NSException raise:NSInvalidArgumentException format:@"The namespace must not be empty or nil"];

    self = [super init];
    if (self) {
        self.connection = connection;
        self.serviceType = serviceType;
        self.namespace = namespace;
    }

    return self;
}

- (void)launchServiceDiscoery
{
    _serviceTypeDiscovery = [MXiServiceTypeDiscovery serviceTypeDiscoveryWithConnection:self.connection
                                                                         andServiceType:self.serviceType
                                                                           forNamespace:self.namespace];
    _serviceTypeDiscovery.delegate = self;

    if (self.serviceType == SINGLE) [_serviceTypeDiscovery discoverServiceInstances];
    else [_serviceTypeDiscovery discoverServices];
}

#pragma mark - MXiServiceTypeDiscovery

- (void)serviceDiscovered:(MXiService *)service
{
    NSMutableArray *tempArray = [NSMutableArray arrayWithCapacity:self.services.count + 1];
    [tempArray addObjectsFromArray:self.services];
    [tempArray addObject:service];

    self.services = [NSArray arrayWithArray:tempArray];
}

- (void)discoveryFinishedWithError:(NSError *)error
{
    [self.delegate serviceDiscoveryFinishedWithError:error];
}

@end
