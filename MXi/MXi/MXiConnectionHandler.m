//
//  ConnectionHandler.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "MXiConnectionHandler.h"

#import "MXiDelegateDictionary.h"
#import "MXiDelegateSelectorMapping.h"
#import "IncomingBeanDetection.h"
#import "DefaultSettings.h"
#import "MXiMultiUserChatDiscovery.h"
#import "MXiServiceManager.h"

@interface MXiConnectionHandler ()

@property (strong, nonatomic) MXiConnection *connection;
@property (strong, nonatomic) NSArray *incomingBeans;
@property (strong, nonatomic) NSMutableArray *outgoingBeanQueue;

@property BOOL authenticated;
@property BOOL connected;

@property (strong, nonatomic) NSMutableArray *delegates;
@property (strong, nonatomic) NSMutableArray *stanzaDelegates;

@property (copy, nonatomic) AuthenticationBlock authenticationBlock;
@property (copy, nonatomic) ServiceCreateCompletionBlock serviceCreateCompletionBlock;

@property (nonatomic) dispatch_queue_t discoveryQueue;

- (NSArray *)allIncomingBeans;
- (void)clearOutgoingBeanQueue;
- (void)addServiceInstance:(MXiService *)service;

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
    self.delegates = [NSMutableArray arrayWithCapacity:10];
    return [self init];
}

#pragma mark - Connection Handling

- (void)launchConnectionWithJID:(NSString *)jabberID
                       password:(NSString *)password
                       hostName:(NSString *)hostName
                    serviceType:(ServiceType)serviceType
                           port:(NSNumber *)hostPort
            authenticationBlock:(AuthenticationBlock)authentication
{

    DefaultSettings *settings = [DefaultSettings defaultSettings];


    self.connection = [MXiConnection connectionWithJabberID:jabberID
                                                   password:password
                                                   hostName:hostName
                                                       port:[hostPort intValue]
                                             coordinatorJID:[NSString stringWithFormat:@"%@@%@/Coordinator", [settings valueForKey:SERVER_USERNAME], hostName]
                                           serviceNamespace:[settings valueForKey:SERVICE_NAMESPACE]
                                                serviceType:serviceType
                                           presenceDelegate:self
                                             stanzaDelegate:self
                                               beanDelegate:self
                                  listeningForIncomingBeans:[self allIncomingBeans]];

    self.serviceManager = [MXiServiceManager serviceManagerWithConnection:self.connection
                                                              serviceType:serviceType
                                                                namespace:[settings valueForKey:SERVICE_NAMESPACE]];
    self.authenticationBlock = authentication;
}

- (void)reconnectWithJID:(NSString *)jabberID
                password:(NSString *)password
                hostName:(NSString *)hostName
                    port:(NSNumber *)port
     authenticationBlock:(AuthenticationBlock)authentication
{
    _authenticated = NO;
    _connected = NO;
    
    self.authenticationBlock = authentication;
    DefaultSettings *settings = [DefaultSettings defaultSettings];
    [self.connection reconnectWithJabberID:jabberID
                                  password:password
                                  hostname:hostName
                                      port:[port integerValue]
                            coordinatorJID:[NSString stringWithFormat:@"%@@%@/Coordinator", [settings valueForKey:SERVER_USERNAME], hostName]
                          serviceNamespace:[settings valueForKey:SERVICE_NAMESPACE]];
}

