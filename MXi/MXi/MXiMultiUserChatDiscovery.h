//
// Created by Martin Weißbach on 10/21/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

#import "MXiConnectionHandler.h"

static NSString *const serviceDiscoInfoNS = @"http://jabber.org/protocol/disco#info";
static NSString *const serviceDiscoItemsNS = @"http://jabber.org/protocol/disco#items";

@interface MXiMultiUserChatDiscovery : NSObject

@property (strong, nonatomic) NSString *domainName;
@property (copy, nonatomic) DiscoveryCompletionBlock discoveryCompletionBlock;

- (id)initWithDomainName:(NSString *)domainName andCompletionBlock:(DiscoveryCompletionBlock)completionBlock;

- (void)startDiscoveryOnQueue:(dispatch_queue_t)discoveryQueue;

- (void)didReceiveIQ:(XMPPIQ *)xmppiq;
@end