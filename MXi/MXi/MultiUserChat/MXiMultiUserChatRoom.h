//
// Created by Martin Weißbach on 10/21/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

@class XMPPJID;


@interface MXiMultiUserChatRoom : NSObject

@property (nonatomic) NSString *name;
@property (nonatomic) XMPPJID *jabberID;

- (id)initWithName:(NSString *)name jabberID:(XMPPJID *)jabberID;

@end