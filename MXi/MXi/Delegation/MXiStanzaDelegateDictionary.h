//
// Created by Martin Weißbach on 11/8/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>

#import "MXiDefinitions.h"

/*!
    @class MXiStanzaDelegateDictionary
 
    This class stores object references to objects interested in arbitrary XMPP stanzas.
 
    @discussion Delegates of this class can specify the kind of stanza they want to be notified about.
                Basically it is possible to be notified on Message, IQ and Presence stanzas.
                Keep in mind that registerd objects interested in stanzas generally will NOT be notified on incoming
                Mobilis Beans.
 
    @see MXiBeanDelegateDictionary
 */
@interface MXiStanzaDelegateDictionary : NSObject

/*!
    Add a new object as a delegate for incoming Mobilis Beans.
 
    @param delegate         The object interested in the stanza element.
    @param selector         The message sent to the registered object when a stanza element arrived.
    @param stanzaElement    The kind of stanza the object want to observe.
 */
- (void)addDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement;
/*!
    Remove the given object from the list of objects interested in the stanza kind.
 
    @param  delegate        The object that does not want to be notified on arriving beans anymore.
    @param  stanzaElement   The kind of the bean the delegate does not want to be notified on anymore.
 */
- (void)removeDelegate:(id)delegate forStanzaElement:(StanzaElement)stanzaElement;
/*!
    @deprecated Use `-removeDelegate:forStanzaElement:` instead.
 
    @param delegate         The object not interested in receiving the given message for a given kind of stanza anymore.
    @param selector         The message sent to the registered object when a stanza element arrived.
    @param stanzaElement    The kind of stanza the object does not wand to observe anymore using the given callback selector.
 */
- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement;

/*!
    Get all delegates for a given stanza element.
 
    @param  stanzaElement   The type of the stanza to get the delegates for.
 
    @return An array of all objects acting as delegates for this stanza element.
 */
- (NSArray *)delegatesforStanzaElement:(StanzaElement)stanzaElement;

@end