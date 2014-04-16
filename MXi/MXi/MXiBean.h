//
//  MXiBean.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "XMPPJID.h"
#import "DDXML.h"

@interface MXiBean : NSObject

typedef NS_ENUM(NSUInteger , BeanType)
{
	GET,
    SET,
    RESULT,
    ERROR
};

@property (nonatomic, strong) NSString* beanId;
@property (nonatomic, strong) XMPPJID* to;
@property (nonatomic, strong) XMPPJID* from;
@property (nonatomic) BeanType beanType;

- (id) init;
- (id) initWithBeanType:(BeanType )theBeanType;

+ (NSString* )elementName;
+ (NSString* )namespace;

- (void)fromXML:(NSXMLElement* )xml;
- (NSXMLElement* )toXML;

@end
