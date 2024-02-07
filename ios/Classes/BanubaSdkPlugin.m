#import "BanubaSdkPlugin.h"
#if __has_include(<banuba_sdk/banuba_sdk-Swift.h>)
#import <banuba_sdk/banuba_sdk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "banuba_sdk-Swift.h"
#endif

@implementation BanubaSdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBanubaSdkPlugin registerWithRegistrar:registrar];
}
@end
