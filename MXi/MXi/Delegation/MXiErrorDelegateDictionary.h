//
//  MXiErrorDelegateDictionary.h
//  MXi
//
//  Created by Martin Weißbach on 11/15/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
    @class MXiErrorDelegateDictionary
 
    This class stores object references to objects interested in incoming error IQs or Messages.
 
    @discussion Error Beans defined by Mobilis will not be forwarded to registered delegates of this class.
 */
@interface MXiErrorDelegateDictionary : NSObject

/*!
    Add a new object as a delegate for incoming error IQs or Messages.
 
    @param delegate The object interested in error information.
    @param selector The message sent to the registered object when an error IQ or Message arrived.
 */
- (void)addErrorDelegate:(id)delegate withSelector:(SEL)selector;

- (void)removeErrorDelegate:(id)delegate;

- (NSArray *)delegates;

@end
