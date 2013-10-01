//
//  MXiOutgoingBean.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#if TARGET_OS_IPHONE
#import "MXiBean.h"
#import "DDXML.h"
#else
#import <MXi/MXi.h>
#endif

@protocol MXiOutgoingBean <NSObject>

- (NSXMLElement* )toXML;

@end
