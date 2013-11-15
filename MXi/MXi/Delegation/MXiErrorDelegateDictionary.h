//
//  MXiErrorDelegateDictionary.h
//  MXi
//
//  Created by Martin Weißbach on 11/15/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//



@interface MXiErrorDelegateDictionary : NSObject

- (void)addErrorDelegate:(id)delegate withSelector:(SEL)selector;

- (void)removeErrorDelegate:(id)delegate;

- (NSArray *)delegates;
@end
