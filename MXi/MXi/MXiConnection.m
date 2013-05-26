//
//  MXiConnection.m
//  MXi
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiConnection.h"
#import "XMPP.h"

@implementation MXiConnection

@synthesize jabberID, password, xmppStream, presenceDelegate, stanzaDelegate;

+ (id)connectionWithJabberID:(NSString *)aJabberID
					password:(NSString *)aPassword
			presenceDelegate:(id<PresenceDelegate> )aPresenceDelegate
			  stanzaDelegate:(id<StanzaDelegate> )aStanzaDelegate {
	MXiConnection* connection = [[MXiConnection alloc] init];
	[connection setJabberID:aJabberID];
	[connection setPassword:aPassword];
	[connection setPresenceDelegate:aPresenceDelegate];
	[connection setStanzaDelegate:aStanzaDelegate];
	
	[connection setupStream];
	[connection connect];
	return connection;
}

- (BOOL)reconnectWithJabberID:(NSString *)aJabberID password:(NSString *)aPassword {
	[self setJabberID:aJabberID];
	[self setPassword:aPassword];
	return [self connect];
}

/*
 * XMPPStream delegate methods
 */

- (void)xmppStreamDidConnect:(XMPPStream* )sender {
	NSLog(@"Connected");
	
	NSError* error = nil;
	[xmppStream authenticateWithPassword:password error:&error];
}

- (void)xmppStreamDidDisconnect:(XMPPStream *)sender withError:(NSError *)error {
	[presenceDelegate didDisconnectWithError:error];
}

- (void)xmppStreamDidAuthenticate:(XMPPStream* )sender {
	[self goOnline];
}

- (void)xmppStream:(XMPPStream *)sender didNotAuthenticate:(NSXMLElement *)error {
	NSLog(@"libMXi.a: Authentication failed");
	
	[presenceDelegate didFailToAuthenticate:error];
}

- (BOOL)xmppStream:(XMPPStream *)sender didReceiveIQ:(XMPPIQ *)iq {
	return [stanzaDelegate didReceiveIQ:iq];
}

- (void)xmppStream:(XMPPStream *)sender didReceiveMessage:(XMPPMessage *)message {
	[stanzaDelegate didReceiveMessage:message];
}

- (void)xmppStream:(XMPPStream *)sender didReceivePresence:(XMPPPresence *)presence {
	[stanzaDelegate didReceivePresence:presence];
}

- (void)xmppStream:(XMPPStream *)sender didReceiveError:(NSXMLElement *)error {
	[stanzaDelegate didReceiveError:error];
}

/*
 * Preparing and closing the xmpp stream
 */

- (void)setupStream {
	xmppStream = [[XMPPStream alloc] init];
	// inform this very object about stream events
	[xmppStream addDelegate:self delegateQueue:dispatch_get_main_queue()];
}

- (void)goOnline {
	XMPPPresence* presence = [XMPPPresence presence];
	[xmppStream sendElement:presence];
	
	[presenceDelegate didAuthenticate];
}

- (void)goOffline {
	XMPPPresence* presence = [XMPPPresence presenceWithType:@"unavailable"];
	[xmppStream sendElement:presence];
}

- (BOOL)connect {
	if ([xmppStream isConnected]) {
		return YES;
	}
	
	XMPPJID* jid = [XMPPJID jidWithString:jabberID];
	[xmppStream setMyJID:jid];
	[xmppStream setHostName:[jid domain]];
	
	NSError* error = nil;
	if (![xmppStream connect:&error]) {
		NSLog(@"Couldn't connect because of error: %@", [error localizedDescription]);
		return NO;
	}
	
	return YES;
}

- (void)sendTestMessageWithContent:(NSString *)content
								to:(NSString *)to {
	NSXMLElement *body = [NSXMLElement elementWithName:@"body"];
	[body setStringValue:content];
	
	NSXMLElement *message = [NSXMLElement elementWithName:@"message"];
	[message addAttributeWithName:@"type" stringValue:@"chat"];
	[message addAttributeWithName:@"to" stringValue:to];
	[message addAttributeWithName:@"from" stringValue:jabberID];
	[message addChild:body];
	
	[xmppStream sendElement:message];
}

- (void)send:(NSXMLElement *)element {
	NSLog(@"Sent: %@", [element prettyXMLString]);
	
	[xmppStream sendElement:element];
}

- (void)disconnect {
	[self goOffline];
	[xmppStream disconnect];
}

@end
