//
// Created by Martin Weißbach on 11/8/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <Foundation/Foundation.h>
#import "MXiDefinitions.h"


@interface MXiStanzaDelegateDictionary : NSObject
- (void)addDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement;

- (void)removeDelegate:(id)delegate forStanzaElement:(StanzaElement)stanzaElement;

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement;

- (NSArray *)delegatesforStanzaElement:(StanzaElement)stanzaElement;
@end