//
//  MXiBeanToXML.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MXi.h"

@interface MXiBeanToXML : NSObject

+ (NSXMLElement* )beanToXML:(MXiBean<MXiOutgoingBean>* )outBean;

@end
