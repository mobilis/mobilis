//
//  MXiDefinitions.h
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#ifndef MXi_MXiDefinitions____FILEEXTENSION___
#define MXi_MXiDefinitions____FILEEXTENSION___

typedef enum _ServiceType {
    SINGLE,
    MULTI
} ServiceType;

typedef enum _StanzaElement {
    PRESENCE,
    MESSAGE,
    IQ,
    UNKNOWN_STANZA
} StanzaElement;

static NSString *const CoordinatorService = @"http://mobilis.inf.tu-dresden.de#services/CoordinatorService";

#endif
