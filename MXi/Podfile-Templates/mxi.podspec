#
# Be sure to run `pod spec lint MobilisMXi.podspec' to ensure this is a
# valid spec and remove all comments before submitting the spec.
#
# To learn more about the attributes see http://docs.cocoapods.org/specification.html
#
Pod::Spec.new do |s|
  s.name         = "MobilisMXi"
  s.version      = "0.4.0"
  s.summary      = "iOS / OSX Client Library for Mobilis based Services"
  s.homepage     = "http://mobilis.inf.tu-dresden.de"

  # Specify the license type. CocoaPods detects automatically the license file if it is named
  # 'LICENCE*.*' or 'LICENSE*.*', however if the name is different, specify it.
  # s.license      = 'MIT (example)'
  # s.license      = { :type => 'MIT (example)', :file => 'FILE_LICENSE' }

  s.author       = 'Richard W', 'Martin W', 'Markus W'

  # Specify the location from where the source should be retrieved.
  #
  s.source       = { :git => "https://github.com/mobilis/mobilis.git", :branch => 'MXi_DM' }


  # ――― MULTI-PLATFORM VALUES ――――――――――――――――――――――――――――――――――――――――――――――――― #

  s.prefix_header_contents = <<-'END'
  #define HAVE_XMPP_SUBSPEC_COREDATASTORAGE
  #define HAVE_XMPP_SUBSPEC_RECONNECT
  #define HAVE_XMPP_SUBSPEC_ROSTER
  #define HAVE_XMPP_SUBSPEC_XEP_0045
  #define HAVE_XMPP_SUBSPEC_XEP_0082
  #define HAVE_XMPP_SUBSPEC_XEP_0203
  END

  # If this Pod runs on both platforms, then specify the deployment
  # targets.
  #
  s.ios.deployment_target = '5.0'
  s.osx.deployment_target = '10.7'

  # A list of file patterns which select the source files that should be
  # added to the Pods project. If the pattern is a directory then the
  # path will automatically have '*.{h,m,mm,c,cpp}' appended.
  #
  s.source_files = 'MXi/MXi'

  # A list of file patterns which select the header files that should be
  # made available to the application. If the pattern is a directory then the
  # path will automatically have '*.h' appended.
  #
  # If you do not explicitly set the list of public header files,
  # all headers of source_files will be made public.
  #
  # s.public_header_files = 'Classes/**/*.h'

  # Specify a list of frameworks that the application needs to link
  # against for this Pod to work.
  #
  # s.framework  = ''
  s.frameworks = 'SystemConfiguration', 'Foundation', 'Security', 'CFNetwork'

  # Specify a list of libraries that the application needs to link
  # against for this Pod to work.
  #
  s.library   = 'xml2'
  # s.libraries = 'iconv', 'xml2'

  # If this Pod uses ARC, specify it like so.
  #
  s.requires_arc = true
  non_arc_files = 'MXi/MXi/IncomingBeanDetection.m'

  s.exclude_files = non_arc_files
  s.subspec 'no-arc' do |sna|
    sna.requires_arc = false
    sna.source_files = non_arc_files
  end

  # If you need to specify any other build settings, add them to the
  # xcconfig hash.
  #
  s.xcconfig = { 'HEADER_SEARCH_PATHS' => '$(SDKROOT)/usr/include/libxml2' }

  # Finally, specify any Pods that this Pod depends on.
  #
  s.dependency 'XMPPFramework/Core'
  s.dependency 'XMPPFramework/XEP-0045'
  s.dependency 'XMPPFramework/Reconnect'
  s.dependency 'XMPPFramework/Roster'

  s.header_dir = 'MXi'
end
