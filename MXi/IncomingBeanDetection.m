//
//  IncomingBeanDetection.m
//  ACDSense
//
//  Created by Martin Weißbach on 8/24/13.
//  Copyright (c) 2013 Technische Universität Dresden. All rights reserved.
//

#import "IncomingBeanDetection.h"

#import <MXi/MXiIncomingBean.h>

#import <objc/runtime.h>

@implementation IncomingBeanDetection

- (NSArray *)detectBeans
{    
    int numberOfClasses;
    Class *classes = NULL;
    
    classes = NULL;
    numberOfClasses = objc_getClassList(NULL, 0);
    
    NSMutableArray *incomingBeans = [[NSMutableArray alloc] initWithCapacity:10];
    if (numberOfClasses > 0) {
        classes = malloc(sizeof(Class) * numberOfClasses);
        numberOfClasses = objc_getClassList(classes, numberOfClasses);
        for (int i = 0; i < numberOfClasses; i++) {
            Class class = classes[i];
            if (class_getClassMethod(class, @selector(conformsToProtocol:))) {
                if ([class conformsToProtocol:@protocol(MXiIncomingBean)]) {
                    [incomingBeans addObject:[[[class alloc] init] autorelease]];
                }
            }
        }
        free(classes);
    }
    return [incomingBeans autorelease];
}


@end
