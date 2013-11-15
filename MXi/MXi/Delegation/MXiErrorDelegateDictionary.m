//
//  MXiErrorDelegateDictionary.m
//  MXi
//
//  Created by Martin Weißbach on 11/15/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiErrorDelegateDictionary.h"
#import "MXiDelegateDictionary.h"

@implementation MXiErrorDelegateDictionary
{
    __strong MXiDelegateDictionary *_delegateDictionary;
}

- (void)addErrorDelegate:(id)delegate withSelector:(SEL)selector
{
    [self initializeDictionaryIfNotExisting];
    [_delegateDictionary addDelegate:delegate withSelector:selector forKey:[self classNameForClass:[delegate class]]];
}

- (void)removeErrorDelegate:(id)delegate
{
    [self initializeDictionaryIfNotExisting];
    [_delegateDictionary removeDelegate:delegate forKey:[self classNameForClass:[delegate class]]];
}

- (NSArray *)delegates
{
    return [_delegateDictionary allDelegates];
}

#pragma mark - Private Helper Methods

- (NSString *)classNameForClass:(Class)class
{
    return [class description];
}

- (void)initializeDictionaryIfNotExisting
{
    if (!_delegateDictionary) {
        _delegateDictionary = [MXiDelegateDictionary new];
    }
}

@end
