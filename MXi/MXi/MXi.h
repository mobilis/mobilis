#import <Foundation/Foundation.h>
#if TARGET_OS_IPHONE
// Third party stuff
#import "XMPP.h"
#import "XMPPReconnect.h"

// Custom classes
#import "MXiConnection.h"
#import "MXiConnectionHandler.h"
#import "MXiBean.h"
#import "MXiBeanConverter.h"
#import "MXiIQTypeLookup.h"

#else

// Third party stuff
#import <XMPP.h>
#import <XMPPReconnect.h>

// Custom classes
#import <MXi/MXiConnection.h>
#import <MXi/MXiConnectionHandler.h>
#import <MXi/MXiBean.h>
#import <MXi/MXiBeanConverter.h>
#import <MXi/MXiIQTypeLookup.h>

#endif
