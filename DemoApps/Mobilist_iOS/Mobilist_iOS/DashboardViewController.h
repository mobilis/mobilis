//
//  DashboardViewController.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 16.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XMPPSettingsViewController.h"
#import "ListDetailViewController.h"
#import "TodoListCell.h"
#import "TodoListViewController.h"

@interface DashboardViewController : UIViewController
		<UITableViewDataSource, UITableViewDelegate>
{
	__weak IBOutlet UIButton *xmppSettingsButton;
	__weak IBOutlet UILabel *existingListsLabel;
	__weak IBOutlet UITableView *existingsListsTable;
}

- (void)showXMPPSettingsView:(id)sender;
- (void)showCreateListView:(id)sender;

@end
