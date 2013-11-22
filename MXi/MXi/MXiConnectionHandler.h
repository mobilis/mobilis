//
//  ConnectionHandler.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "MXiService.h"
#import "MXiMultiUserChatDelegate.h"
#import "MXiOutgoingBean.h"
#import "MXiDefinitions.h"

@class MXiConnection;
@class MXiMultiUserChatDiscovery;
@class MXiServiceManager;
@protocol MXiConnectionHandlerDelegate;
@protocol MXiMultiUserChatDiscoveryDelegate;
@class MXiBean;

/**
 *  The ConnectionHandler class provides global-level information of the XMPP connection to an XMPP server.
 */
@interface MXiConnectionHandler : NSObject

@property (readonly) MXiConnection *connection;
@property (readonly) MXiServiceManager *serviceManager;

@property (nonatomic, weak) id<MXiConnectionHandlerDelegate> delegate;

/**
 *  Returns a ConnectionHandler object that manages all relevant information on the connection and incoming and
 *  outgoing stanzas.
 *
 *  Because there is only one active connection supposed to exist at a time, always the same instance of the object
 *  will be returned.
 *
 *  @see ConnectionHandlerDelegate protocol
 *
 *  @return The ConnectionHandler object.
 */
+ (instancetype)sharedInstance;

/**
 *  Set up a new Connection to a XMPP server. The credentials used in this method will automatically be
 *  stored in the Keychains which means old account information will be overridden.
 *
 *  @param jabberID        The full or bare JID of the user who's registered at the XMPP server.
 *  @param password        The user's password associated with the JID and XMPP server.
 *  @param hostName        The host name of XMPP server, e.g. 'jabber.org'.
 *  @param serviceType     The type of the service which is either MULTI or SINGLE.
 *  @param port            The port under which the XMPP server is available, usually 5222.
 *  @param authentication  Callback block to inform the application on the success of the authentication.
 */
- (void)launchConnectionWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName serviceType:(ServiceType)serviceType port:(NSNumber *)hostPort;

/**
 *  Use already existing connection delegates and just reconfigure the connection to the server.
 *
 *  @param jabberID       The full or bare JID of the user who's registered at the XMPP server.
 *  @param password       The user's password associated with the JID and XMPP server.
 *  @param hostName       The host name of XMPP server, e.g. 'jabber.org'.
 *  @param port           The port under which the XMPP server is available, usually 5222.
 *  @param authentication Callback block to inform the sender on the success of the authentication.
 */
- (void)reconnectWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName port:(NSNumber *)port;

/**
 *  This method realizes client-server communication and sends outgoing beans to a service instance.
 *
 *  If you run a SINGLE-mode service the service parameter can be nil, so the detected SINGLE-service-instance will be used
 *  automatically.
 *  If you run a MULTI-mode service, specify the service instance you want the bean to be sent to. If no service is specified
 *  the message will be sent to the first service stored by the MXiServiceManager by default.
 *
 *  @param outgoingBean     Bean object that should be send to the service.
 *  @param service          Optional. If the service is not set, the first stored by the MXiServiceManger will be used.
 *
 *  @see MXiOutgoingBean protocol
 */
- (void)sendBean:(MXiBean <MXiOutgoingBean> *)outgoingBean toService:(MXiService *)service;

/*!
    Send a stanza of any kind to the XMPP server.

    @param element The XML stanza that is supposed to be transferred.
 */
- (void)sendElement:(NSXMLElement *)element;

- (void)sendMessageXML:(NSXMLElement *)messageElement toJID:(NSString *)jid;
- (void)sendMessageString:(NSString *)messageString toJID:(NSString *)jid;

@end

@protocol MXiConnectionHandlerDelegate <NSObject>

- (void)authenticationFinishedSuccessfully:(BOOL)authenticationState;
- (void)connectionDidDisconnect:(NSError *)error;

/*!
    Will be invoked when the service discovery finished.

    @param error    If the service discovery finished regularly, the error object is set to nil. Otherwise not.
 */
- (void)serviceDiscoveryError:(NSError *)error;

@end