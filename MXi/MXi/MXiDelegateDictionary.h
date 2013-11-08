//
//  DelegateDictionary.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MXiDelegateDictionary : NSObject

- (void)addDelegate:(id)delegate withSelector:(SEL)selector forKey:(NSString *)key;

- (void)removeDelegate:(id)delegate forKey:(NSString *)key;

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forKey:(NSString *)key;

- (NSArray *)delegatesForKey:(NSString *)key;
@end
