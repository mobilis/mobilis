//
//  ConnectionHandler.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

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
@property BOOL connected;

@property (nonatomic) dispatch_queue_t discoveryQueue;

- (NSArray *)allIncomingBeans;
- (void)clearOutgoingBeanQueue;

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

    self.serviceManager = [MXiServiceManager serviceManagerWithConnection:self.connection
                                                              serviceType:serviceType
                                                                namespace:[settings valueForKey:SERVICE_NAMESPACE]];
}

- (void)reconnectWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName port:(NSNumber *)port
{
    _authenticated = NO;
    _connected = NO;

    DefaultSettings *settings = [DefaultSettings defaultSettings];
    [self.connection reconnectWithJabberID:jabberID
                                  password:password
                                  hostname:hostName
                                      port:[port integerValue]
                            coordinatorJID:[NSString stringWithFormat:@"%@@%@/Coordinator", [settings valueForKey:SERVER_USERNAME], hostName]
                          serviceNamespace:[settings valueForKey:SERVICE_NAMESPACE]];
}

- (void)sendBean:(MXiBean<MXiOutgoingBean> *)outgoingBean
{
    if (self.connection && outgoingBean) {
        if (_authenticated && _connected) {
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
    [self.delegate connectionDidDisconnect:error];
}

- (void)connectionAuthenticationFinished:(NSXMLElement *)error
{
    if (!error)
        [self.delegate authenticationFinishedSuccessfully:YES];
    else [self.delegate authenticationFinishedSuccessfully:NO];
}

#pragma mark - Multi User Chat Support

- (void)discoverMultiUserChatRoomsInDomain:(NSString *)domain withCompletionBlock:(DiscoveryCompletionBlock)completionBlock
{
    MXiMultiUserChatDiscovery *discovery = [[MXiMultiUserChatDiscovery alloc] initWithDomainName:domain
                                                                              andCompletionBlock:completionBlock];
    self.multiUserChatDiscovery = discovery;

    if (!self.discoveryQueue) {
        self.discoveryQueue = dispatch_queue_create("mucDiscoveryQueue", DISPATCH_QUEUE_CONCURRENT);
    }
    [discovery startDiscoveryOnQueue:self.discoveryQueue];
}

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

- (void)clearOutgoingBeanQueue
{
    if (self.outgoingBeanQueue && self.outgoingBeanQueue.count > 0) {
        for (MXiBean<MXiOutgoingBean> *outgoing in self.outgoingBeanQueue) {
            outgoing.to = [XMPPJID jidWithString:self.connection.serviceJID];
            [self sendBean:outgoing];
        }
    }
}

- (void)clearOutgoingBeanQueueWithServiceJID:(NSString *)serviceJID
{
    if (self.outgoingBeanQueue && self.outgoingBeanQueue.count > 0) {
        for (MXiBean<MXiOutgoingBean> *outgoing in self.outgoingBeanQueue) {
            outgoing.to = [XMPPJID jidWithString:serviceJID];
            [self sendBean:outgoing];
        }
    }
}

- (void)serviceDiscoveryFinishedWithError:(NSError *)error
{
    #error Unimplemented Method
}

- (void)createdServiceInstanceSuccessfully:(MXiService *)service
{
    #error Unimplemented Method
}

@end
