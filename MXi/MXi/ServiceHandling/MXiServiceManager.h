//
//  MXiServiceManager.h
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "MXiDefinitions.h"

@class MXiConnection;
@protocol MXiServiceManagerDelegate;
@class MXiService;

/*!
    Service instances for MULTI-mode and SINGLE-mode services are stored, manged and discovered by this class.
 */
@interface MXiServiceManager : NSObject

/// List of MXiServices that were discovered for the service. A service discovery must complete without errors before this list contains any objects.
@property (atomic, readonly) NSArray *services;

@property (nonatomic, readonly, weak) MXiConnection *connection;
@property (nonatomic, readonly) ServiceType serviceType;
@property (nonatomic, readonly) NSString *namespace;

+ (instancetype)serviceManagerWithConnection:(MXiConnection *)connection serviceType:(ServiceType)serviceType namespace:(NSString *)namespace;

- (void)addDelegate:(id <MXiServiceManagerDelegate>)delegate;
- (void)removeDelegate:(id<MXiServiceManagerDelegate>)delegate;

- (void)createServiceWithName:(NSString *)serviceName andPassword:(NSString *)password;

@end

@protocol MXiServiceManagerDelegate <NSObject>

- (void)serviceDiscoveryFinishedWithError:(NSError *)error;

@optional

- (void)createdServiceInstanceSuccessfully:(MXiService *)service;

@end