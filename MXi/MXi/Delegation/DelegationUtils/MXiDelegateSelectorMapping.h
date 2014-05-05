//
//  DelegateSelectorMapping.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/25/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MXiDelegateSelectorMapping;

/*!
    @class MXiDelegateSelectorMapping
 
    Container class storing object-selector associations.
 
    @discussion This class does not take care if the given delegate objects responds to the specified message.
                It just stores a pair of object and message.
 */
@interface MXiDelegateSelectorMapping : NSObject

/*!
    @property delegate
 
    An arbitraty object thats related to a selector.
 */
@property (strong, nonatomic) id delegate;
/*!
    @property selectorAsString
 
    String representation of a message's selector.
 */
@property (strong, nonatomic) NSString *selectorAsString;

/*!
    Create a new Delegate-Selector-Mapping.
 
    @param  delegate    The object acting as delegate.
    @param  selector    The message's selector supposed to be received some time by the delegate.
 
    @return A new Delegate-Selector-Mapping.
 */
- (id)initWithDelegate:(id)delegate andSelector:(SEL)selector;

/*!
    @return The selector object of the string representation of the initially stored message.
 */
- (SEL)selector;

/*!
    Semantic equality check.
 
    @param  anotherMapping  Other Delegate-Selector-Mapping to check if receiver and parameter are semantically equal.
 
    @return TRUE, if the delegate objects and the selecor's string representations match.
 */
- (BOOL)isEqualTo:(MXiDelegateSelectorMapping *)anotherMapping;
/*!
    Check if the receiver represents the given parameter.
 
    @param  delegate    The delegate to check for equality.
    @param  selector    The selecror to check for equality.
 
    @return TRUE, if both delegate and selector match the properties stored by the receiver semantically.
 */
- (BOOL)isEqualToDelegate:(id)delegate withSelector:(SEL)selector;

@end
