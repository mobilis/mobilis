//
// Created by Martin Weißbach on 10/18/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

typedef struct _DefaultSettingKeys {
    __strong NSString *SERVER_PORT = @"server.port";
    __strong NSString *SERVER_HOSTNAME = @"server.hostname";
    __strong NSString *SERVER_USERNAME = @"server.username";
    __strong NSString *SERVICE_JID = @"service.jid";
    __strong NSString *SERVICE_PASSWORD = @"service.password";
    __strong NSString *SERVICE_NAMESPACE = @"service.namespace";
    __strong NSString *SERVICE_TYPE = @"service.type";
} DefaultSettingKeys;

/*!
    A class that handles the access of the default settings file of the project.
 */
@interface DefaultSettings : NSObject

+ (instancetype)defaultSettings;

- (id)initWithDefaultSettings;

/*!
    This method retrieves the corresponding value for a given key from the default settings.

    @param key  The key of the settings value to retrieve. This key must be one of the DefaultSettingsKey.

    @return The value of the default settings for the given value or nil, if no key could be found.

    @see DefaultSettingsKeys
 */
- (NSString *)valueForKey:(NSString *)key;

@end