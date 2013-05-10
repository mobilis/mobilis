//
//  MXiConnection.h
//  MXi
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "XMPP.h"

@protocol PresenceDelegate <NSObject>

- (void)didAuthenticate;
- (void)didDisconnectWithError:(NSError* )error;
- (void)didFailToAuthenticate:(NSXMLElement* )error;

@end

@protocol StanzaDelegate <NSObject>

- (void)didReceiveMessage:(XMPPMessage* )message;
- (BOOL)didReceiveIQ:(XMPPIQ* )iq;
- (void)didReceivePresence:(XMPPPresence* )presence;
- (void)didReceiveError:(NSXMLElement* )error;

@end

@interface MXiConnection : NSObject

@property (nonatomic, retain) NSString* jabberID;
@property (nonatomic, retain) NSString* password;
@property (nonatomic, readonly) XMPPStream* xmppStream;
@property (nonatomic, strong) id<PresenceDelegate> presenceDelegate;
@property (nonatomic, strong) id<StanzaDelegate> stanzaDelegate;

+ (id)connectionWithJabberID:(NSString* )aJabberID
					password:(NSString* )aPassword
			presenceDelegate:(id<PresenceDelegate> )aPresenceDelegate
			  stanzaDelegate:(id<StanzaDelegate> )aStanzaDelegate;

- (void)sendTestMessageWithContent:(NSString* )content to:(NSString* )to;
- (void)send:(NSXMLElement* )element;

- (BOOL)reconnectWithJabberID:(NSString* ) aJabberID
					 password:(NSString* )aPassword;
- (void)disconnect;

@end