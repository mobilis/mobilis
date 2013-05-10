//
//  DashboardViewController.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 16.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "DashboardViewController.h"

@interface DashboardViewController ()

@end

@implementation DashboardViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        UIImage* bgImage = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]
										pathForResource:@"light_toast" ofType:@"png"]];
		self.view.backgroundColor = [UIColor colorWithPatternImage:bgImage];
		
		[existingsListsTable setDataSource:self];
		[existingsListsTable setDelegate:self];
		
		UIBarButtonItem* addListItem = [[UIBarButtonItem alloc]
				initWithBarButtonSystemItem:UIBarButtonSystemItemAdd
										target:self
										action:@selector(showCreateListView:)];
		[[self navigationItem] setRightBarButtonItem:addListItem];
		
		UIBarButtonItem* settingsItem = [[UIBarButtonItem alloc]
				initWithTitle:@"Settings"
						style:UIBarButtonItemStylePlain
					   target:self
					   action:@selector(showXMPPSettingsView:)];
		[[self navigationItem] setLeftBarButtonItem:settingsItem];
		
		[[self navigationItem] setTitle:@"Mobilist"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	UINib* nib = [UINib nibWithNibName:@"TodoListCell" bundle:nil];
	
	[existingsListsTable registerNib:nib forCellReuseIdentifier:@"TodoListCell"];
}

/*
 * Table view data source
 */

- (UITableViewCell *) tableView:(UITableView *)tableView
		  cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	MobiList* list = [[[MobiListStore sharedStore] allLists] objectAtIndex:[indexPath row]];
	
	TodoListCell* cell = [existingsListsTable dequeueReusableCellWithIdentifier:@"TodoListCell"];
	
	[[cell listNameLabel] setText:[list name]];
	
	return cell;
}

- (NSInteger) tableView:(UITableView *)tableView
  numberOfRowsInSection:(NSInteger)section {
	return [[[MobiListStore sharedStore] allLists] count];
}

/*
 * End table view data source
 */

/*
 * Table view delegate
 */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	TodoListViewController* tlvc = [[TodoListViewController alloc]
				initWithMobiList:[[[MobiListStore sharedStore] allLists] objectAtIndex:[indexPath row]]];
	
	[[self navigationController] pushViewController:tlvc animated:YES];
}

/*
 * End table view delegate
 */

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)showXMPPSettingsView:(id)sender {
	XMPPSettingsViewController* xvc = [[XMPPSettingsViewController alloc] init];
	[[self navigationController] pushViewController:xvc animated:YES];
}

- (void)showCreateListView:(id)sender {
	ListDetailViewController* nlvc = [[ListDetailViewController alloc] initForNewList:YES];
	
	[nlvc setDismissBlock:^{
		[existingsListsTable reloadData];
	}];
	
	UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:nlvc];
	
	[self presentViewController:navController
					   animated:YES
					 completion:nil];
}

@end
