//
//  StanzaDelegate.h
//  MXi
//
//  Created by Richard Wotzlaw on 02.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol MXiStanzaDelegate <NSObject>

- (void)didReceiveMessage:(XMPPMessage* )message;
- (BOOL)didReceiveIQ:(XMPPIQ* )iq;
- (void)didReceivePresence:(XMPPPresence* )presence;
- (void)didReceiveError:(NSXMLElement* )error;

@end
