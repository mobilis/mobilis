//
//  MXiDefinitions.h
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#ifndef MXi_MXiDefinitions____FILEEXTENSION___
#define MXi_MXiDefinitions____FILEEXTENSION___

typedef NS_ENUM(NSUInteger , ServiceType) {
    SINGLE,
    MULTI,
    SERVICE,
    RUNTIME
};

typedef NS_ENUM(NSUInteger , StanzaElement){
    PRESENCE,
    MESSAGE,
    IQ,
    UNKNOWN_STANZA
};

static NSString *const CoordinatorServiceNS = @"http://mobilis.inf.tu-dresden.de#services/CoordinatorService";
static NSString *const CoordinatorResourceName = @"Coordinator";

#endif
