//
//  MobiListEntry.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MobiListEntry.h"

@implementation MobiListEntry

@synthesize title, entryDescription, dueDate, done;

- (id)initWithTitle:(NSString *)aTitle
		description:(NSString* )aDescription
			dueDate:(NSDate *)aDueDate {
	self = [super init];
	
	if (self) {
		[self setTitle:aTitle];
		[self setEntryDescription:aDescription];
		[self setDueDate:aDueDate];
		
		CFUUIDRef uuid = CFUUIDCreate(NULL);
		entryID = (__bridge_transfer NSString *)CFUUIDCreateString(NULL, uuid);
		CFRelease(uuid);
	}
	
	return self;
}

- (id)initWithTitle:(NSString *)aTitle {
	return [self initWithTitle:aTitle description:nil dueDate:nil];
}

- (id)init {
	return [self initWithTitle:nil description:nil dueDate:nil];
}

- (NSString *)entryID {
	return entryID;
}

@end
