//
//  MXiConnection.h
//  MXi
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#if TARGET_OS_IPHONE
#import "MXi.h"
#import "XMPPFramework.h"
#import "MXiMultiUserChatDelegate.h"
#else
#import <MXi/MXi.h>
#import <MXi/XMPPFramework.h>
#import <MXi/MXiMultiUserChatDelegate.h>
#endif

static NSString *const CoordinatorService = @"http://mobilis.inf.tu-dresden.de#services/CoordinatorService";

@interface MXiConnection : NSObject

@property (nonatomic, retain) XMPPJID* jabberID;
@property (nonatomic, retain) NSString* password;
@property (nonatomic, strong) NSString* hostName;
@property (nonatomic) NSInteger port;
@property (nonatomic, strong) NSString* serviceJID;
@property (nonatomic, strong) NSString* serviceNamespace;
@property (nonatomic, strong) NSString *serviceName;
@property (nonatomic, strong) NSString* coordinatorJID;
@property (nonatomic, readonly) XMPPStream* xmppStream;
@property (nonatomic, strong) id<MXiPresenceDelegate> presenceDelegate;
@property (nonatomic, strong) id<MXiStanzaDelegate> stanzaDelegate;
@property (nonatomic, strong) id<MXiBeanDelegate> beanDelegate;
@property (nonatomic, strong) id<MXiMultiUserChatDelegate> mucDelegate;
@property (nonatomic, strong) NSArray* incomingBeanPrototypes;

+ (id)connectionWithJabberID:(NSString* )jabberID
					password:(NSString* )password
					hostName:(NSString* )hostName
						port:(NSInteger )port
			  coordinatorJID:(NSString* )coordinatorJID
			serviceNamespace:(NSString* )serviceNamespace
			presenceDelegate:(id<MXiPresenceDelegate> )presenceDelegate
			  stanzaDelegate:(id<MXiStanzaDelegate> )stanzaDelegate
				beanDelegate:(id<MXiBeanDelegate> )beanDelegate
   listeningForIncomingBeans:(NSArray* )incomingBeanPrototypes;

- (void)sendTestMessageWithContent:(NSString* )content to:(NSString* )to;
- (void)sendElement:(NSXMLElement* )element;
- (void)sendBean:(MXiBean<MXiOutgoingBean>* )bean;

- (BOOL)reconnectWithJabberID:(NSString* )jabberID
					 password:(NSString* )password
					 hostname:(NSString* )hostname
						 port:(NSInteger )port
			   coordinatorJID:(NSString* )coordinatorJID
			 serviceNamespace:(NSString* )serviceNamespace;

- (void)connectToMultiUserChatRoom:(NSString *)roomJID;
- (void)leaveMultiUserChatRoom:(NSString *)roomJID;
- (void)sendMessage:(NSString *)message toRoom:(NSString *)roomJID;

- (void)createServiceInstanceWithServiceName:(NSString *)serviceName
                             servicePassword:(NSString *)password
                            serviceNamespace:(NSString *)serviceNamespace;

- (void)disconnect;

@end