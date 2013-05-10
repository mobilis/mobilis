//
//  NewListViewController.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 16.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MobiList.h"
#import "MobiListStore.h"
#import "MobiListEntry.h"

@interface ListDetailViewController : UIViewController
{
	__weak IBOutlet UITextField *listNameTextField;
}

@property (nonatomic, copy) void (^dismissBlock)(void);
@property (nonatomic, strong) MobiList* list;

- (id)initForNewList:(BOOL)isNew;

@end
