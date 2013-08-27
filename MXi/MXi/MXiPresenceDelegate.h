//
//  PresenceDelegate.h
//  MXi
//
//  Created by Richard Wotzlaw on 02.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol MXiPresenceDelegate <NSObject>

- (void)didAuthenticate;
- (void)didDiscoverServiceWithNamespace:(NSString* )serviceNamespace
								   name:(NSString* )serviceName
								version:(NSInteger )version
							 atJabberID:(NSString* )serviceJID;
- (void)didDisconnectWithError:(NSError* )error;
- (void)didFailToAuthenticate:(NSXMLElement* )error;

@end
