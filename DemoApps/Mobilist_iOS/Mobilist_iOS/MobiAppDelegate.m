//
//  MobiAppDelegate.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 14.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MobiAppDelegate.h"
#import "DashboardViewController.h"
#import <MXi/MXi.h>

@implementation MobiAppDelegate

/*
 * Presence delegate
 */

- (void)didAuthenticate {
	//[connection sendTestMessageWithContent:@"Hey! How are you?" to:@"kallistoo@jabber.ccc.de"];
	NSLog(@"Authenticated");
	
	NSXMLElement* pingRequest = [NSXMLElement elementWithName:@"PingRequest" xmlns:@"mobilist:iq:ping"];
	
	NSXMLElement* content = [NSXMLElement elementWithName:@"content" xmlns:@"mobilist:iq:ping"];
	[content setStringValue:@"Hallo Welt"];
	
	[pingRequest addChild:content];
	
	NSXMLElement* iq = [NSXMLElement elementWithName:@"iq" xmlns:@"mobilist:iq:ping"];
	[iq addAttributeWithName:@"type" stringValue:@"get"];
	[iq addAttributeWithName:@"to" stringValue:@"mobilis@mymac.box/Mobilist_v1#1"];
	[iq addAttributeWithName:@"from" stringValue:[connection jabberID]];
	[iq addChild:pingRequest];
	
	
	
	/*NSXMLElement* getLocationRequest = [NSXMLElement elementWithName:@"GetLocationResponse"
															   xmlns:@"treasurehunt:iq:getlocation"];
	NSXMLElement* iq = [NSXMLElement elementWithName:@"iq" xmlns:@"treasurehunt:iq:getlocation"];
	[iq addAttributeWithName:@"type" stringValue:@"result"];
	[iq addAttributeWithName:@"to" stringValue:@"mobilis@mymac.box/TreasureHunt_v1#1"];
	[iq addAttributeWithName:@"from" stringValue:[connection jabberID]];
	[iq addChild:getLocationRequest];*/
	
	[connection send:iq];
}

- (void)didDisconnectWithError:(NSError* )error {
	
}

- (void)didFailToAuthenticate:(NSXMLElement* )error {
	NSLog(@"Failed to authenticate");
}

/*
 * Stanza delegate
 */

- (void)didReceiveMessage:(XMPPMessage* )message {
	NSLog(@"Received message:\n%@", [message prettyXMLString]);
}

- (BOOL)didReceiveIQ:(XMPPIQ* )iq {
	NSLog(@"Received iq:\n%@", [iq prettyXMLString]);
	return true;
}

- (void)didReceivePresence:(XMPPPresence* )presence {
	NSLog(@"Received presence:\n%@", [presence prettyXMLString]);
}

- (void)didReceiveError:(NSXMLElement* )error {
	NSLog(@"Received error:\n%@", [error prettyXMLString]);
}

/*
 * App delegate
 */

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
	//[MxiLog log];
	self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
	DashboardViewController* dvc = [[DashboardViewController alloc] init];
	
	UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:dvc];
	[self.window setRootViewController:navController];
	
	self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
	return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
	// Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
	// Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
	// Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
	// If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
	// Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
	connection = [MXiConnection connectionWithJabberID:@"gannimet@jabber.ccc.de"
											  password:@"dRit!g!zW"
									  presenceDelegate:self
										stanzaDelegate:nil];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
	// Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
