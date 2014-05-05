//
//  MXiDefinitions.h
//  MXi
//
//  Created by Martin Weißbach on 11/8/13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#ifndef MXi_MXiDefinitions_H_
#define MXi_MXiDefinitions_H_

/*!
    @typedef ServiceType
 
    @constant SINGLE    Used only by client applications communicating with a single instance Mobilis service.
    @constant MULTI     Used only by client applications communicating with a multi instance Mobilis service.
    @constant SERVICE   Used only by Mobilis Runtime services.
    @constant RUNTIME   Used only by Mobilis Runtime applications communicating with other Mobilis runtimes.
 */
typedef NS_ENUM(NSUInteger , ServiceType) {
    SINGLE,
    MULTI,
    SERVICE,
    RUNTIME
};

/*!
    @typedef StanzaElement
 
    @constant PRESENCE          Represents stanzas of kind presence.
    @constant MESSAGE           Represents stanzas of kind message.
    @constant IQ                Represents stanzas of kine iq.
    @constant UNKNOWN_STANZA    An unknown stanza arrived. This constant mostly indicates an error that occured in stanza
                                processing, not an error iq or message itself.
 */
typedef NS_ENUM(NSUInteger , StanzaElement){
    PRESENCE,
    MESSAGE,
    IQ,
    UNKNOWN_STANZA
};

static NSString *const CoordinatorServiceNS = @"http://mobilis.inf.tu-dresden.de#services/CoordinatorService";
static NSString *const CoordinatorResourceName = @"Coordinator";

#endif
