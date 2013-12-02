//
//  AccountManager.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "Account.h"

@interface AccountManager : NSObject

/**
 *  Store account information in the Keychains.
 *
 *  @param account The account to store in the Keychains.
 */
+ (void)storeAccount:(Account *)account;
/**
 *  Fetch the account that is currently stored in the Keychains.
 *
 *  @return Account containing the information to connect to a XMPP server and login.
 *          If no information are stored in the Keychain, the fields of the account will be nil.
 */
+ (Account *)account;

@end
