//
//  MXiConnection.m
//  MXi
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiConnection.h"

#import "MXiMultiUserChatMessage.h"
#import "XMPPRoomMemoryStorage.h"
#import "MXiBeanDelegateDictionary.h"
#import "MXiDelegateSelectorMapping.h"
#import "MXiStanzaDelegateDictionary.h"
#import "MXiErrorDelegateDictionary.h"
#import "MXiMultiUserChatDelegateDictionary.h"

@interface MXiConnection () <XMPPRoomDelegate>

@property (strong, nonatomic) NSMutableArray *connectedMUCRooms;
@property dispatch_queue_t room_queue;

- (BOOL)isIncomingIQBeanContainer:(XMPPIQ *)incomingIQ;
- (void)notifyBeanDelegates:(MXiBean *)bean;
- (void)notifyStanzaDelegates:(NSXMLElement *)stanza;
- (void)notifyErrorDelegates:(NSXMLElement *)error;

@end

@implementation MXiConnection {
    __strong MXiBeanDelegateDictionary *_beanDelegateDictionary;
    __strong MXiStanzaDelegateDictionary *_stanzaDelegateDictionary;
    __strong MXiErrorDelegateDictionary *_errorDelegateDictionary;
    __strong MXiMultiUserChatDelegateDictionary *_multiUserChatDelegateDictionary;
}

+ (id)connectionWithJabberID:(NSString *)aJabberID password:(NSString *)aPassword hostName:(NSString *)aHostName port:(NSInteger)port coordinatorJID:(NSString *)theCoordinatorJID serviceNamespace:(NSString *)theServiceNamespace serviceType:(ServiceType)serviceType listeningForIncomingBeans:(NSArray *)theIncomingBeanPrototypes connectionDelegate:(id<MXiConnectionDelegate>)delegate
{
	return [[self alloc] initWithJabberID:aJabberID password:aPassword hostName:aHostName port:port coordinatorJID:theCoordinatorJID serviceNamespace:theServiceNamespace serviceType:serviceType listeningForIncomingBeans:theIncomingBeanPrototypes connectionDelegate:delegate ];
}

- (id)initWithJabberID:(NSString *)aJabberID password:(NSString *)aPassword hostName:(NSString *)aHostName port:(NSInteger)port coordinatorJID:(NSString *)theCoordinatorJID serviceNamespace:(NSString *)theServiceNamespace serviceType:(ServiceType)serviceType listeningForIncomingBeans:(NSArray *)theIncomingBeanPrototypes connectionDelegate:(id<MXiConnectionDelegate>)connectionDelegate
{
    self = [super init];
    if (self) {
        XMPPJID* tempJid = [XMPPJID jidWithString:aJabberID];
        [self setJabberID:tempJid];
        [self setPassword:aPassword];
        [self setServiceType: serviceType];
        if (aHostName && ![aHostName isEqualToString:@""]) {
            [self setHostName:aHostName];
        } else {
            [self setHostName:[tempJid domain]];
        }
        [self setPort:port];
        [self setCoordinatorJID:theCoordinatorJID];
        [self setServiceNamespace:theServiceNamespace];
        [self setIncomingBeanPrototypes:theIncomingBeanPrototypes];

        self.delegate = connectionDelegate;

        [self setupDelegateDictionaries];
        [self setupStream];
        [self connect];
    }
    return self;
}
- (void)setupDelegateDictionaries
{
    _beanDelegateDictionary = [MXiBeanDelegateDictionary new];
    _stanzaDelegateDictionary = [MXiStanzaDelegateDictionary new];
    _errorDelegateDictionary = [MXiErrorDelegateDictionary new];
    _multiUserChatDelegateDictionary = [MXiMultiUserChatDelegateDictionary new];
}

- (BOOL)reconnectWithJabberID:(NSString *)aJabberID
					 password:(NSString *)aPassword
					 hostname:(NSString *)aHostname
						 port:(NSInteger )thePort
				   coordinatorJID:(NSString *)theCoordinatorJID
			 serviceNamespace:(NSString *)theServiceNamespace {
	[self disconnect];
	
	[self setJabberID:[XMPPJID jidWithString:aJabberID]];
	[self setPassword:aPassword];
	if (aHostname && ![aHostname isEqualToString:@""]) {
		[self setHostName:aHostname];
	}
	[self setPort:thePort];
	[self setCoordinatorJID:theCoordinatorJID];
	[self setServiceNamespace:theServiceNamespace];
	
	return [self connect];
}

#pragma mark - XEP-0045: Multi-User-Chat

