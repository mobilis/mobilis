//
//  MXiBeanDelegate.h
//  MXi
//
//  Created by Richard Wotzlaw on 02.06.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol MXiBeanDelegate <NSObject>

- (void)didReceiveBean:(MXiBean<MXiIncomingBean>* )theBean;

@end
