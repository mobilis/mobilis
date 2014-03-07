//
//  AccountManager.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "AccountManager.h"

#import "FXKeychain/FXKeychain.h"

@implementation AccountManager

+ (void)storeAccount:(Account *)account
{
    FXKeychain *keychain = [[FXKeychain alloc] initWithService:[[NSBundle mainBundle] bundleIdentifier]
                                                   accessGroup:nil
                                                 accessibility:FXKeychainAccessibleWhenUnlocked];
    
    [keychain setObject:account.jid forKey:(__bridge id)(kSecAttrAccount)];
    [keychain setObject:account.hostName forKey:(__bridge id)(kSecAttrService)];
    [keychain setObject:account.port forKey:(__bridge id)(kSecAttrDescription)];
    [keychain setObject:account.password forKey:(__bridge id)(kSecValueData)];
    [keychain setObject:account.runtimeName forKey:(__bridge id)(kSecAttrLabel)];
}

+ (Account *)account
{
    FXKeychain *keychain = [[FXKeychain alloc] initWithService:[[NSBundle mainBundle] bundleIdentifier]
                                                   accessGroup:nil
                                                 accessibility:FXKeychainAccessibleWhenUnlocked];
    
    Account *account = [Account new];
    account.jid = [keychain objectForKey:(__bridge id)(kSecAttrAccount)];
    account.password = [keychain objectForKey:(__bridge id)(kSecValueData)];
    account.hostName = [keychain objectForKey:(__bridge id)(kSecAttrService)];
    account.port = [keychain objectForKey:(__bridge id)(kSecAttrDescription)];
    account.runtimeName = [keychain objectForKey:(__bridge id)(kSecAttrLabel)];
    
    return account;
}

@end
