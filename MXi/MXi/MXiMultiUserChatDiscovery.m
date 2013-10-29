//
// Created by Martin Weißbach on 10/21/13.
// Copyright (c) 2013 TU Dresden. All rights reserved.
//


#import <XMPPFramework/XMPPIQ.h>
#import "MXiMultiUserChatDiscovery.h"
#import "MXiMultiUserChatRoom.h"

@interface MXiMultiUserChatDiscovery ()

@property (nonatomic) NSMutableArray *cachedDomainItems;
@property (nonatomic) NSMutableArray *discoveredRooms;

@property (nonatomic) dispatch_queue_t discoveryQueue;

- (XMPPIQ *)constructInformationQueryWithAddressee:(NSString *)jid elementID:(NSString *)elementID queryType:(NSString *)queryType;

- (BOOL)isDiscoItemsResult:(XMPPIQ *)xmppiq;
- (BOOL)hasItemMUCFeature:(XMPPIQ *)xmppiq;
- (void)readRoomItemsFromIQ:(XMPPIQ *)xmppiq;

@end

@implementation MXiMultiUserChatDiscovery

- (id)initWithDomainName:(NSString *)domainName andCompletionBlock:(DiscoveryCompletionBlock)completionBlock
{
    self = [super init];
    if (self) {
        self.domainName = domainName;
        self.discoveryCompletionBlock = completionBlock;

        self.discoveredRooms = [NSMutableArray arrayWithCapacity:10];
    }

    return self;
}

- (void)startDiscoveryOnQueue:(dispatch_queue_t)discoveryQueue
{
    if (!discoveryQueue)
        [NSException raise:NSInvalidArgumentException format:@"The Queue to schedule room discovery must not be nil"];

    self.discoveryQueue = discoveryQueue;

    dispatch_async(self.discoveryQueue, ^
    {
        XMPPIQ *iq = [self constructInformationQueryWithAddressee:self.domainName
                                                        elementID:@"discoverDomainItems"
                                                        queryType:serviceDiscoItemsNS];
        [[MXiConnectionHandler sharedInstance] sendElement:iq];
    });
}

- (void)didReceiveIQ:(XMPPIQ *)xmppiq
{
    if ([xmppiq isResultIQ] && [[xmppiq attributeStringValueForName:@"id"] isEqualToString:@"discoverDomainItems"]) {
        [self domainItemsRequest:xmppiq];
    }
    if ([xmppiq isResultIQ] && [self isDiscoItemsResult:xmppiq]) {
        if ([self hasItemMUCFeature:xmppiq]) {
            XMPPIQ *iq = [self constructInformationQueryWithAddressee:[[xmppiq from] full]
                                                            elementID:@"roomDiscovery"
                                                            queryType:serviceDiscoItemsNS];
            [[MXiConnectionHandler sharedInstance] sendElement:iq];
        }
    }
    if ([xmppiq isResultIQ] && [[xmppiq attributeStringValueForName:@"id"] isEqualToString:@"roomDiscovery"]) {
        [self readRoomItemsFromIQ:xmppiq];
    }
}

#pragma mark - Build Queries

- (XMPPIQ *)constructInformationQueryWithAddressee:(NSString *)jid elementID:(NSString *)elementID queryType:(NSString *)queryType
{
    NSXMLElement *query = [[NSXMLElement alloc] initWithName:@"query" xmlns:queryType];
    XMPPIQ *iq = [[XMPPIQ alloc] initWithType:@"get" to:[XMPPJID jidWithString:jid] elementID:elementID child:query];

    return iq;
}

- (void)domainItemsRequest:(XMPPIQ *)xmppiq
{
    NSArray *items = [[xmppiq childElement] elementsForName:@"item"];
    self.cachedDomainItems = [NSMutableArray arrayWithCapacity:items.count];
    int index = 0;
    for (NSXMLElement *element in items) {
        NSString *jid = [element attributeStringValueForName:@"jid"];
        NSString *elementID = [NSString stringWithFormat:@"elementDisco_%i", index++];
        XMPPIQ *iq = [self constructInformationQueryWithAddressee:jid elementID:elementID queryType:serviceDiscoInfoNS];
        dispatch_async(self.discoveryQueue, ^
        {
            [[MXiConnectionHandler sharedInstance] sendElement:iq];
        });
        [self.cachedDomainItems addObject:jid];
    }
}

#pragma mark - Helper

- (BOOL)isDiscoItemsResult:(XMPPIQ *)xmppiq
{
    NSCharacterSet *characterSet = [NSCharacterSet characterSetWithCharactersInString:@"_"];
    NSString *idAttribute = [xmppiq attributeStringValueForName:@"id"];

    NSArray *idAttributeComponents = [idAttribute componentsSeparatedByCharactersInSet:characterSet];
    if (idAttributeComponents && idAttributeComponents.count >= 1)
        if ([((NSString *) idAttributeComponents[0]) isEqualToString:@"elementDisco"])
            return YES;

    return NO;
}

- (BOOL)hasItemMUCFeature:(XMPPIQ *)xmppiq
{
    NSArray *features = [[xmppiq childElement] elementsForName:@"feature"];
    BOOL isMUC = NO;
    for (NSXMLElement *element in features)
        if ([[element attributeStringValueForName:@"var"] isEqualToString:@"http://jabber.org/protocol/muc"]) {
            isMUC = YES;
            break;
        }

    return isMUC;
}

- (void)readRoomItemsFromIQ:(XMPPIQ *)xmppiq
{
    NSArray *rooms = [[xmppiq childElement] elementsForName:@"item"];
    @synchronized (self.discoveredRooms) {
        for (NSXMLElement *roomElement in rooms)
            [self.discoveredRooms addObject:[[MXiMultiUserChatRoom alloc] initWithName:[roomElement attributeStringValueForName:@"name"]
                                                                              jabberID:[XMPPJID jidWithString:[roomElement attributeStringValueForName:@"jid"]]]];
    }
    self.discoveryCompletionBlock(YES,[NSArray arrayWithArray:self.discoveredRooms]);
}

@end