- (void)createServiceWithName:(NSString *)serviceName completionBlock:(ServiceCreateCompletionBlock)completionBlock
{
    self.serviceCreateCompletionBlock = completionBlock;
    [self.connection createServiceInstanceWithServiceName:serviceName
                                          servicePassword:nil
                                         serviceNamespace:[self.connection serviceNamespace]];
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

#pragma mark ConnectionHandler Delegation Methods

- (void)addStanzaDelegate:(id)delegate
{
    if (!delegate)
        return;
    if (!self.stanzaDelegates) {
        self.stanzaDelegates = [NSMutableArray arrayWithCapacity:10];
    }

    [self.stanzaDelegates addObject:delegate];
}

- (void)removeStanzaDelegate:(id)delegate
{
    [self.stanzaDelegates removeObject:delegate];
}

#pragma mark - MXiBeanDelegate

- (void)didReceiveBean:(MXiBean<MXiIncomingBean> *)theBean
{
    NSArray *delegates = [[MXiDelegateDictionary sharedInstance] delegatesForBeanClass:[theBean class]];
    if (delegates) {
        for (MXiDelegateSelectorMapping *mapping in delegates) {
            if ([mapping.delegate respondsToSelector:[mapping selector]]) {
                [mapping.delegate performSelector:[mapping selector] withObject:theBean]; // Warning can be ignored.
            }
        }
    }
}

- (void)addDelegate:(id <MXiConnectionServiceStateDelegate>)delegate
{
    @synchronized (_delegates) {
        [self.delegates addObject:delegate];
    }
}

- (void)removeDelegate:(id <MXiConnectionServiceStateDelegate>)delegate
{
    @synchronized (_delegates) {
        [self.delegates removeObject:delegate];
    }
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


#pragma mark - MXiPresenceDelegate

- (void)didAuthenticate
{
    self.authenticated = YES;
}

- (void)didDiscoverServiceWithNamespace:(NSString *)serviceNamespace
                                   name:(NSString *)serviceName
                                version:(NSInteger)version
                             atJabberID:(NSString *)serviceJID
{
    self.authenticationBlock(_authenticated);
    _connected = YES;

    [self addServiceInstance:[MXiService serviceWithName:serviceName
                                               namespace:serviceNamespace
                                                 version:version
                                                jabberID:serviceJID]];
    [self clearOutgoingBeanQueueWithServiceJID:serviceJID];
}

- (void)didDisconnectWithError:(NSError *)error
{
#warning didDisconnectWithError: not implemented
    // TODO: figure out how this could best be handled
    // View that has initialized the connection might not be visible or allocated anymore
    _connected = NO;
    _authenticated = NO;
    self.connection = nil;
}

- (void)didFailToAuthenticate:(NSXMLElement *)error
{
    self.authenticationBlock(NO);
}

- (void)didCreateServiceWithJabberID:(NSString *)jabberID andVersion:(NSString *)version
{
    [self.connection discoverServiceInstances];
    self.serviceCreateCompletionBlock(jabberID);
}

#pragma mark - MXiStanzaDelegate

- (void)didReceiveMessage:(XMPPMessage *)message
{
#warning incomplete implementation
    for (id delegate in self.stanzaDelegates)
        if ([delegate respondsToSelector:@selector(messageStanzaReceived:)])
            [delegate performSelectorOnMainThread:@selector(messageStanzaReceived:)
                                       withObject:message
                                    waitUntilDone:NO];
}

- (BOOL)didReceiveIQ:(XMPPIQ *)iq
{
    if (self.multiUserChatDiscovery)
        [self.multiUserChatDiscovery didReceiveIQ:iq];

    for (id delegate in self.stanzaDelegates)
        if ([delegate respondsToSelector:@selector(iqStanzaReceived:)])
            [delegate performSelectorOnMainThread:@selector(iqStanzaReceived:)
                                       withObject:iq
                                    waitUntilDone:NO];

    return YES;
}

- (void)didReceivePresence:(XMPPPresence *)presence
{
    if ([[[presence from] full] isEqualToString:[[self connection] serviceJID]]) {
        NSString *presenceType = [presence type];
        if ([presenceType compare:@"available" options:NSCaseInsensitiveSearch] == NSOrderedSame) {
            for (id<MXiConnectionServiceStateDelegate> delegate in self.delegates) {
                [delegate connectionStateChanged:MXiConnectionServiceConnected];
            }
        }
        if ([presenceType compare:@"unavailable" options:NSCaseInsensitiveSearch] == NSOrderedSame) {
            for (id<MXiConnectionServiceStateDelegate> delegate in self.delegates) {
                [delegate connectionStateChanged:MXiConnectionServiceUnconnected];
            }
        }
    }

    for (id delegate in self.stanzaDelegates)
        if ([delegate respondsToSelector:@selector(presenceStanzaRecieved:)])
            [delegate performSelectorOnMainThread:@selector(presenceStanzaReceived)
                                       withObject:presence
                                    waitUntilDone:NO];
}

- (void)serviceInstanceCreating
{
    // TODO: implement this method.
    // Inform the initial sender that the server is creating a new service instance.
    NSLog(@"Service Instance is being created.");
}

- (void)didReceiveError:(NSXMLElement *)error
{
    NSLog(@"%@", error);
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

- (void)addServiceInstance:(MXiService *)service
{
    NSMutableArray *tmpArray = [NSMutableArray arrayWithArray:self.discoveredServiceInstances];
    [tmpArray addObject:service];
    
    self.discoveredServiceInstances = [NSArray arrayWithArray:tmpArray];
}

@end
