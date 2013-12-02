//
//  MXiMultiUserChatDelegateDictionary.m
//  MXi
//
//  Created by Martin Weißbach on 11/22/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiMultiUserChatDelegateDictionary.h"
#import "MXiMultiUserChatDelegate.h"
#import "MXiMultiUserChatRoom.h"
#import "XMPPJID.h"

@implementation MXiMultiUserChatDelegateDictionary
{
    __strong NSMutableDictionary *_multiUserChatDelegateDictionary;
}

- (id)init
{
    self = [super init];
    if (self) {
        _multiUserChatDelegateDictionary = [NSMutableDictionary dictionaryWithCapacity:5];
    }
    return self;
}

- (BOOL)addDelegate:(id <MXiMultiUserChatDelegate>)delegate forMultiUserChatRoom:(NSString *)roomJID
{
    if ([_multiUserChatDelegateDictionary valueForKey:roomJID] == nil) {
        [_multiUserChatDelegateDictionary setObject:delegate forKey:roomJID];
        return YES;
    }
    return NO;
}

- (void)removeDelegate:(id <MXiMultiUserChatDelegate>)delegate forMultiUserChatRoom:(NSString *)roomJID
{
    [_multiUserChatDelegateDictionary removeObjectForKey:roomJID];
}

- (id<MXiMultiUserChatDelegate>)delegateForMultiUserChatRoom:(NSString *)roomJID
{
    return [_multiUserChatDelegateDictionary valueForKey:roomJID];
}

@end
