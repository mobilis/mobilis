//
//  MXiService.h
//  MXi
//
//  Created by Martin Weißbach on 10/16/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import <Foundation/Foundation.h>

@class XMPPJID;

@interface MXiService : NSObject

@property (strong, nonatomic) NSString *name;
@property (nonatomic) NSInteger serviceVersion;
@property (nonatomic, strong) XMPPJID *jid;
@property (nonatomic, strong) NSString *namespace;

+ (id)serviceWithName:(NSString *)serviceName
            namespace:(NSString *)serviceNamespace
              version:(NSInteger)serviceVersion
             jabberID:(NSString *)jabberID;

- (id)initWithName:(NSString *)serviceName namespace:(NSString *)serviceNamespace version:(NSInteger)serviceVersion jabberID:(NSString *)jabberID;

- (BOOL)isEqalToService:(MXiService *)otherService;

@end
