//
//  TodoListViewController.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 25.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MobiList.h"
#import "MobiListEntry.h"
#import "TodoListEntryCell.h"
#import "EntryDetailViewController.h"

@interface TodoListViewController : UITableViewController

@property (nonatomic, strong) MobiList* theList;

- (id)initWithMobiList:(MobiList* )aList;
- (void)showComposeListEntryView:(id)sender;

@end
