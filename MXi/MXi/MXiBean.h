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

@property (nonatomic, strong) NSString* elementName;
@property (nonatomic, strong) NSString* iqNamespace;

- (id) init;
- (id) initWithElementName:(NSString* )theElementName
			   iqNamespace:(NSString* )theIqNamespace
				  beanType:(BeanType )theBeanType;

@end
