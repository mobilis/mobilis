//
//  MXiBeanToXML.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#if TARGET_OS_IPHONE
#import "MXi.h"
#else
#import <MXi/MXi.h>
#endif

@interface MXiBeanConverter : NSObject

+ (NSXMLElement* )beanToIQ:(MXiBean* )outBean;
+ (void)beanFromIQ:(XMPPIQ* )xml intoBean:(MXiBean *)inBean;

@end
