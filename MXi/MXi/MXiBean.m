//
//  MXiBean.m
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiBean.h"

@implementation MXiBean : NSObject

@synthesize beanId, beanType, to, from, elementName, iqNamespace;

- (id)initWithElementName:(NSString *)theElementName
			  iqNamespace:(NSString *)theIqNamespace
				 beanType:(BeanType)theBeanType {
	self = [super init];
	
	if (self) {
		elementName = theElementName;
		iqNamespace = theIqNamespace;
		beanType = theBeanType;
	}
	
	return self;
}

- (id)init {
	[NSException raise:@"Wrong initializer" format:@"Use initWithElementName:iqNamespace:beanType: or init of subtype"];
	return nil;
}

@end