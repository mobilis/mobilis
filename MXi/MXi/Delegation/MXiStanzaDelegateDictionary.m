//
// Created by Martin Weißbach on 11/8/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import "MXiStanzaDelegateDictionary.h"
#import "MXiDelegateDictionary.h"

@interface MXiStanzaDelegateDictionary ()

- (void)initializeDelegateDictionaryIfNotExisting;

- (NSString *)keyForStanzaElement:(StanzaElement)element;

@end

@implementation MXiStanzaDelegateDictionary {
    __strong MXiDelegateDictionary *_delegateDictionary;
}

- (void)addDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement
{
    [self initializeDelegateDictionaryIfNotExisting];
    [_delegateDictionary addDelegate:delegate withSelector:selector forKey:[self keyForStanzaElement:stanzaElement]];
}

- (void)removeDelegate:(id)delegate forStanzaElement:(StanzaElement)stanzaElement
{
    [self initializeDelegateDictionaryIfNotExisting];
    [_delegateDictionary removeDelegate:delegate forKey:[self keyForStanzaElement:stanzaElement]];
}

- (void)removeDelegate:(id)delegate withSelector:(SEL)selector forStanzaElement:(StanzaElement)stanzaElement
{
    [self initializeDelegateDictionaryIfNotExisting];
    [_delegateDictionary removeDelegate:delegate withSelector:selector forKey:[self keyForStanzaElement:stanzaElement]];
}

- (NSArray *)delegatesforStanzaElement:(StanzaElement)stanzaElement
{
    [self initializeDelegateDictionaryIfNotExisting];
    return [_delegateDictionary delegatesForKey:[self keyForStanzaElement:stanzaElement]];
}

#pragma mark - Private Method Implementations

- (void)initializeDelegateDictionaryIfNotExisting
{
    if (_delegateDictionary == nil) {
        _delegateDictionary = [MXiDelegateDictionary new];
    }
}

- (NSString *)keyForStanzaElement:(StanzaElement)element
{
    return [[NSNumber numberWithInt:element] stringValue];
}

@end