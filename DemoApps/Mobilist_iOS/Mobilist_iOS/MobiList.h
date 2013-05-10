//
//  MobiList.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MobiListEntry.h"

@interface MobiList : NSObject
{
	NSString* listID;
	NSString* creator;
	NSMutableArray* entries;
}

@property (nonatomic, strong) NSString* name;

- (id)initWithListEntries:(NSMutableArray* )theListEntries;

- (NSString* )listID;
- (MobiListEntry* )createEntry;
- (void)addListEntry:(MobiListEntry* )aListEntry;
- (void)removeListEntry:(MobiListEntry* )aListEntry;
- (NSMutableArray* )allEntries;
- (MobiListEntry* )entryAtIndex:(NSInteger)index;

@end
