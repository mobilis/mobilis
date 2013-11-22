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
#else
#import <MXi/MXi.h>
#import <XMPPFramework.h>
#endif

#import "MXiMultiUserChatDelegate.h"
#import "MXiDefinitions.h"

@protocol MXiConnectionDelegate;

@interface MXiConnection : NSObject

@property (nonatomic, retain) XMPPJID* jabberID;
@property (nonatomic, retain) NSString* password;
@property (nonatomic, strong) NSString* hostName;
@property (nonatomic) NSInteger port;
@property (nonatomic, strong) NSString* serviceJID;
@property (nonatomic, strong) NSString* serviceNamespace;
@property (nonatomic, strong) NSString *serviceName;
@property (nonatomic) ServiceType serviceType;
@property (nonatomic, strong) NSString* coordinatorJID;
@property (nonatomic, readonly) XMPPStream* xmppStream;
@property (nonatomic, strong) id<MXiMultiUserChatDelegate> mucDelegate;
@property (nonatomic, strong) NSArray* incomingBeanPrototypes;

@property (nonatomic, weak) id<MXiConnectionDelegate> delegate;

+ (id)connectionWithJabberID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName port:(NSInteger)port coordinatorJID:(NSString *)coordinatorJID serviceNamespace:(NSString *)serviceNamespace serviceType:(ServiceType)serviceType listeningForIncomingBeans:(NSArray *)incomingBeanPrototypes connectionDelegate:(id<MXiConnectionDelegate>)delegate;

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

- (void)disconnect;

- (void)addBeanDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;
- (void)addStanzaDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement;
- (void)addErrorDelegate:(id)delegate withSelecor:(SEL)selector;

- (void)removeBeanDelegate:(id)delegate forBeanClass:(Class)beanClass;
- (void)removeStanzaDelegate:(id)delegate forStanzaElement:(StanzaElement)element;
- (void)removeErrorDelegate:(id)delegate;

@end

@protocol MXiConnectionDelegate <NSObject>

/*!
    Indicates when the authentication process finished and therefor if the connection could be set up.
    @param  error   If the authentication finished without errors this parameter will be nil otherwise not.
 */
- (void)connectionAuthenticationFinished:(NSXMLElement *)error;
- (void)connectionDidDisconnect:(NSError *)error;

@end