- (void)connectToMultiUserChatRoom:(NSString *)roomJID withDelegate:(id <MXiMultiUserChatDelegate>)delegate
{
    if (!_connectedMUCRooms) {
        self.connectedMUCRooms = [NSMutableArray arrayWithCapacity:5];
        _room_queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    }
    XMPPRoom *room = [[XMPPRoom alloc] initWithRoomStorage:[[XMPPRoomMemoryStorage alloc] init]
                                                       jid:[XMPPJID jidWithString:roomJID]
                                             dispatchQueue:_room_queue];

    [_multiUserChatDelegateDictionary addDelegate:delegate forMultiUserChatRoom:roomJID];

    [room activate:self.xmppStream];
    [room addDelegate:self delegateQueue:_room_queue];
    [room joinRoomUsingNickname:[NSString stringWithFormat:@"mobilis_iOS_%f", [[NSDate date] timeIntervalSince1970]]
                        history:nil];
}

- (void)leaveMultiUserChatRoom:(NSString *)roomJID
{
    XMPPRoom *roomToLeave = nil;
    for (XMPPRoom *room in _connectedMUCRooms) {
        if ([[room.roomJID full] isEqualToString:roomJID]) {
            [room deactivate];
            roomToLeave = room;
            break;
        }
    }
    [_connectedMUCRooms removeObject:roomToLeave];
    [_multiUserChatDelegateDictionary removeDelegate:[_multiUserChatDelegateDictionary delegateForMultiUserChatRoom:roomJID]
                                forMultiUserChatRoom:roomJID];
}

- (void)sendMessage:(NSString *)message toRoom:(NSString *)roomJID;
{
    XMPPJID *outgoingJID = [XMPPJID jidWithString:roomJID];
    for (XMPPRoom *room in _connectedMUCRooms) {
        if ([[room.roomJID full] isEqualToString:[outgoingJID bare]]) {
            [room sendMessage:[MXiMultiUserChatMessage messageWithBody:message]];
        }
    }
}

#pragma mark - XMPPStreamDelegate

- (void)xmppStreamDidConnect:(XMPPStream* )sender {
	NSError* error = nil;
	[self.xmppStream authenticateWithPassword:self.password error:&error];
}

- (void)xmppStreamDidDisconnect:(XMPPStream *)sender withError:(NSError *)error {
	[self.delegate connectionDidDisconnect:error];
}

- (void)xmppStreamDidAuthenticate:(XMPPStream* )sender {
	[self goOnline];
}

- (void)xmppStream:(XMPPStream *)sender didNotAuthenticate:(NSXMLElement *)error {
    [self.delegate connectionAuthenticationFinished:error];
}

- (BOOL)xmppStream:(XMPPStream *)sender didReceiveIQ:(XMPPIQ *)iq {
    if ([self isIncomingIQPing:iq]) {
        [self pong:iq];
        return YES;
    }
    if ([iq.type isEqualToString:@"error"])
    {
        [self xmppStream:sender didReceiveError:iq];
        return YES;
    }

    [self notifyStanzaDelegates:iq];

    NSLog(@"Received iq: %@", [iq prettyXMLString]);

    // Did we get an incoming mobilis bean?
    if ([self isIncomingIQBeanContainer:iq]) {
        NSXMLElement* childElement = [iq childElement];
        for (MXiBean* prototype in self.incomingBeanPrototypes) {
            if ([[[prototype class] elementName] isEqualToString:[childElement name]] &&
                    [[[prototype class] namespace] isEqualToString:[childElement xmlns]] &&
                    [[MXiIQTypeLookup stringValueForIQType:[prototype beanType]]
                        isEqualToString:[iq attributeStringValueForName:@"type"]]) {
                // parse the iq data into the bean object
                [MXiBeanConverter beanFromIQ:iq intoBean:prototype];
                // inform the app about this incoming bean
                [self notifyBeanDelegates:prototype];
            }
        }
        return YES;
    }

	return YES;
}

- (void)pong:(XMPPIQ *)xmppiq
{
    NSXMLElement *pong = [NSXMLElement elementWithName:@"iq"
                                              children:nil
                                            attributes:@[[NSXMLElement attributeWithName:@"from" stringValue:xmppiq.to.full],
                                            [NSXMLElement attributeWithName:@"to" stringValue:xmppiq.fromStr],
                                            [NSXMLElement attributeWithName:@"id" stringValue:xmppiq.elementID],
                                            [NSXMLElement attributeWithName:@"type" stringValue:@"result"]]];
    [self sendElement:pong];
}

- (BOOL)isIncomingIQPing:(XMPPIQ *)xmppiq
{
    NSXMLElement *childElement = [xmppiq childElement];
    for (NSXMLNode *namespaceElement in [childElement namespaces])
        if ([[namespaceElement stringValue] isEqualToString:@"urn:xmpp:ping"])
            return YES;
    return NO;
}

