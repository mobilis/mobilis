//
//  MobiList.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MobiList.h"

@implementation MobiList

@synthesize name;

- (id)initWithListEntries:(NSMutableArray *)theListEntries {
	self = [super init];
	
	if (self) {
		entries = theListEntries;
		CFUUIDRef uuid = CFUUIDCreate(NULL);
		listID = (__bridge_transfer NSString *)CFUUIDCreateString(NULL, uuid);
		CFRelease(uuid);
	}
	
	return self;
}

- (id)init {
	return [self initWithListEntries:[NSMutableArray array]];
}

- (NSString *)listID {
	return listID;
}

- (MobiListEntry *)createEntry {
	MobiListEntry* newEntry = [[MobiListEntry alloc] init];
	[self addListEntry:newEntry];
	
	return newEntry;
}

- (void)addListEntry:(MobiListEntry *)aListEntry {
	[entries addObject:aListEntry];
}

- (void)removeListEntry:(MobiListEntry *)aListEntry {
	[entries removeObjectIdenticalTo:aListEntry];
}

- (NSMutableArray *)allEntries {
	return entries;
}

- (MobiListEntry *)entryAtIndex:(NSInteger)index {
	return [entries objectAtIndex:index];
}

@end
