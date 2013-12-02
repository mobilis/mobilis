//
//  Account.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Account : NSObject

@property (strong, nonatomic) NSString *jid, *password, *hostName;
@property (strong, nonatomic) NSNumber *port;

- (id)initWithJID:(NSString *)jabberID
         password:(NSString *)password
         hostName:(NSString *)hostName
             port:(NSNumber *)port;

@end
