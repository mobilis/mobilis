//
//  MXiMultiUserChatDelegateDictionary.h
//  MXi
//
//  Created by Martin Weißbach on 11/22/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//



@protocol MXiMultiUserChatDelegate;
@class MXiMultiUserChatRoom;

@interface MXiMultiUserChatDelegateDictionary : NSObject

- (BOOL)addDelegate:(id <MXiMultiUserChatDelegate>)delegate forMultiUserChatRoom:(NSString *)roomJID;
- (void)removeDelegate:(id<MXiMultiUserChatDelegate>)delegate forMultiUserChatRoom:(NSString *)roomJID;

- (id <MXiMultiUserChatDelegate>)delegateForMultiUserChatRoom:(NSString *)roomJID;

@end
