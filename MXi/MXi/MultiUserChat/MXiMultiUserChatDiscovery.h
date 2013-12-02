//
// Created by Martin Weißbach on 10/21/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

static NSString *const serviceDiscoInfoNS = @"http://jabber.org/protocol/disco#info";
static NSString *const serviceDiscoItemsNS = @"http://jabber.org/protocol/disco#items";
static NSString *const mucFeatureString = @"http://jabber.org/protocol/muc";

@protocol MXiMultiUserChatDiscoveryDelegate;

@interface MXiMultiUserChatDiscovery : NSObject

+ (instancetype)multiUserChatDiscoveryWithDomainName:(NSString *)domainName andDelegate:(id<MXiMultiUserChatDiscoveryDelegate>)delegate;

- (void)startDiscoveryWithResultQueue:(dispatch_queue_t)resultQueue;

@end

@protocol MXiMultiUserChatDiscoveryDelegate <NSObject>

- (void)multiUserChatRoomsDiscovered:(NSArray *)chatRooms inDomain:(NSString *)domainName;

@end