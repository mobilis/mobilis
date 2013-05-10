//
//  MobiListStore.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MobiListStore.h"

@implementation MobiListStore

+ (MobiListStore *)sharedStore {
	static MobiListStore* sharedInstance = nil;
	
	if (!sharedInstance) {
		sharedInstance = [[MobiListStore alloc] init];
	}
	
	return sharedInstance;
}

- (id)init {
	self = [super init];
	
	if (self) {
		allLists = [NSMutableArray array];
	}
	
	return self;
}

- (NSMutableArray *)allLists {
	return allLists;
}

- (void)addMobiList:(MobiList *)aList {
	[allLists addObject:aList];
}

- (void)removeMobiList:(MobiList *)aList {
	[allLists removeObjectIdenticalTo:aList];
}

@end
