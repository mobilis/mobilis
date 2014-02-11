//
//  MobilisService.m
//  ACDSenseService4Mac
//
//  Created by Martin Weissbach on 28/12/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <XMPPFramework/XMPPIQ.h>
#import <XMPPFramework/NSXMLElement+XMPP.h>
#import "MobilisService.h"
#import "MXiServiceConnectionHandler.h"
#import "MXiConnection.h"

@interface MobilisService () <MXiConnectionHandlerDelegate>

@property (nonatomic, readwrite) MXiServiceConnectionHandler *connectionHandler;

@end

@implementation MobilisService

- (void)dealloc
{
    [self.connectionHandler.connection removeStanzaDelegate:self forStanzaElement:IQ];
}

- (instancetype)initServiceWithJID:(XMPPJID *)jid password:(NSString *)password hostName:(NSString *)hostName port:(NSNumber *)port
{
    self = [super init];
    if (self) {
        self.connectionHandler = [MXiServiceConnectionHandler new];
        self.connectionHandler.delegate = self;

        [self.connectionHandler launchConnectionWithJID:jid.bare
                                               password:password
                                               hostName:hostName
                                            serviceType:SERVICE
                                                   port:port];
    }

    return self;
}

#pragma mark - MXiConnectionHandlerDelegate

- (void)authenticationFinishedSuccessfully:(BOOL)authenticationState
{
    [self.connectionHandler.connection addStanzaDelegate:self
                                            withSelector:@selector(iqStanzaReceived:)
                                        forStanzaElement:IQ];
}

- (void)connectionDidDisconnect:(NSError *)error
{

}

- (void)serviceDiscoveryError:(NSError *)error
{

}

#pragma mark - Getter / Setter

- (void)setServiceType:(ServiceType)serviceType
{
    if (serviceType == SERVICE || serviceType == RUNTIME)
        @throw [NSException exceptionWithName:@"Invalid Service Type"
                                       reason:@"Service type must either be SINGLE or MULTI"
                                     userInfo:nil];

    if (serviceType != _serviceType)
        _serviceType = serviceType;
}

#pragma mark - Stanza Processing

- (void)iqStanzaReceived:(XMPPIQ *)xmppiq
{
    if (    [xmppiq isGetIQ] &&
            [[xmppiq childElement] namespaces].count > 0 &&
            [[[[xmppiq childElement] namespaces][0] stringValue] isEqualToString:@"http://jabber.org/protocol/disco#info"]
            ) {
        NSXMLElement *resultIQBody = [[NSXMLElement alloc] initWithName:@"query" xmlns:@"http://jabber.org/protocol/disco#info"];

        NSXMLElement *serviceFeatureString = [[NSXMLElement alloc] initWithName:@"feature"];
        [serviceFeatureString addAttributeWithName:@"var" stringValue:[self buildServiceFeatureString]];
        NSXMLElement *serviceInstanceFeatureString = [[NSXMLElement alloc] initWithName:@"feature"];
        [serviceInstanceFeatureString addAttributeWithName:@"var" stringValue:[self buildServiceInstanceFeatureString]];

        // TODO: figure out which feature strings are exactly required for single and multi.
//        [resultIQBody addChild:serviceFeatureString]; // obviously not required for SINGLE-services
        [resultIQBody addChild:serviceInstanceFeatureString];

        XMPPIQ *responseIQ = [XMPPIQ iqWithType:@"result"
                                             to:[xmppiq from]
                                      elementID:[xmppiq elementID]
                                          child:resultIQBody];
        [[MXiConnectionHandler sharedInstance] sendElement:responseIQ];
    }
}

- (NSString *)buildServiceFeatureString
{
    NSMutableString *featureString = [NSMutableString stringWithFormat:@"http://mobilis.inf.tu-dresden.de"];
    [featureString appendFormat:@"/service#servicenamespace=%@,", self.serviceNamespace];
    [featureString appendFormat:@"version=%i,", [self.serviceVersion intValue]];
    [featureString appendFormat:@"mode=%@,", [self serviceTypeAsString]];
    [featureString appendFormat:@"rt=%@", [MXiConnectionHandler sharedInstance].connection.jabberID.bareJID];
    return featureString;
}
- (NSString *)serviceTypeAsString
{
    if (self.serviceType == SINGLE)
        return @"single";
    if (self.serviceType == MULTI)
        return @"multi";

    @throw [NSException exceptionWithName:@"Invalid Service Type."
                                   reason:@"Service Type is neither single nor multi"
                                 userInfo:nil];
}

- (NSString *)buildServiceInstanceFeatureString
{
    NSMutableString *featureString = [NSMutableString stringWithFormat:@"http://mobilis.inf.tu-dresden.de"];
    [featureString appendFormat:@"/instance#servicenamespace=%@,", self.serviceNamespace];
    [featureString appendFormat:@"version=%i,", [self.serviceVersion intValue]];
    [featureString appendFormat:@"name=%@,", self.serviceName];
    [featureString appendFormat:@"rt=%@", [MXiConnectionHandler sharedInstance].connection.jabberID.bareJID];
    return featureString;
}

@end
