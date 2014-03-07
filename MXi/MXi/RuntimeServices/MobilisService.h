//
//  MobilisService.h
//  ACDSenseService4Mac
//
//  Created by Martin Weissbach on 28/12/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "MXiDefinitions.h"

@class MXiServiceConnectionHandler;

/*!
    Base class for Mobilis Service implementations.
    Mobilis Services that will be uploaded to the Objective-C runtime need to make their principle class a subclass of
    MobilisService class.
 */
@interface MobilisService : NSObject

@property (nonatomic) NSString *serviceName;
@property (nonatomic) NSString *serviceNamespace;
@property (nonatomic) NSNumber *serviceVersion;
/// The publicly visible type of the Service. Either MULTI or SINGLE.
@property (nonatomic) ServiceType serviceType;

@property (nonatomic, readonly) MXiServiceConnectionHandler *connectionHandler;

/*!
    Initialize a new Service Instance.

    @param  jid         The jid of the new service.
    @param  password    The password of the XMPP Account with the given JID.
    @param  hostName    XMPP server to connect to.
    @param  runtimeName XMPP user name of the server to connect to.
    @param  port        The port number the XMPP listens to.

    @retun  A new instance of the MobilisService.
 */
- (instancetype)initServiceWithJID:(XMPPJID *)jid password:(NSString *)password hostName:(NSString *)hostName runtimeName:(NSString *)runtimeName port:(NSNumber *)port;

@end