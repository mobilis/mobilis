//
//  DelegateSelectorMapping.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/25/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "MXiDelegateSelectorMapping.h"
#import "NSString+Selector.h"

@implementation MXiDelegateSelectorMapping

- (id)initWithDelegate:(id)delegate andSelector:(SEL)selector
{
    self = [super init];
    if (self) {
        self.delegate = delegate;
        self.selectorAsString = [NSString stringFromSelector:selector];
    }
    
    return self;
}

- (SEL)selector
{
    return [NSString selectorFromString:_selectorAsString];
}

- (BOOL)isEqualTo:(MXiDelegateSelectorMapping *)anotherMapping
{
    if ([_delegate isEqual:anotherMapping.delegate] &&
        [_selectorAsString isEqualToString:anotherMapping.selectorAsString]) {
        return YES;
    } else return NO;
}

- (BOOL)isEqualToDelegate:(id)delegate withSelector:(SEL)selector
{
    MXiDelegateSelectorMapping *intermediaryMapping = [[MXiDelegateSelectorMapping alloc] initWithDelegate:delegate
                                                                                               andSelector:selector];
    return [self isEqualTo:intermediaryMapping];
}

@end
