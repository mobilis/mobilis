//
//  AccountManager.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "AccountManager.h"

#import "KeychainItemWrapper.h"

@implementation AccountManager

+ (void)storeAccount:(Account *)account
{
    KeychainItemWrapper *keychain = [[KeychainItemWrapper alloc] initWithIdentifier:[[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleIdentifier"]
                                                                        accessGroup:nil];
    
    [keychain setObject:account.jid forKey:(__bridge id)(kSecAttrAccount)];
    [keychain setObject:account.hostName forKey:(__bridge id)(kSecAttrService)];
    [keychain setObject:account.port forKey:(__bridge id)(kSecAttrDescription)];
    [keychain setObject:account.password forKey:(__bridge id)(kSecValueData)];
}

+ (Account *)account
{
    KeychainItemWrapper *keychain = [[KeychainItemWrapper alloc] initWithIdentifier:[[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleIdentifier"]
                                                                        accessGroup:nil];
    
    Account *account = [Account new];
    account.jid = [keychain objectForKey:(__bridge id)(kSecAttrAccount)];
    account.password = [keychain objectForKey:(__bridge id)(kSecValueData)];
    account.hostName = [keychain objectForKey:(__bridge id)(kSecAttrService)];
    account.port = [keychain objectForKey:(__bridge id)(kSecAttrDescription)];
    
    return account;
}

@end
