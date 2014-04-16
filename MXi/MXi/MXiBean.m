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
}

+ (NSString* )elementName {
	return nil;
}

+ (NSString* )namespace
{
	return nil;
}

#pragma mark - (De-)Serialization

- (void)fromXML:(DDXMLElement *)xml
{
    [NSException raise:@"Abstract Method" format:@"Subclasses of %@ have to implement -fromXML:", [MXiBean class]];
}

- (DDXMLElement *)toXML
{
    [NSException raise:@"Abstract Method" format:@"Subclasses of %@ have to implement -toXML", [MXiBean class]];
}


@end