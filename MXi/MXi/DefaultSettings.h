//
// Created by Martin Weißbach on 10/18/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

const static NSString *SERVER_PORT = @"server.port";
const static NSString *SERVER_HOSTNAME = @"server.hostname";
const static NSString *SERVER_USERNAME = @"server.username";
const static NSString *SERVICE_JID = @"service.jid";
const static NSString *SERVICE_PASSWORD = @"service.password";
const static NSString *SERVICE_NAMESPACE = @"service.namespace";
const static NSString *SERVICE_TYPE = @"service.type";

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
- (NSString *)valueForKey:(const NSString *)key;

@end