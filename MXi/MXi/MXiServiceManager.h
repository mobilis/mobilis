//
//  MXiServiceManager.h
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiDefinitions.h"

@class MXiConnection;
@protocol MXiServiceManagerDelegate;

@interface MXiServiceManager : NSObject

@property (atomic, readonly) NSArray *services;

@property (nonatomic, readonly, weak) MXiConnection *connection;
@property (nonatomic, readonly) ServiceType serviceType;
@property (nonatomic, readonly) NSString *namespace;

@property (nonatomic, weak) id<MXiServiceManagerDelegate> delegate;

+ (instancetype)serviceManagerWithConnection:(MXiConnection *)connection serviceType:(ServiceType)serviceType namespace:(NSString *)namespace;

@end

@protocol MXiServiceManagerDelegate

- (void)serviceDiscoveryFinishedWithError:(NSError *)error;

@end