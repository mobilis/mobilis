//
//  NewListViewController.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 16.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "ListDetailViewController.h"

@interface ListDetailViewController ()

@end

@implementation ListDetailViewController

@synthesize dismissBlock, list;

- (id)initForNewList:(BOOL)isNew {
	self = [super initWithNibName:@"ListDetailViewController" bundle:nil];
	
    if (self) {
		UIImage* bgImage = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]
															 pathForResource:@"light_toast" ofType:@"png"]];
		self.view.backgroundColor = [UIColor colorWithPatternImage:bgImage];
		
		if (isNew) {
			UIImage* bgImage = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]
																 pathForResource:@"light_toast" ofType:@"png"]];
			self.view.backgroundColor = [UIColor colorWithPatternImage:bgImage];
			
			UIBarButtonItem* doneItem = [[UIBarButtonItem alloc]
										 initWithBarButtonSystemItem:UIBarButtonSystemItemDone
										 target:self
										 action:@selector(save:)];
			[[self navigationItem] setRightBarButtonItem:doneItem];
			
			UIBarButtonItem* cancelItem = [[UIBarButtonItem alloc]
										   initWithBarButtonSystemItem:UIBarButtonSystemItemCancel
										   target:self
										   action:@selector(cancel:)];
			[[self navigationItem] setLeftBarButtonItem:cancelItem];
		}
    }
	
    return self;
}

- (id)init {
	return [self initForNewList:YES];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    @throw [NSException exceptionWithName:@"Wrong initializer"
								   reason:@"Use initForNewList"
								 userInfo:nil];
	
	return nil;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	[listNameTextField becomeFirstResponder];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)save:(id)sender {
	NSString* listNameText = [listNameTextField text];
	
	if ([listNameText length] < 3) {
		UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Name too short"
														message:@"List name must be at least 3 characters long"
													   delegate:nil
											  cancelButtonTitle:@"Change name"
											  otherButtonTitles:nil];
		[alert show];
		
		return;
	}
	
	MobiList* theNewList = [[MobiList alloc] init];
	[theNewList setName:listNameText];
	
	MobiListEntry* theEntry = [[MobiListEntry alloc] initWithTitle:@"Erster Eintrag"
													   description:@"Nur ein Beispieleintrag zur Demonstration"
														   dueDate:[NSDate date]];
	[theNewList addListEntry:theEntry];
	
	MobiListStore* sharedStore = [MobiListStore sharedStore];
	[sharedStore addMobiList:theNewList];
	
	[[self navigationController] dismissViewControllerAnimated:YES completion:dismissBlock];
}

- (void)cancel:(id)sender {
	[[self navigationController] dismissViewControllerAnimated:YES completion:dismissBlock];
}

@end
