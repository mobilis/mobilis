//
//  MXiOutgoingBean.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXi.h"

@protocol MXiOutgoingBean <NSObject>

- (NSXMLElement* )payloadToXML;
- (NSDictionary* )beanAttributes;

@end
