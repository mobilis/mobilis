//
//  DelegateSelectorMapping.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/25/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MXi/MXiBean.h>

@interface MXiDelegateSelectorMapping : NSObject

@property (strong, nonatomic) id delegate;
@property (strong, nonatomic) NSString *selectorAsString;

- (id)initWithDelegate:(id)delegate andSelector:(SEL)selector;

- (SEL)selector;

- (BOOL)isEqualTo:(MXiDelegateSelectorMapping *)anotherMapping;
- (BOOL)isEqualToDelegate:(id)delegate withSelector:(SEL)selector;

@end