- (BOOL)isIncomingIQBeanContainer:(XMPPIQ *)incomingIQ
{
    BOOL isBean = NO;
    NSXMLElement *childElement = [incomingIQ childElement];
    for (MXiBean* prototype in self.incomingBeanPrototypes) {
        if ([[[prototype class] elementName] isEqualToString:[childElement name]] &&
                [[[prototype class] namespace] isEqualToString:[childElement xmlns]] &&
                [[MXiIQTypeLookup stringValueForIQType:[prototype beanType]]
                        isEqualToString:[incomingIQ attributeStringValueForName:@"type"]]) {
            isBean = YES;
            break;
        }
    }
    return isBean;
}

- (void)xmppStream:(XMPPStream *)sender didReceiveMessage:(XMPPMessage *)message {
    [self notifyStanzaDelegates:message];
}

- (void)xmppStream:(XMPPStream *)sender didReceivePresence:(XMPPPresence *)presence {
	[self notifyStanzaDelegates:presence];
}

- (void)xmppStream:(XMPPStream *)sender didReceiveError:(NSXMLElement *)error {
	[self notifyErrorDelegates:error];
}

/*
 * Preparing and closing the xmpp stream
 */

- (void)setupStream {
    _xmppStream = [[XMPPStream alloc] init];
	// inform this very object about stream events
	[self.xmppStream addDelegate:self delegateQueue:dispatch_get_main_queue()];
}

- (void)goOnline {
	XMPPPresence* presence = [XMPPPresence presence];
	[self.xmppStream sendElement:presence];

    [self.delegate connectionAuthenticationFinished:nil];
}

- (void)goOffline {
	XMPPPresence* presence = [XMPPPresence presenceWithType:@"unavailable"];
	[self.xmppStream sendElement:presence];
}

- (BOOL)connect {
	if ([self.xmppStream isConnected]) {
		return YES;
	}
	
	[self.xmppStream setMyJID:[self jabberID]];
	[self.xmppStream setHostName:[self hostName]];
    [self.xmppStream setHostPort:(UInt16) [self port]];
	
	XMPPReconnect* reconnect = [[XMPPReconnect alloc] init];
	[reconnect activate:self.xmppStream];
	
	/*
	NSLog(@"Trying to connect with:");
	NSLog(@" - myJid: %@", [xmppStream myJID]);
	NSLog(@" - myPassword: %@", password);
	NSLog(@" - hostname: %@", [xmppStream hostName]);
	NSLog(@" - port: %d", [xmppStream hostPort]);
	*/
	
	NSError* error = nil;
	if (![self.xmppStream connectWithTimeout:30.0 error:&error]) {
		NSLog(@"Couldn't connect because of error: %@", [error localizedDescription]);
		return NO;
	}
	
	return YES;
}

- (void)sendElement:(NSXMLElement* )element {
	NSLog(@"Sent: %@", [element prettyXMLString]);
	
	[self.xmppStream sendElement:element];
}

- (void)sendBean:(MXiBean* )bean {
    NSAssert(bean.to != nil, @"No addresse of the outgoing bean!");
	[bean setFrom:self.jabberID];
	[self sendElement:[MXiBeanConverter beanToIQ:bean]];
}

- (void)sendBean:(MXiBean *)bean toJid:(XMPPJID *)jid
{
    NSAssert(jid != nil, @"The JID is not allowed to be nil.");
    [bean setTo:jid];
    [self sendBean:bean];
}

- (void)disconnect {
	[self goOffline];
	[self.xmppStream disconnect];
}

#pragma mark - Manage Bean & Stanza Delegation

- (void)addBeanDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass
{
    [_beanDelegateDictionary addDelegate:delegate withSelector:selector forBeanClass:beanClass];
}

- (void)addStanzaDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement
{
    [_stanzaDelegateDictionary addDelegate:delegate withSelector:selector forStanzaElement:stanzaElement];
}

- (void)addErrorDelegate:(id)delegate withSelecor:(SEL)selector
{
    [_errorDelegateDictionary addErrorDelegate:delegate withSelector:selector];
}

- (void)removeBeanDelegate:(id)delegate forBeanClass:(Class)beanClass
{
    [_beanDelegateDictionary removeDelegate:delegate forBeanClass:beanClass];
}

- (void)removeStanzaDelegate:(id)delegate forStanzaElement:(StanzaElement)element
{
    [_stanzaDelegateDictionary removeDelegate:delegate forStanzaElement:element];
}

- (void)removeErrorDelegate:(id)delegate
{
    [_errorDelegateDictionary removeErrorDelegate:delegate];
}

#pragma mark - Delegate Notification

- (void)notifyBeanDelegates:(MXiBean *)bean
{
    NSArray *registeredDelegates = nil;
    @synchronized (_beanDelegateDictionary) {
        registeredDelegates = [NSArray arrayWithArray:[_beanDelegateDictionary delegatesForBeanClass:[bean class]]];
    }
    for (MXiDelegateSelectorMapping *mapping in registeredDelegates) {
        if ([mapping.delegate respondsToSelector:[mapping selector]]) {
            [mapping.delegate performSelector:[mapping selector] withObject:bean]; // Warning can be ignored.
        }
    }
}

