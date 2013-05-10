//
//  MobiListEntry.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 18.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MobiListEntry : NSObject
{
	NSString* entryID;
}

@property (nonatomic, retain) NSString* title;
@property (nonatomic, retain) NSString* entryDescription;
@property (nonatomic, retain) NSDate* dueDate;
@property (nonatomic) BOOL done;

- (id)initWithTitle:(NSString* )aTitle
		description:(NSString* )aDescription
			dueDate:(NSDate* )aDueDate;
- (id)initWithTitle:(NSString *)aTitle;

- (NSString* )entryID;

@end
