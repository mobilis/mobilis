//
//  MXiServiceTypeDiscovery.h
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiDefinitions.h"

@class MXiConnection;
@class MXiService;
@protocol MXiServiceTypeDiscoveryDelegate;

/*!
    A class responsible for the discovery of Mobilis Runtime Services.

    The service discovery executed here is not the XMPP service discovery specified in XEP-0030.
 */
@interface MXiServiceTypeDiscovery : NSObject

@property (nonatomic, weak, readonly) MXiConnection *connection;
@property (nonatomic, weak) id<MXiServiceTypeDiscoveryDelegate> delegate;

+ (instancetype)serviceTypeDiscoveryWithConnection:(MXiConnection *)connection
                                    andServiceType:(ServiceType)serviceType
                                      forNamespace:(NSString *)namespace;

/*!
    Starts a service discovery asynchronously.
    Discovers all services that are currently available on a Mobilis Runtime specified by the current connection.

    This method is the first step of the service discovery of MULTI-services.
    If you want to discover service instances of a SINGLE-service you must not invoke this method. Use discover services instead.

    @see MXiConnection
    @see MXiConnectionHandler

    @see discoverServices
 */
- (void)discoverServices;
/*!
    Starts a service discovery asynchronously.
    Discovers all instances of a service that is determined by a namespace.

    This method is the first and only step for service discovery of SINGLE-services.
    It is the second step of service discovery of MULTI-services and will be invoked automatically in this case.

    If you want to determine all service instances of a MULTI-service send discoverServices instead.

    @see discoverServices
 */
- (void)discoverServiceInstances;

@end

@protocol MXiServiceTypeDiscoveryDelegate

- (void)serviceDiscovered:(MXiService *)service;
/*!
    This message is sent to the receiver when the service discovery finished.

    @param error    When the service discovery finished without failures, the error will be nil.
                    Otherwise the error object will be set.
 */
- (void)discoveryFinishedWithError:(NSError *)error;

@end
