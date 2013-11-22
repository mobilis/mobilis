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
@property (readonly) MXiMultiUserChatDiscovery *multiUserChatDiscovery;

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
 *  This method realizes client-server communication and sends outgoing beans to the service.
 *
 *  @param outgoingBean Bean object that should be send to the service.
 *
 *  @see MXiOutgoingBean protocol
 */
- (void)sendBean:(MXiBean<MXiOutgoingBean> *)outgoingBean;

/*!
    Send a stanza of any kind to the XMPP server.

    @param element The XML stanza that is supposed to be transfered.
 */
- (void)sendElement:(NSXMLElement *)element;

- (void)sendMessageXML:(NSXMLElement *)messageElement toJID:(NSString *)jid;
- (void)sendMessageString:(NSString *)messageString toJID:(NSString *)jid;

#pragma mark - Multi User Chat support

/*!
    This method will connect to a multi user chat room specified by a given jabber ID of the room.

    @param roomJID The jabber ID of the multi user chat room to connect to.
    @param delegate The delegate incoming multi user chat messages will be delegated to.

    @warning *Important:* If the delegate is _nil_ and no delegate has been set before, this method will throw an exception of kind NSException.

    @see isMultiUserChatDelegateSet
 */
- (void)connectToMultiUserChatRoom:(NSString *)roomJID withDelegate:(id <MXiMultiUserChatDelegate>)delegate;

/**
    Disconnects from the room specified by a given JID.

    @param roomJID The jabberID of the room that sould be left.
*/
- (void)leaveMultiUserChatRoom:(NSString *)roomJID;
/*!
    Sends a groupchat message to a multi user chat room specified by a given JID.

    @param message  The body of the message as a string.
    @param roomJID  The jabber ID of the room this message is addressed to.
    @param userName The resource of the user within the chat room;
 */
- (void)sendMessage:(NSString *)message toRoom:(NSString *)roomJID;
- (void)sendMessage:(NSString *)message toRoom:(NSString *)roomJID toUser:(NSString *)userName;

@end

@protocol MXiConnectionHandlerDelegate <NSObject>

- (void)authenticationFinishedSuccessfully:(BOOL)authenticationState;
- (void)connectionDidDisconnect:(NSError *)error;

- (void)serviceDiscoveryError:(NSError *)error;

@end