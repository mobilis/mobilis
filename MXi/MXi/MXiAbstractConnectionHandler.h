//
// Created by Martin Weissbach on 28/12/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MXiDefinitions.h"
#import "MXiServiceManager.h"

@class NSXMLElement;
@class MXiService;
@class MXiBean;
@protocol MXiConnectionHandlerDelegate;
@class MXiServiceManager;
@class MXiConnection;


@interface MXiAbstractConnectionHandler : NSObject <MXiServiceManagerDelegate>

@property (readonly) MXiConnection *connection;
@property (readonly) MXiServiceManager *serviceManager;

@property (nonatomic, weak) id<MXiConnectionHandlerDelegate> delegate;

/**
 *  Set up a new Connection to a XMPP server. The credentials used in this method will automatically be
 *  stored in the Keychains which means old account information will be overridden.
 *
 *  @param jabberID        The full or bare JID of the user who's registered at the XMPP server.
 *  @param password        The user's password associated with the JID and XMPP server.
 *  @param hostName        The host name of XMPP server, e.g. 'jabber.org'.
 *  @param runtimeName     The XMPP user name of the Mobilis runtime, e.g. 'runtime1'.
 *  @param serviceType     The type of the service which is either MULTI or SINGLE.
 *  @param hostPort        The port under which the XMPP server is available, usually 5222.
 *  @param authentication  Callback block to inform the application on the success of the authentication.
 */
- (void)launchConnectionWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName runtimeName:(NSString *)runtimeName serviceType:(ServiceType)serviceType port:(NSNumber *)hostPort;

/**
 *  Use already existing connection delegates and just reconfigure the connection to the server.
 *
 *  @param jabberID       The full or bare JID of the user who's registered at the XMPP server.
 *  @param password       The user's password associated with the JID and XMPP server.
 *  @param hostName       The host name of XMPP server, e.g. 'jabber.org'.
 *  @param runtimeName     The XMPP user name of the Mobilis runtime, e.g. 'runtime1'.
 *  @param port           The port under which the XMPP server is available, usually 5222.
 *  @param authentication Callback block to inform the sender on the success of the authentication.
 */
- (void)reconnectWithJID:(NSString *)jabberID password:(NSString *)password hostName:(NSString *)hostName runtimeName:(NSString *)runtimeName port:(NSNumber *)port;

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
 */
- (void)sendBean:(MXiBean *)outgoingBean toService:(MXiService *)service;
/*!
    This method realizes bean communication with a specific jid.

    If you want to communicate with a mobilis service use `-sendBean:toService` method.

    @param outgoingBean     Bean object that should be send to the service.
    @param jid              The jid to deliver the bean to.
 */
- (void)sendBean:(MXiBean *)outgoingBean toJID:(NSString *)jid;

/*!
    Send a stanza of any kind to the XMPP server.

    @param element The XML stanza that is supposed to be transferred.
 */
- (void)sendElement:(NSXMLElement *)element;

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
