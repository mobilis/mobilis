//
// Created by Martin Weißbach on 10/21/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <XMPPFramework/XMPPJID.h>
#import "MXiMultiUserChatRoom.h"


@implementation MXiMultiUserChatRoom

- (id)initWithName:(NSString *)name jabberID:(XMPPJID *)jabberID
{
    self = [super init];
    if (self) {
        self.name = name;
        self.jabberID = jabberID;
    }

    return self;
}

@end