//
//  MXiBeanToXML.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MXi.h"

@interface MXiBeanConverter : NSObject

+ (NSXMLElement* )beanToIQ:(MXiBean<MXiOutgoingBean>* )outBean;
+ (void)beanFromIQ:(XMPPIQ* )xml intoBean:(MXiBean<MXiIncomingBean>* )inBean;

@end
