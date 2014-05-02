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
#import "MXiDefinitions.h"
#import "MXiAbstractConnectionHandler.h"

@class MXiConnection;
@class MXiServiceManager;
@protocol MXiConnectionHandlerDelegate;
@protocol MXiMultiUserChatDiscoveryDelegate;
@class MXiBean;

/*!
	@class MXiConnectionHandler
    The ConnectionHandler class provides global-level information of the XMPP connection to an XMPP server.
*/
@interface MXiConnectionHandler : MXiAbstractConnectionHandler

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

@end
