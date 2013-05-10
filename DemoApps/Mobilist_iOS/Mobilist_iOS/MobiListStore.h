//
//  MobiListStore.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MobiList.h"

@interface MobiListStore : NSObject
{
	NSMutableArray* allLists;
}

+ (MobiListStore* )sharedStore;

- (NSMutableArray* )allLists;
- (void)addMobiList:(MobiList* )aList;
- (void)removeMobiList:(MobiList* )aList;

@end
