//
//  MXiMultiUserChatDelegate.h
//  MXi
//
//  Created by Martin Weißbach on 8/31/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol MXiMultiUserChatDelegate <NSObject>

- (void)connectionToRoomEstablished:(NSString *)roomJID;

@optional

- (void)didReceiveMultiUserChatMessage:(NSString *)message fromUser:(NSString *)user publishedInRoom:(NSString *)roomJID;

- (void)userWithJid:(NSString *)fullJid didJoin:(NSString *)presence room:(NSString *)roomJid;
- (void)userWithJid:(NSString *)fullJid didLeaveRoom:(NSString *)roomJid;
- (void)userWithJid:(NSString *)fullJid didUpdate:(NSString *)presence inRoom:(NSString *)roomJid;

@end
