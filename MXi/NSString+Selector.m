//
//  NSString+Selector.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/25/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "NSString+Selector.h"

@implementation NSString (Selector)

+ (NSString *)stringFromSelector:(SEL)selector
{
    return NSStringFromSelector(selector);
}

+ (SEL)selectorFromString:(NSString *)string
{
    return NSSelectorFromString(string);
}

@end
