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

- (void)connectionToRoomNotEstablished:(NSString *)roomJID;

- (void)didReceiveMultiUserChatMessage:(NSString *)message fromUser:(NSString *)user publishedInRomm:(NSString *)roomJID;

@end
