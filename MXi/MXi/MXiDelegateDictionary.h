//
//  DelegateDictionary.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MXiDelegateDictionary : NSObject

+ (instancetype)sharedInstance;

+ (instancetype) alloc __attribute__((unavailable("alloc not available, call sharedInstance instead")));
- (instancetype) init __attribute__((unavailable("init not available, call sharedInstance instead")));
+ (instancetype) new __attribute__((unavailable("new not available, call sharedInstance instead")));

- (void)addDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;
- (void)removeDelegate:(id)delegate forBeanClass:(Class)beanClass;
- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;
- (NSArray *)delegatesForBeanClass:(Class)beanClass;

@end
