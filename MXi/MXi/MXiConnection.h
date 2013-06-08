//
//  MXiConnection.h
//  MXi
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXi.h"

@interface MXiConnection : NSObject

@property (nonatomic, retain) XMPPJID* jabberID;
@property (nonatomic, retain) NSString* password;
@property (nonatomic, strong) NSString* hostName;
@property (nonatomic, readonly) XMPPStream* xmppStream;
@property (nonatomic, strong) id<MXiPresenceDelegate> presenceDelegate;
@property (nonatomic, strong) id<MXiStanzaDelegate> stanzaDelegate;
@property (nonatomic, strong) id<MXiBeanDelegate> beanDelegate;
@property (nonatomic, strong) NSArray* incomingBeanPrototypes;

+ (id)connectionWithJabberID:(NSString* )aJabberID
					password:(NSString* )aPassword
					hostName:(NSString* )aHostName
			presenceDelegate:(id<MXiPresenceDelegate> )aPresenceDelegate
			  stanzaDelegate:(id<MXiStanzaDelegate> )aStanzaDelegate
				beanDelegate:(id<MXiBeanDelegate> )aBeanDelegate
   listeningForIncomingBeans:(NSArray* )theIncomingBeanPrototypes;

- (void)sendTestMessageWithContent:(NSString* )content to:(NSString* )to;
- (void)sendElement:(NSXMLElement* )element;
- (void)sendBean:(MXiBean<MXiOutgoingBean>* )bean;

- (BOOL)reconnectWithJabberID:(NSString* ) aJabberID
					 password:(NSString* )aPassword;
- (void)disconnect;

@end