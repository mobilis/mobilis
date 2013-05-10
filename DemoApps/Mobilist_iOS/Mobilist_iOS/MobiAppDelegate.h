//
//  MobiAppDelegate.h
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 14.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MXi/MXi.h>

@interface MobiAppDelegate : UIResponder <UIApplicationDelegate, PresenceDelegate, StanzaDelegate>
{
	MXiConnection* connection;
}

@property (strong, nonatomic) UIWindow *window;

@end
