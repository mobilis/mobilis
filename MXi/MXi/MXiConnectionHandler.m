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

@interface MXiConnectionHandler ()

@property (strong, nonatomic) MXiConnection *connection;
@property (strong, nonatomic) NSArray *incomingBeans;
@property (strong, nonatomic) NSMutableArray *outgoingBeanQueue;

@property BOOL authenticated;
@property BOOL connected;

@property (copy, nonatomic) AuthenticationBlock authenticationBlock;

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
        shared = [[super alloc] initUniqueInstance];
    });
    return shared;
}

- (instancetype)initUniqueInstance
{
    return [super init];
}

#pragma mark - Connection Handling

- (void)launchConnectionWithJID:(NSString *)jabberID
                       password:(NSString *)password
                       hostName:(NSString *)hostName
                           port:(NSNumber *)hostPort
            authenticationBlock:(AuthenticationBlock)authentication
{
    NSDictionary *settingsDictionary = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Settings"
                                                                                                                  ofType:@'plist']];
    self.connection = [MXiConnection connectionWithJabberID:jabberID
                                                   password:password
                                                   hostName:hostName
                                                       port:hostPort
                                             coordinatorJID:[NSString stringWithFormat:@"mobilis@%@/Coordinator", hostName]
                                           serviceNamespace:[settingsDictionary valueForKeyPath:@"jabberInformation.serviceNamespace"]
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
    authtenticationBlock:(AuthenticationBlock)authentication
{
    _authenticated = NO;
    _connected = NO;
    
    self.authenticationBlock = authentication;
    [self.connection reconnectWithJabberID:jabberID
                                  password:password
                                  hostname:hostName
                                      port:[port integerValue]
                            coordinatorJID:[NSString stringWithFormat:@"mobilis@%@/Coordinator", hostName]
                          serviceNamespace:[[[NSBundle mainBundle] infoDictionary] valueForKeyPath:@"jabberInformation.serviceNamespace"]];
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

- (void)didFailToAuthenticate:(DDXMLElement *)error
{
    self.authenticationBlock(NO);
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
#warning incomplete implementation
}

- (void)didReceiveError:(DDXMLElement *)error
{
    NSLog(@"%@", error);
}

#pragma mark - Private Helper

- (NSArray *)allIncomingBeans
{
    if (self.incomingBeans) {
        return self.incomingBeans;
    }
//    IncomingBeanDetection *incomingBeans = [IncomingBeanDetection new];
//    self.incomingBeans = [incomingBeans detectBeans];
    // TODO: either use reflection here or generate all the beans.
    self.incomingBeans = @[];

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

@end
