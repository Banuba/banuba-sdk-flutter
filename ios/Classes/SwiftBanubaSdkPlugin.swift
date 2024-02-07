import Flutter
import UIKit

public class SwiftBanubaSdkPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "banuba_sdk", binaryMessenger: registrar.messenger())
    let instance = SwiftBanubaSdkPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
    
    let messenger : FlutterBinaryMessenger = registrar.messenger()
    let banubaSdkManager = BanubaSdkPluginImpl()
    BanubaSdkManagerSetup.setUp(binaryMessenger: messenger, api: banubaSdkManager)

    let factory = NativeViewFactory(messenger: messenger)
    registrar.register(factory, withId: "effect_player_view")
  }
}
