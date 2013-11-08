//
//  DelegateDictionary.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "MXiDelegateDictionary.h"

#import "MXiDelegateSelectorMapping.h"

@interface MXiDelegateDictionary ()

@property (strong, nonatomic) NSMutableDictionary *delegateDictionary;

- (void)initializeDictionaryIfNotExisting;

@end

@implementation MXiDelegateDictionary

#pragma mark - Delegate Handling

- (void)addDelegate:(id)delegate withSelector:(SEL)selector forKey:(NSString *)key
{
    [self initializeDictionaryIfNotExisting];

    MXiDelegateSelectorMapping *delegateSelectorMapping = [[MXiDelegateSelectorMapping alloc] initWithDelegate:delegate
                                                                                             andSelector:selector];

    NSArray *registeredDelegates = [self.delegateDictionary objectForKey:key];
    if (!registeredDelegates) {
        [self.delegateDictionary setObject:@[delegateSelectorMapping] forKey:key];
    } else {
        BOOL duplicate = NO;
        for (MXiDelegateSelectorMapping *mapping in registeredDelegates) {
            if ([mapping isEqualToDelegate:delegate withSelector:selector]) {
                duplicate = YES;
                break;
            }
        }
        if (duplicate) return;

        NSMutableArray *newRegisteredDelegates = [[NSMutableArray alloc] initWithCapacity:registeredDelegates.count+1];
        [newRegisteredDelegates addObjectsFromArray:registeredDelegates];
        [newRegisteredDelegates addObject:delegateSelectorMapping];
        [self.delegateDictionary setObject:newRegisteredDelegates forKey:key];
    }
}

- (void)removeDelegate:(id)delegate forKey:(NSString *)key
{
    [self initializeDictionaryIfNotExisting];
    NSMutableArray *registeredDelegates = [self.delegateDictionary objectForKey:key];
    if (registeredDelegates) {
        NSMutableArray *mappingsToDelete = [NSMutableArray arrayWithCapacity:registeredDelegates.count];
        for (MXiDelegateSelectorMapping *mapping in registeredDelegates) {
            if ([mapping.delegate isEqual:delegate]) {
                [mappingsToDelete addObject:mapping];
            }
        }
        [registeredDelegates removeObjectsInArray:mappingsToDelete];
        [self.delegateDictionary setObject:registeredDelegates forKey:key];
    }
}

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forKey:(NSString *)key
{
    if (selector == nil) {
        [self removeDelegate:delegate forKey:key];
    } else {
        [self initializeDictionaryIfNotExisting];
        NSMutableArray *registeredDelegates = [self.delegateDictionary objectForKey:key];
        if (registeredDelegates) {
            MXiDelegateSelectorMapping *mappingToRemove = nil;
            for (MXiDelegateSelectorMapping *mapping in registeredDelegates) {
                if ([mapping isEqualToDelegate:delegate withSelector:selector]) {
                    mappingToRemove = mapping;
                    break;
                }
            }
            [registeredDelegates removeObject:mappingToRemove];
            [self.delegateDictionary setObject:registeredDelegates forKey:key];
        }
    }
}

- (NSArray *)delegatesForKey:(NSString *)key
{
    [self initializeDictionaryIfNotExisting];
    return [self.delegateDictionary objectForKey:key];
}

#pragma mark - Private Methods

- (void)initializeDictionaryIfNotExisting
{
    if (!self.delegateDictionary) {
        self.delegateDictionary = [[NSMutableDictionary alloc] initWithCapacity:10];
    }
}

@end
