//
//  MXiBean.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "XMPPJID.h"

#if TARGET_OS_IPHONE
#import "DDXML.h"
#endif

@interface MXiBean : NSObject

typedef NS_ENUM(NSUInteger , BeanType)
{
	GET,
    SET,
    RESULT,
    ERROR,
    UNKNOWN
};

typedef NS_ENUM(NSUInteger , BeanContainer)
{
    BEAN_CONTAINER_IQ,
    BEAN_CONTAINER_MESSAGE,
};

@property (nonatomic, strong) NSString* beanId;
@property (nonatomic, strong) XMPPJID* to;
@property (nonatomic, strong) XMPPJID* from;
@property (nonatomic, readonly) BeanType beanType;
@property (nonatomic, readonly) BeanContainer beanContainer;

- (id) init;
- (id) initWithBeanType:(BeanType )beanType andBeanContainer:(BeanContainer)beanContainer;

+ (NSString* )elementName;
+ (NSString* )namespace;

- (void)fromXML:(NSXMLElement* )xml;
- (NSXMLElement* )toXML;

@end
