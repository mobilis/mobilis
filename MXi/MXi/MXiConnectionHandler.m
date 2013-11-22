//
//  ConnectionHandler.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <MobilisMXi/MXi/MXiBean.h>
#import "MXiConnectionHandler.h"

#import "IncomingBeanDetection.h"
#import "DefaultSettings.h"
#import "MXiMultiUserChatDiscovery.h"
#import "MXiServiceManager.h"
#import "MXiConnection.h"

@interface MXiConnectionHandler () <MXiConnectionDelegate, MXiServiceManagerDelegate>

@property MXiConnection *connection;
@property MXiServiceManager *serviceManager;
@property MXiMultiUserChatDiscovery *multiUserChatDiscovery;

@property (strong, nonatomic) NSArray *incomingBeans;
@property (strong, nonatomic) NSMutableArray *outgoingBeanQueue;

@property BOOL authenticated;

@property (nonatomic) dispatch_queue_t discoveryQueue;

- (NSArray *)allIncomingBeans;

@end

@implementation MXiConnectionHandler

#pragma mark - Singleton stack

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    __strong static MXiConnectionHandler *shared = nil;
    dispatch_once(&onceToken, ^{
        shared = [[super allocWithZone:NULL] initUniqueInstance];
    });
    return shared;
}

+ (id)allocWithZone:(struct _NSZone *)zone
{
    return [self sharedInstance];
}

- (instancetype)initUniqueInstance
{
    return [self init];
}

#pragma mark - Connection Handling

- (void)launchConnectionWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName serviceType:(ServiceType)serviceType port:(NSNumber *)hostPort
{

    DefaultSettings *settings = [DefaultSettings defaultSettings];


    self.connection = [MXiConnection connectionWithJabberID:jabberID password:password hostName:hostName port:[hostPort intValue] coordinatorJID:[NSString stringWithFormat:@"%@@%@/Coordinator", [settings valueForKey:SERVER_USERNAME], hostName] serviceNamespace:[settings valueForKey:SERVICE_NAMESPACE] serviceType:serviceType listeningForIncomingBeans:[self allIncomingBeans] connectionDelegate:self];
}

- (void)reconnectWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName port:(NSNumber *)port
{
    _authenticated = NO;

    DefaultSettings *settings = [DefaultSettings defaultSettings];
    [self.connection reconnectWithJabberID:jabberID
                                  password:password
                                  hostname:hostName
                                      port:[port integerValue]
                            coordinatorJID:[NSString stringWithFormat:@"%@@%@/Coordinator", [settings valueForKey:SERVER_USERNAME], hostName]
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
        self.serviceManager = [MXiServiceManager serviceManagerWithConnection:self.connection
                                                                  serviceType:self.connection.serviceType
                                                                    namespace:self.connection.serviceNamespace];
        [self.serviceManager addDelegate:self];
        [self.delegate authenticationFinishedSuccessfully:YES];
    } else [self.delegate authenticationFinishedSuccessfully:NO];
}

#pragma mark - Multi User Chat Support

- (void)connectToMultiUserChatRoom:(NSString *)roomJID
                      withDelegate:(id <MXiMultiUserChatDelegate>)delegate
{
    if (!self.connection.mucDelegate && !delegate)
        @throw [NSException exceptionWithName:@"No delegate set."
                                       reason:@"Please specify a delegate first"
                                     userInfo:nil];
    else if (delegate)
        self.connection.mucDelegate = delegate;

    [self.connection connectToMultiUserChatRoom:roomJID];
}

- (void)leaveMultiUserChatRoom:(NSString *)roomJID
{
    [self.connection leaveMultiUserChatRoom:roomJID];
}

- (void)sendMessage:(NSString *)message toRoom:(NSString *)roomJID
{
    [self.connection sendMessage:message toRoom:roomJID];
}

- (void)sendMessage:(NSString *)message toRoom:(NSString *)roomJID toUser:(NSString *)userName
{
    [self.connection sendMessage:message toRoom:[NSString stringWithFormat:@"%@/%@", roomJID, userName]];
}

- (BOOL)isMultiUserChatDelegateSet
{
    return self.connection.mucDelegate != nil;
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
