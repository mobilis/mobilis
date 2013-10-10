//
//  NSString+Selector.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/25/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (Selector)

+ (NSString *)stringFromSelector:(SEL)selector;
+ (SEL)selectorFromString:(NSString *)string;

@end
