//
//  MXiIncomingBean.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiBean.h"
#if TARGET_OS_IPHONE
#import "DDXML.h"
#endif

@protocol MXiIncomingBean <NSObject>

- (void)fromXML:(NSXMLElement* )xml;

@end
