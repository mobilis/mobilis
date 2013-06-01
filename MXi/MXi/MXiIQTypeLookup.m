//
//  MXiIQTypeLookup.m
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "MXiIQTypeLookup.h"

@implementation MXiIQTypeLookup

+ (NSString *)stringValueForIQType:(BeanType)beanType {
	if (beanType == GET) {
		return @"get";
	}
	
	if (beanType == SET) {
		return @"set";
	}
	
	if (beanType == RESULT) {
		return @"result";
	}
	
	if (beanType == ERROR) {
		return @"error";
	}
	
	return nil;
}

@end
