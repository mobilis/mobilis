# Overview 

Mobilis is a platform for collaborative mobile applications. Its main purpose is to support developers in buidling collaborative applications consisting of smartphone apps, server parts, Web-based clients and even ad-hoc connected nodes like sensors and actuators. For the mobile client part we are focusing on Android while we provide an HTML5 based client framework for all other platforms. But the main part of Mobilis is not cross-platform development or offering collaborative functionality under Android. It rather covers the whole application compound which typically consists of a server part and the network protocols, too. We provide a service hosting environment with dynamic deployment and runtime support. We further provide a mechanism to develop network protocols in a platform-independent and service-oriented way. Application compounds can be tested within an emulation environment with distributed scripting capabilities. The glue for all these parts is the eXtensible Messaging and Presence Protocol (XMPP) and its many community extensions. 

For further information please visit the project Wiki: https://github.com/mobilis/mobilis/wiki

![Mobilis Overview](https://raw.github.com/mobilis/mobilis/master/mobilis-overview.png)

# Branches

* master - The master branch represents the last stable version using the MSDL-based code generation using XSLT. The example apps (master branch) all run on this version. Multi-server functionality is already built-in. (Mobilis 3.0)
* superior_CodeGen - The codegen branch is an experimental branch where we replaced MSDL with a new approach for code generation called XPD (XMPP Protocol Description). It is based on FreeMarker instead of XSLT and uses a much cleaner syntax for protocol descriptions especially linking to existing XSDs for describing the data structures. If you want to run the XPD version you need the following:
** the branch superior_CodeGen from mobilis/mobilis
** the XPD generator on BitBucket: https://bitbucket.org/mwwm/mobilis-xpd
** optionally the CodeGen branch on mobilis/9Cards to see an example application (only working for iOS)
