//
//  MXiServiceTypeDiscovery.m
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiServiceTypeDiscovery.h"
#import "MXiConnection.h"
#import "MXiService.h"

@interface MXiServiceTypeDiscovery ()

@property (nonatomic, weak, readwrite) MXiConnection *connection;

- (instancetype)initWithConnection:(MXiConnection *)connection andServiceType:(ServiceType)serviceType forNamespace:(NSString *)namespace;

@end

@implementation MXiServiceTypeDiscovery
{
    ServiceType _serviceType;
    __strong NSString *_namespace;
}

+ (instancetype)serviceTypeDiscoveryWithConnection:(MXiConnection *)connection andServiceType:(ServiceType)serviceType forNamespace:(NSString *)namespace
{
    return [[self alloc] initWithConnection:connection andServiceType:serviceType forNamespace:namespace];
}

- (instancetype)initWithConnection:(MXiConnection *)connection andServiceType:(ServiceType)serviceType forNamespace:(NSString *)namespace
{
    if (!connection)
        [NSException raise:NSInvalidArgumentException format:@"The connection must not be nil"];

    self = [super init];
    if (self) {
        self.connection = connection;

        _serviceType = serviceType;
        _namespace = namespace;

        [self.connection addStanzaDelegate:self
                              withSelector:@selector(didReceiveIQ:)
                          forStanzaElement:IQ];
    }

    return self;
}

- (void)dealloc
{
    [self.connection removeStanzaDelegate:self forStanzaElement:IQ];

    free((void *) _serviceType);
    _namespace = nil;
}

#pragma mark - Service Discovery

- (void)discoverServices
{
    NSXMLElement* discoElement =
    [NSXMLElement elementWithName:@"serviceDiscovery"
                            xmlns:CoordinatorService];
    NSXMLElement* iqElement = [NSXMLElement elementWithName:@"iq"];
    [iqElement addAttributeWithName:@"to"
                        stringValue:[self.connection coordinatorJID]];
    [iqElement addAttributeWithName:@"type" stringValue:@"get"];
    [iqElement addChild:discoElement];

    [self.connection sendElement:iqElement];
}

- (void)discoverServiceInstances
{
    NSXMLElement* namespaceElement =
    [NSXMLElement elementWithName:@"serviceNamespace"];
    [namespaceElement setStringValue:[self.connection serviceNamespace]];
    NSXMLElement* discoElement =
    [NSXMLElement elementWithName:@"serviceDiscovery"
                            xmlns:CoordinatorService];
    [discoElement addChild:namespaceElement];
    NSXMLElement* iqElement = [NSXMLElement elementWithName:@"iq"];
    [iqElement addAttributeWithName:@"to"
                        stringValue:[self.connection coordinatorJID]];
    [iqElement addAttributeWithName:@"type" stringValue:@"get"];
    [iqElement addChild:discoElement];

    [self.connection sendElement:iqElement];
}

- (void)didReceiveIQ:(XMPPIQ *)iq
{
    [self validateDiscoveredServices:[iq childElement]];
}

- (void)validateDiscoveredServices:(NSXMLElement *)serviceDiscoveryElement
{
    if ([[serviceDiscoveryElement name] isEqualToString:@"serviceDiscovery"]) {
        NSArray* discoveredServiceElements = [serviceDiscoveryElement children];
        if (_serviceType == SINGLE)
            [self singleModeServiceDetection:discoveredServiceElements];
        else [self multiModeServiceDetection:discoveredServiceElements];
    }
}
- (void)singleModeServiceDetection:(NSArray *)discoveredServiceElements
{
    if (discoveredServiceElements.count != 1) {
        NSError *error = [NSError errorWithDomain:@"Wrong number of single services discovered"
                                             code:2
                                         userInfo:nil];
        [self.delegate discoveryFinishedWithError:error];
    } else {
        NSXMLElement* discoveredServiceElement = [discoveredServiceElements firstObject];
        MXiService *service = [MXiService serviceWithName:[discoveredServiceElement attributeStringValueForName:@"serviceName"]
                                                namespace:[discoveredServiceElement attributeStringValueForName:@"namespace"]
                                                  version:[discoveredServiceElement attributeIntegerValueForName:@"version"]
                                                 jabberID:[discoveredServiceElement attributeStringValueForName:@"jid"]];
        [self.delegate serviceDiscovered:service];
        [self.delegate discoveryFinishedWithError:nil];
    }
}
- (void)multiModeServiceDetection:(NSArray *)discoveredServiceElements
{
    BOOL concreteServiceFound = NO;
    for (NSXMLElement *discoveredServiceElement in discoveredServiceElements)
        if ([[discoveredServiceElement attributeStringValueForName:@"namespace"] isEqualToString:_namespace])
            if ([discoveredServiceElement attributeStringValueForName:@"jid"] && [discoveredServiceElement attributeStringValueForName:@"serviceName"]) {
                MXiService *service = [MXiService serviceWithName:[discoveredServiceElement attributeStringValueForName:@"serviceName"]
                                                        namespace:[discoveredServiceElement attributeStringValueForName:@"namespace"]
                                                          version:[discoveredServiceElement attributeIntegerValueForName:@"version"]
                                                         jabberID:[discoveredServiceElement attributeStringValueForName:@"jid"]];
                [self.delegate serviceDiscovered:service];
                concreteServiceFound = YES;
            }
    if (!concreteServiceFound && discoveredServiceElements.count > 0)
        [self discoverServiceInstances];
    if (concreteServiceFound)
        [self.delegate discoveryFinishedWithError:nil];
}

@end
