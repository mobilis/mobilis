//
// Created by Martin Weißbach on 10/18/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import "DefaultSettings.h"

@interface DefaultSettings ()

@property (strong, nonatomic) NSDictionary *settings;

@end

@implementation DefaultSettings

+ (instancetype)defaultSettings
{
    return [[self alloc] initWithDefaultSettings];
}

- (id)initWithDefaultSettings
{
    self = [super init];
    if (self) {

        [self readDefaultSettings];
    }

    return self;
}

- (NSString *)valueForKey:(const NSString *)key
{
    NSString *value = nil;
    @try {
        value = [self.settings valueForKeyPath:(NSString *)key];
    } @catch (NSException *exception) {
        NSLog(@"An exception was thrown: %@", exception.name);
    } @finally {
        return value;
    }
}

#pragma mark - Private Instance Methods

- (void)readDefaultSettings
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"Settings" ofType:@"plist"];
    self.settings = [NSDictionary dictionaryWithContentsOfFile:filePath];
}

@end