//
//  EntryDetailViewController.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 26.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MobiListEntry.h"
#import "MobiList.h"

@interface EntryDetailViewController : UIViewController
{
	__weak IBOutlet UITextField *titleTextField;
	__weak IBOutlet UITextView *descriptionTextField;
	__weak IBOutlet UIDatePicker *dueDatePicker;
}

@property (nonatomic, strong) MobiListEntry* entry;
@property (nonatomic, strong) MobiList* parent;
@property (nonatomic, copy) void (^dismissBlock)(void);

- (id)initForNewEntry:(BOOL)isNew;
- (IBAction)backgroundTapped:(id)sender;

@end