- (void)notifyStanzaDelegates:(NSXMLElement *)stanza
{
    NSArray *registeredDelegates = nil;
    StanzaElement stanzaElement = [self stanzaElementFromStanza:stanza];
    if (stanzaElement == UNKNOWN_STANZA)
        return;
    @synchronized (_stanzaDelegateDictionary) {
        registeredDelegates = [NSArray arrayWithArray:[_stanzaDelegateDictionary delegatesforStanzaElement:stanzaElement]];
    }
    for (MXiDelegateSelectorMapping *mapping in registeredDelegates) {
        if ([mapping.delegate respondsToSelector:[mapping selector]]) {
            [mapping.delegate performSelector:[mapping selector] withObject:stanza]; // Warning can be ignored.
        }
    }
}
- (StanzaElement)stanzaElementFromStanza:(NSXMLElement *)stanza
{
    if ([[stanza name] isEqualToString:@"iq"]) return IQ;
    if ([[stanza name] isEqualToString:@"message"]) return MESSAGE;
    if ([[stanza name] isEqualToString:@"presence"]) return PRESENCE;

    return UNKNOWN_STANZA;
}

- (void)notifyErrorDelegates:(NSXMLElement *)error
{
    NSArray *registeredDelegates = nil;
    @synchronized (_errorDelegateDictionary) {
        registeredDelegates = [NSArray arrayWithArray:[_errorDelegateDictionary delegates]];
    }
    for (NSArray *mappingArray in registeredDelegates) {
        for (MXiDelegateSelectorMapping *mapping in mappingArray)
            if ([mapping.delegate respondsToSelector:[mapping selector]]) {
                [mapping.delegate performSelector:[mapping selector] withObject:error];
            }
    }
}

#pragma mark - XMPPRoomDelegate

- (void)xmppRoomDidJoin:(XMPPRoom *)sender
{
    [self.connectedMUCRooms addObject:sender];
    id<MXiMultiUserChatDelegate> delegate = [_multiUserChatDelegateDictionary delegateForMultiUserChatRoom:sender.roomJID.full];
    [delegate performSelector:@selector(connectionToRoomEstablished:usingRoomJID:) withObject:[sender.roomJID bare] withObject:sender.myRoomJID.full];
}

- (void)xmppRoom:(XMPPRoom *)sender didReceiveMessage:(XMPPMessage *)message fromOccupant:(XMPPJID *)occupantJID
{
    id<MXiMultiUserChatDelegate> delegate = [_multiUserChatDelegateDictionary delegateForMultiUserChatRoom:sender.roomJID.full];
    if ([delegate respondsToSelector:@selector(didReceiveMultiUserChatMessage:fromUser:publishedInRoom:)])
        [delegate didReceiveMultiUserChatMessage:[[message elementForName:@"body"] stringValue]
                                        fromUser:occupantJID.full
                                 publishedInRoom:sender.roomJID.full];
}

- (void)xmppRoom:(XMPPRoom *)sender occupantDidJoin:(XMPPJID *)occupantJID withPresence:(XMPPPresence *)presence
{
    id<MXiMultiUserChatDelegate> delegate = [_multiUserChatDelegateDictionary delegateForMultiUserChatRoom:sender.roomJID.full];
    if ([delegate respondsToSelector:@selector(userWithJid:didJoin:room:)])
        [delegate userWithJid:occupantJID.full didJoin:presence.status room:[sender.roomJID full]];
}

- (void)xmppRoom:(XMPPRoom *)sender occupantDidLeave:(XMPPJID *)occupantJID withPresence:(XMPPPresence *)presence
{
    id<MXiMultiUserChatDelegate> delegate = [_multiUserChatDelegateDictionary delegateForMultiUserChatRoom:sender.roomJID.full];
    if ([delegate respondsToSelector:@selector(userWithJid:didLeaveRoom:)])
        [delegate userWithJid:occupantJID.full didLeaveRoom:[sender.roomJID full]];
}

- (void)xmppRoom:(XMPPRoom *)sender occupantDidUpdate:(XMPPJID *)occupantJID withPresence:(XMPPPresence *)presence
{
    id<MXiMultiUserChatDelegate> delegate = [_multiUserChatDelegateDictionary delegateForMultiUserChatRoom:sender.roomJID.full];
    if ([delegate respondsToSelector:@selector(userWithJid:didUpdate:inRoom:)])
        [delegate userWithJid:occupantJID.full didUpdate:presence.status inRoom:[sender.roomJID full]];
}

@end
