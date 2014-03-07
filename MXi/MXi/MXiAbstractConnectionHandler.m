//
// Created by Martin Weissbach on 28/12/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiAbstractConnectionHandler.h"
#import "MXiService.h"
#import "MXiOutgoingBean.h"
#import "MXiServiceManager.h"
#import "MXiConnection.h"
#import "DefaultSettings.h"
#import "IncomingBeanDetection.h"


@interface MXiAbstractConnectionHandler() <MXiConnectionDelegate>

@property MXiConnection *connection;
@property MXiServiceManager *serviceManager;

@property (strong, nonatomic) NSArray *incomingBeans;
@property (strong, nonatomic) NSMutableArray *outgoingBeanQueue;

@property BOOL authenticated;

- (NSArray *)allIncomingBeans;

@end

@implementation MXiAbstractConnectionHandler

#pragma mark - Connection Handling

- (void)launchConnectionWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName runtimeName:(NSString *)runtimeName serviceType:(ServiceType)serviceType port:(NSNumber *)hostPort
{

    DefaultSettings *settings = [DefaultSettings defaultSettings];
    self.connection = [MXiConnection connectionWithJabberID:jabberID
                                                   password:password
                                                   hostName:hostName
                                                       port:[hostPort intValue]
                                             coordinatorJID:[NSString stringWithFormat:@"%@@%@/%@", runtimeName, hostName, CoordinatorResourceName]
                                           serviceNamespace:[settings valueForKey:SERVICE_NAMESPACE]
                                                serviceType:serviceType
                                  listeningForIncomingBeans:[self allIncomingBeans]
                                         connectionDelegate:self];
}

- (void)reconnectWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName runtimeName:(NSString *)runtimeName port:(NSNumber *)port
{
    _authenticated = NO;

    DefaultSettings *settings = [DefaultSettings defaultSettings];
    [self.connection reconnectWithJabberID:jabberID
                                  password:password
                                  hostname:hostName
                                      port:[port integerValue]
                            coordinatorJID:[NSString stringWithFormat:@"%@@%@/%@", runtimeName, hostName, CoordinatorResourceName]
                          serviceNamespace:[settings valueForKey:SERVICE_NAMESPACE]];
}

- (void)sendBean:(MXiBean <MXiOutgoingBean> *)outgoingBean toService:(MXiService *)service
{
    if (self.connection && outgoingBean) {
        if (_authenticated) {
            if (!service)
                outgoingBean.to = ((MXiService *)[self.serviceManager.services firstObject]).jid;
            else
                outgoingBean.to = service.jid;
            [self.connection sendBean:outgoingBean];
        } else {
            if (!self.outgoingBeanQueue) {
                self.outgoingBeanQueue = [NSMutableArray arrayWithCapacity:10];
            }
            [self.outgoingBeanQueue addObject:outgoingBean];
        }
    }
}

- (void)sendElement:(NSXMLElement *)element
{
    [self.connection sendElement:element];
}

- (void)sendMessageString:(NSString *)messageString toJID:(NSString *)jid
{
    NSXMLElement *body = [NSXMLElement elementWithName:@"body" stringValue:messageString];
    NSXMLElement *message = [NSXMLElement elementWithName:@"message"
                                                 children:@[body]
                                               attributes:@[[NSXMLNode attributeWithName:@"to" stringValue:jid],
                                                       [NSXMLNode attributeWithName:@"type" stringValue:@"chat"],
                                                       [NSXMLNode attributeWithName:@"from" stringValue:[self.connection.jabberID full]]]];
    [self sendElement:message];
}

- (void)sendMessageXML:(NSXMLElement *)messageElement toJID:(NSString *)jid
{
    [self sendMessageString:[messageElement XMLString] toJID:jid];
}

#pragma mark - MXiConnectionDelegate

- (void)connectionDidDisconnect:(NSError *)error
{
    _authenticated = NO;
    [self.delegate connectionDidDisconnect:error];
}

- (void)connectionAuthenticationFinished:(NSXMLElement *)error
{
    if (!error) {
        _authenticated = YES;
        NSLog(@"%d", self.connection.serviceType);
        if (self.connection.serviceType != RUNTIME && self.connection.serviceType != SERVICE)
            self.serviceManager = [MXiServiceManager serviceManagerWithConnection:self.connection
                                                                      serviceType:self.connection.serviceType
                                                                        namespace:self.connection.serviceNamespace
                                                                         delegate:self];
        [self.delegate authenticationFinishedSuccessfully:YES];
    } else [self.delegate authenticationFinishedSuccessfully:NO];
}

#pragma mark - Private Helper

- (NSArray *)allIncomingBeans
{
    if (self.incomingBeans) {
        return self.incomingBeans;
    }
    IncomingBeanDetection *incomingBeans = [IncomingBeanDetection new];
    self.incomingBeans = [incomingBeans detectBeans];

    return self.incomingBeans;
}

- (void)clearOutgoingBeanQueueWithServiceJID:(NSString *)serviceJID
{
    if (self.outgoingBeanQueue && self.outgoingBeanQueue.count > 0) {
        for (MXiBean<MXiOutgoingBean> *outgoing in self.outgoingBeanQueue) {
            outgoing.to = [XMPPJID jidWithString:serviceJID];
            [self sendBean:outgoing toService:nil ];
        }
    }
}

#pragma mark - MXiServiceManagerDelegate

- (void)serviceDiscoveryFinishedWithError:(NSError *)error
{
    [self.delegate serviceDiscoveryError:error];
}


@end