//
//  MXiBean.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "XMPPJID.h"

@interface MXiBean : NSObject

typedef enum beanTypes {
	GET, SET, RESULT, ERROR
} BeanType;

@property (nonatomic, strong) NSString* beanId;
@property (nonatomic, strong) XMPPJID* to;
@property (nonatomic, strong) XMPPJID* from;
@property (nonatomic) BeanType beanType;

- (id) init;
- (id) initWithBeanType:(BeanType )theBeanType;

+ (NSString* )elementName;
+ (NSString* )namespace;

@end
