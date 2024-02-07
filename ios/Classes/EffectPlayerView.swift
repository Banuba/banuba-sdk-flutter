import Flutter
import UIKit
import BNBSdkApi

class NativeViewFactory: NSObject, FlutterPlatformViewFactory {
    private var messenger: FlutterBinaryMessenger
    
    private static var knowViews = NSMapTable<NSNumber, EffectPlayerView>(
        keyOptions: .copyIn, valueOptions: .weakMemory)

    init(messenger: FlutterBinaryMessenger) {
        self.messenger = messenger
        super.init()
    }

    func create(
        withFrame frame: CGRect,
        viewIdentifier viewId: Int64,
        arguments args: Any?
    ) -> FlutterPlatformView {
        let view = EffectPlayerView(
            frame: frame,
            viewIdentifier: viewId,
            arguments: args,
            binaryMessenger: messenger)
            
        let banubaId: NSNumber = (args as! Dictionary<String, NSNumber>)["banubaId"]!
        NativeViewFactory.knowViews.setObject(view, forKey: banubaId)
        
        return view
    }
    
    func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
        return FlutterStandardMessageCodec.sharedInstance();
    }
    
    static func findEffectPlayer(banubaId: Int64) -> BNBSdkApi.EffectPlayerView? {
        return knowViews.object(forKey: banubaId as NSNumber)?.effectPlayerView()
    }
}

class EffectPlayerView: NSObject, FlutterPlatformView {
    private var epView: BNBSdkApi.EffectPlayerView

    init(
        frame: CGRect,
        viewIdentifier viewId: Int64,
        arguments args: Any?,
        binaryMessenger messenger: FlutterBinaryMessenger?
    ) {
        epView = BNBSdkApi.EffectPlayerView(frame: frame)
        epView.contentMode = .scaleAspectFill
        super.init()
    }

    func view() -> UIView {
        return epView
    }
    
    fileprivate func effectPlayerView() -> BNBSdkApi.EffectPlayerView {
        return epView
    }
}
