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

@interface MXiConnectionHandler ()

@property (strong, nonatomic) MXiConnection *connection;
@property (strong, nonatomic) NSArray *incomingBeans;
@property (strong, nonatomic) NSMutableArray *outgoingBeanQueue;

@property BOOL authenticated;
@property BOOL connected;

@property (strong, nonatomic) NSMutableArray *delegates;

@property (copy, nonatomic) AuthenticationBlock authenticationBlock;
@property (copy, nonatomic) ServiceCreateCompletionBlock serviceCreateCompletionBlock;

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
        shared = [[super alloc] initUniqueInstance];
    });
    return shared;
}

- (instancetype)initUniqueInstance
{
    self.delegates = [NSMutableArray arrayWithCapacity:10];
    return [super init];
}

#pragma mark - Connection Handling

- (void)launchConnectionWithJID:(NSString *)jabberID
                       password:(NSString *)password
                       hostName:(NSString *)hostName
                    serviceType:(ServiceType)serviceType
                           port:(NSNumber *)hostPort
            authenticationBlock:(AuthenticationBlock)authentication
{
    NSDictionary *settingsDictionary = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Settings"
                                                                                                                  ofType:@"plist"]];
    DefaultSettings *settings = nil;
    self.connection = [MXiConnection connectionWithJabberID:jabberID
                                                   password:password
                                                   hostName:hostName
                                                       port:[hostPort intValue]
                                             coordinatorJID:[NSString stringWithFormat:@"mobilis@%@/Coordinator", hostName]
                                           serviceNamespace:[settingsDictionary valueForKeyPath:@"jabberInformation.serviceNamespace"]
                                                serviceType:serviceType
                                           presenceDelegate:self
                                             stanzaDelegate:self
                                               beanDelegate:self
                                  listeningForIncomingBeans:[self allIncomingBeans]];

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
    NSDictionary *settingsDictionary = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Settings"
                                                                                                                  ofType:@"plist"]];
    [self.connection reconnectWithJabberID:jabberID
                                  password:password
                                  hostname:hostName
                                      port:[port integerValue]
                            coordinatorJID:[NSString stringWithFormat:@"mobilis@%@/Coordinator", hostName]
                          serviceNamespace:[settingsDictionary valueForKeyPath:@"jabberInformation.serviceNamespace"]];
}

- (void)createServiceWithCompletionBlock:(ServiceCreateCompletionBlock)completionBlock
{
    self.serviceCreateCompletionBlock = completionBlock;
    [self.connection createServiceInstanceWithServiceName:[self.connection serviceName]
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

#pragma mark ConnectionHandler Delgation Methods

- (void)addDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass
{
    [[MXiDelegateDictionary sharedInstance] addDelegate:delegate withSelector:selector forBeanClass:beanClass];
}

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass
{
    [[MXiDelegateDictionary sharedInstance] removeDelegate:delegate withSelector:selector forBeanClass:beanClass];
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

- (BOOL)isMultiUserChatDelegateSet
{
    return self.connection.mucDelegate != nil;
}


#pragma mark - MXiPresenceDelegate

- (void)didAuthenticate
{
    _authenticated = YES;
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
    self.serviceCreateCompletionBlock(jabberID);
}

#pragma mark - MXiStanzaDelegate

- (void)didReceiveMessage:(XMPPMessage *)message
{
#warning incomplete implementation
}

- (BOOL)didReceiveIQ:(XMPPIQ *)iq
{
#warning incomplete implementation
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
