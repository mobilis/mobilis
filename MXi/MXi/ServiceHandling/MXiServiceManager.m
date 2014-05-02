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
{
    struct {
        unsigned short serviceCreation : 1;
    } _stanzaDelegate;
}

@property (readwrite) NSArray *services;

@property (nonatomic, readwrite, weak) MXiConnection *connection;
@property (nonatomic, readwrite) ServiceType serviceType;
@property (nonatomic, readwrite) NSString *namespace;

@end

@implementation MXiServiceManager
{
    __strong MXiServiceTypeDiscovery *_serviceTypeDiscovery;
    __strong NSMutableArray *_delegates;
}

+ (instancetype)serviceManagerWithConnection:(MXiConnection *)connection serviceType:(ServiceType)serviceType namespace:(NSString *)namespace delegate:(id <MXiServiceManagerDelegate>)delegate
{
    return [[self alloc] initWithConnection:connection serviceType:serviceType namespace:namespace delegate:delegate];
}

- (instancetype)initWithConnection:(MXiConnection *)connection serviceType:(ServiceType)serviceType namespace:(NSString *)namespace delegate:(id<MXiServiceManagerDelegate>)delegate
{
    if (!connection) [NSException raise:NSInvalidArgumentException format:@"The connection object must not be nil"];
    if (!namespace || [namespace isEqualToString:@""]) [NSException raise:NSInvalidArgumentException format:@"The namespace must not be empty or nil"];

    self = [super init];
    if (self) {
        _delegates = [NSMutableArray arrayWithCapacity:5];
        [_delegates addObject:delegate];
        self.connection = connection;
        self.serviceType = serviceType;
        self.namespace = namespace;
    }

    [self launchServiceDiscovery];

    return self;
}

- (void)launchServiceDiscovery
{
    _serviceTypeDiscovery = [MXiServiceTypeDiscovery serviceTypeDiscoveryWithConnection:self.connection
                                                                         andServiceType:self.serviceType
                                                                           forNamespace:self.namespace];
    _serviceTypeDiscovery.delegate = self;

    if (self.serviceType == SINGLE) [_serviceTypeDiscovery discoverServiceInstances];
    else [_serviceTypeDiscovery discoverServices];
}

- (void)rediscoverServices
{
    self.services = [NSArray array];
    _serviceTypeDiscovery = nil;
    
    [self launchServiceDiscovery];
}

- (void)addDelegate:(id <MXiServiceManagerDelegate>)delegate
{
    if (![[delegate class] conformsToProtocol:@protocol(MXiServiceManagerDelegate)])
        return;
    @synchronized (_delegates) {
        if (![_delegates containsObject:delegate])
            [_delegates addObject:delegate];
    }
}

- (void)removeDelegate:(id <MXiServiceManagerDelegate>)delegate
{
    if (![[delegate class] conformsToProtocol:@protocol(MXiServiceManagerDelegate)])
        return;
    @synchronized (_delegates) {
        [_delegates removeObject:delegate];
    }
}


- (void)createServiceWithName:(NSString *)serviceName andPassword:(NSString *)password
{
    if (!_stanzaDelegate.serviceCreation)
    {
        [self.connection addStanzaDelegate:self withSelector:@selector(handleServiceResponse:) forStanzaElement:IQ];
        _stanzaDelegate.serviceCreation = 1;
    }
    NSXMLElement *serviceIQ;
    @autoreleasepool {
        serviceIQ = [NSXMLElement elementWithName:@"iq"];
        NSXMLElement *serviceBean = [NSXMLElement elementWithName:@"createNewServiceInstance"
                                                            xmlns:CoordinatorServiceNS];

        [serviceBean addChild:[[NSXMLElement alloc] initWithName:@"serviceNamespace" stringValue:self.namespace]];
        [serviceBean addChild:[[NSXMLElement alloc] initWithName:@"password" stringValue:password]];
        [serviceBean addChild:[[NSXMLElement alloc] initWithName:@"serviceName" stringValue:serviceName]];

        [serviceIQ addAttributeWithName:@"to" stringValue:self.connection.coordinatorJID];
        [serviceIQ addAttributeWithName:@"from" stringValue:self.connection.jabberID.full];
        [serviceIQ addAttributeWithName:@"type" stringValue:@"set"];

        [serviceIQ addChild:serviceBean];

    }
    if (serviceIQ) [self.connection sendElement:serviceIQ];
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
    for (id<MXiServiceManagerDelegate> delegate in _delegates)
        [delegate serviceDiscoveryFinishedWithError:error];
}

#pragma mark - Service Creation

- (void)handleServiceResponse:(XMPPIQ *)iq
{
    NSArray *iqChildren = [iq children];
    if (iqChildren.count != 1)
        return;

    if ([iq elementForName:@"sendNewServiceInstance"]) {
        NSXMLElement *element = [iq elementForName:@"sendNewServiceInstance"];
        NSString *serviceName = [[element elementForName:@"serviceName"] stringValue];
        NSString *serviceJID = [[element elementForName:@"jidOfNewService"] stringValue];
        NSString *serviceVersion = [[element elementForName:@"serviceVersion"] stringValue];

        MXiService *newService = [MXiService serviceWithName:serviceName
                                                   namespace:self.namespace
                                                     version:[serviceVersion integerValue]
                                                    jabberID:serviceJID];
        [self serviceDiscovered:newService];
        for (id<MXiServiceManagerDelegate> delegate in _delegates)
            if ([delegate respondsToSelector:@selector(createdServiceInstanceSuccessfully:)])
                [delegate createdServiceInstanceSuccessfully:newService];

        [self sendServiceCreationAcknowledgement];
    }
}
- (void)sendServiceCreationAcknowledgement
{
    [self.connection removeStanzaDelegate:self forStanzaElement:IQ];
    _stanzaDelegate.serviceCreation = 0;
    NSXMLElement *ackIQ = [NSXMLElement elementWithName:@"iq"];
    [ackIQ addAttributeWithName:@"to" stringValue:self.connection.coordinatorJID];
    [ackIQ addAttributeWithName:@"from" stringValue:self.connection.jabberID.full];
    [ackIQ addAttributeWithName:@"type" stringValue:@"result"];
    [ackIQ addChild:[[NSXMLElement alloc] initWithName:@"sendNewServiceInstance" xmlns:CoordinatorServiceNS]];

    [self.connection sendElement:ackIQ];
}

@end
