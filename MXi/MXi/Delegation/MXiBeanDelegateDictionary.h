//
// Created by Martin Weißbach on 11/5/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>


@interface MXiBeanDelegateDictionary : NSObject
- (void)addDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;

- (void)removeDelegate:(id)delegate forBeanClass:(Class)beanClass;

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;

- (NSArray *)delegatesForBeanClass:(Class)beanClass;
@end