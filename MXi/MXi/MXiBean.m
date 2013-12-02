//
//  MXiBean.m
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiBean.h"

@implementation MXiBean : NSObject

@synthesize beanId, beanType, to, from;

- (id)initWithBeanType:(BeanType)theBeanType {
	self = [super init];
	
	if (self) {
		beanType = theBeanType;
	}
	
	return self;
}

- (id)init {
	[NSException raise:@"Wrong initializer" format:@"Use initWithBeanType: or init of subtype"];
	return nil;
}

+ (NSString* )elementName {
	return nil;
}

+ (NSString* )iqNamespace {
	return nil;
}

@end