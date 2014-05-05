//
// Created by Martin Weißbach on 11/5/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

/*!
    @class MXiBeanDelegateDictionary
 
    This class stores object references to objects interested in incoming Mobilis Beans.
 */
@interface MXiBeanDelegateDictionary : NSObject

/*!
    Add a new object as a delegate for incoming Mobilis Beans.
 
    @param delegate     The object interested in Mobilis Beans.
    @param selector     The message sent to the registered object when a Mobilis Bean arrived.
    @param beanClass    The class of the bean the object want to observe.
 
    @discussion Due to the specification of a given bean class, objects will not be notified on the arrival of beans
                unless the bean arrived is of the class the delegate registered for.
 */
- (void)addDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;

/*!
    Remove the given object from the list of objects interested in the given bean.
    
    @param  delegate    The object that does not want to be notified on arriving beans anymore.
    @param  beanClass   The class of the bean the delegate does not want to be notified on anymore.
 */
- (void)removeDelegate:(id)delegate forBeanClass:(Class)beanClass;

/*!
    @deprecated Use `-removeDelegate:forBeanClass:` instead.
 
    @param delegate     The object not interested in receiving the given message for a given bean class anymore.
    @param selector     The message sent to the registered object when a Mobilis Bean arrived.
    @param beanClass    The bean class the object does not wand to observe anymore using the given callback selector.
 */
- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forBeanClass:(Class)beanClass;

/*!
    Get all delegates for a given bean class.
 
    @param  beanClass   The class of the bean to get the delegates for.
 
    @return An array of all objects acting as delegates for this bean class.
 */
- (NSArray *)delegatesForBeanClass:(Class)beanClass;

@end