//
//  MXiIQTypeLookup.h
//  MXi
//
//  Created by Richard Wotzlaw on 01.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MXiBean.h"

@interface MXiIQTypeLookup : NSObject

+ (NSString* )stringValueForIQType:(BeanType)beanType;
+ (BeanType)beanTypeForStringIQType:(NSString *)stringIQType;

@end
