//
//  DelegateDictionary.h
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
    @class  MXiDelegateDictionary
 
    This class associates objects and selectors with keys.
    Each key kan be associated to multiple object-selector pairs.
 */
@interface MXiDelegateDictionary : NSObject

/*!
    Associate a new object-selector pair with the given key or create a new association if the key is not existing
    in the dictionary.
 
    @param  delegate    The object receiving the message specified for the given key.
    @param  selector    The message the objects want to receive.
    @param  key         Semantically abstract key to store pairs of object and selector for.
 */
- (void)addDelegate:(id)delegate withSelector:(SEL)selector forKey:(NSString *)key;
/*!
    Remove a delegate association for the given key.
 
    @param  delegate    The object that should not be associated with the given key any longer.
    @param  key         The key to remove the object association from.
 */
- (void)removeDelegate:(id)delegate forKey:(NSString *)key;
/*!
    @deprecated Use `-removeDelegate:forKey` instead.
 
    @param  delegate    The object receiving messages for the given key.
    @param  selector    The message that should not be sent to the given object any longer.
    @param  key         The key to remove the object association from.
 */
- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forKey:(NSString *)key;

/*!
    Get all delegates of the given key.
 
    @param  key         The object-selector associations for the given key.
 
    @return All object-selector pairs associated with the key.
 */
- (NSArray *)delegatesForKey:(NSString *)key;

/*!
    @return All delegates for all keys.
 */
- (NSArray *)allDelegates;

@end
