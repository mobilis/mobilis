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
#import <MXi/MXiOutgoingBean.h>
#endif

#import "MXiOutgoingBean.h"

@interface MXiBeanConverter : NSObject

+ (NSXMLElement* )beanToIQ:(MXiBean<MXiOutgoingBean>* )outBean;
+ (void)beanFromIQ:(XMPPIQ* )xml intoBean:(MXiBean <MXiIncomingBean> *)inBean;

@end
