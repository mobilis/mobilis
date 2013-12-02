//
// Created by Martin Weißbach on 11/5/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import "MXiBeanDelegateDictionary.h"
#import "MXiDelegateDictionary.h"

@interface MXiBeanDelegateDictionary ()

- (void)initializeDelegateDictionaryIfNotExisting;

- (NSString *)classNameForClass:(Class)class;

@end

@implementation MXiBeanDelegateDictionary {
    __strong MXiDelegateDictionary *_delegateDictionary;
}

- (void)addDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass
{
    [self initializeDelegateDictionaryIfNotExisting];
    [_delegateDictionary addDelegate:delegate withSelector:selector forKey:[self classNameForClass:beanClass]];
}

- (void)removeDelegate:(id)delegate forBeanClass:(Class)beanClass
{
    [self initializeDelegateDictionaryIfNotExisting];
    [_delegateDictionary removeDelegate:delegate forKey:[self classNameForClass:beanClass]];
}

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass
{
    [self initializeDelegateDictionaryIfNotExisting];
    [_delegateDictionary removeDelegate:delegate withSelector:selector forKey:[self classNameForClass:beanClass]];
}

- (NSArray *)delegatesForBeanClass:(Class)beanClass
{
    [self initializeDelegateDictionaryIfNotExisting];
    return [_delegateDictionary delegatesForKey:[self classNameForClass:beanClass]];
}

#pragma mark - Private Method Implementations

- (void)initializeDelegateDictionaryIfNotExisting
{
    if (_delegateDictionary == nil) {
        _delegateDictionary = [MXiDelegateDictionary new];
    }
}

- (NSString *)classNameForClass:(Class)class
{
    return [class description];
}

@end