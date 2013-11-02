//
//  MXiMultiUserChatMessage.h
//  MXi
//
//  Created by Martin Weißbach on 8/31/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

#if TARGET_OS_IPHONE
#import "XMPPFramework.h"
#else
#import <XMPPFramework.h>
#endif

@interface MXiMultiUserChatMessage : XMPPMessage

+ (id)messageWithBody:(NSString *)body;

- (id)initWithBody:(NSString *)body;

- (NSString *)bodyContent;

@end
