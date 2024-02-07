import Foundation
import BNBSdkApi
import BNBSdkCore
import AVFoundation
import os

public let pluginDomain = "com.banuba.sdk.flutter.plugin"

public class BanubaSdkPluginImpl: NSObject, BanubaSdkManager, VideoRecorderDelegate {
    
    private static let logger = Logger(
        subsystem: Bundle.main.bundleIdentifier ?? "",
        category: "BanubaSdkPlugin"
    )
    
    private var banubaSdkManager: BNBSdkApi.BanubaSdkManager = BNBSdkApi.BanubaSdkManager()
    private let configuration: EffectPlayerConfiguration = .init()
    private var recordingVideoCompletion: ((Result<Void, Error>) -> Void)?
    
    deinit {
        deinitialize()
    }
    
    func deinitialize() {
        Self.logger.debug("deinitialize")
        banubaSdkManager.stopEffectPlayer()
        banubaSdkManager.removeRenderTarget()
        banubaSdkManager.destroyEffectPlayer()
        BNBSdkApi.BanubaSdkManager.deinitialize()
    }
    
    func initialize(resourcePath: [String], clientTokenString: String, logLevel: SeverityLevel) {
        Self.logger.debug("initialize")
        var flutterResPath = resourcePath
        flutterResPath.append(Bundle.main.bundlePath + "/bnb-resources")
        flutterResPath.append(Bundle.main.bundlePath) // for "effects"
        BNBSdkApi.BanubaSdkManager.initialize(
            resourcePath: flutterResPath,
            clientTokenString: clientTokenString,
            logLevel: BNBSeverityLevel(rawValue: logLevel.rawValue) ?? .info
        )
        banubaSdkManager.setup(configuration: configuration)
    }
    
    func attachWidget(banubaId: Int64) {
        Self.logger.debug("attachWidget")
        
        guard let view = NativeViewFactory.findEffectPlayer(banubaId: banubaId) else {
            print("View with id \(banubaId)")
            return
        }
         
        banubaSdkManager.setRenderTarget(
            view: view,
            playerConfiguration: nil
        )
    }
    
    func openCamera() {
        Self.logger.debug("openCamera")
        banubaSdkManager.input.startCamera()
        
        // Warm up internal Banuba SDK instances before video recording
        let tmpFileURL = FileManager.default.temporaryDirectory.appendingPathComponent("tmp.mov")
        banubaSdkManager.output?.startRecordingWithURL(tmpFileURL, delegate: self)
        banubaSdkManager.output?.stopRecording()
        try? FileManager.default.removeItem(at: tmpFileURL)
    }
    
    func closeCamera() {
        Self.logger.debug("closeCamera")
        banubaSdkManager.input.stopCamera()
    }
    
    func loadEffect(path: String, synchronously: Bool) {
        Self.logger.debug("loadEffect = \(path), synchronously = \(synchronously)")
        banubaSdkManager.loadEffect(path, synchronous: synchronously)
    }
    
    func unloadEffect() {
        Self.logger.debug("unloadEffect")
        banubaSdkManager.loadEffect("")
    }

    func startPlayer() {
        Self.logger.debug("startPlayer")
        banubaSdkManager.startEffectPlayer()
        if banubaSdkManager.renderTarget == nil {
            // Setup offscreen mode
            banubaSdkManager.setRenderTarget(layer: CAMetalLayer(), playerConfiguration: configuration)
        }
    }
    
    func stopPlayer() {
        Self.logger.debug("stopPlayer")
        banubaSdkManager.stopEffectPlayer()
    }
    
    func evalJs(script: String) {
        Self.logger.debug("evalJs = \(script)")
        banubaSdkManager.effectManager()?.current()?.evalJs(script, resultCallback: nil)
    }
    
    func startVideoRecording(
        filePath: String,
        captureAudio: Bool,
        width: Int64,
        height: Int64
    ) {
        Self.logger.debug("startVideoRecording = \(filePath); audio = \(captureAudio); w = \(width); h = \(height)")
        let url = URL(fileURLWithPath: filePath)
        adjustPlayerSize(width: width, height: height)
        if captureAudio {
            banubaSdkManager.input.startAudioCapturing()
        }
        banubaSdkManager.output?.reset()
        banubaSdkManager.output?.startRecordingWithURL(
            url,
            delegate: self
        )
    }
    
    func stopVideoRecording(completion: @escaping (Result<Void, Error>) -> Void) {
        Self.logger.debug("stopVideoRecording")
        recordingVideoCompletion = completion
        banubaSdkManager.output?.stopRecording()
    }
    
    func takePhoto(
        filePath: String,
        width: Int64,
        height: Int64,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        Self.logger.debug("takePhoto = \(filePath); w = \(width); h = \(height)")
        let url = URL(fileURLWithPath: filePath)
        adjustPlayerSize(width: width, height: height)
        banubaSdkManager.output?.takeSnapshot(handler: { snapshot in
            do {
                try snapshot?.pngData()?.write(to: url)
                completion(.success(()))
            } catch {
                completion(.failure(error))
            }
        })
    }
    
    func setCameraFacing(front: Bool) throws {
        Self.logger.debug("setCameraFacing front = \(front)")
        let cameraSessionType: CameraSessionType = front ? .FrontCameraSession : .BackCameraSession
        banubaSdkManager.input.switchCamera(to: cameraSessionType, completion: {})
    }
    
    func processImage(
        sourceFilePath: String,
        destFilePath: String,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        Self.logger.warning("DEPTECATED! processImage")
        let start = CACurrentMediaTime()
        
        let sourceUrl = URL(fileURLWithPath: sourceFilePath)
        let destinationUrl = URL(fileURLWithPath: destFilePath)
        
        guard
            let imageData = try? Data(contentsOf: sourceUrl),
            let image = UIImage(data: imageData, scale: 1.0)
        else {
            let error = NSError(domain: pluginDomain, code: 0, userInfo: [NSDebugDescriptionErrorKey: "Unable to open image"])
            completion(.failure(error))
            return
        }
        
        banubaSdkManager.startEditingImage(image) { [weak banubaSdkManager] _, _ in
            banubaSdkManager?.captureEditedImage { resultImage in
                defer { banubaSdkManager?.stopEditingImage() }
                guard let resultImage else {
                    let error = NSError(domain: pluginDomain, code: 1, userInfo: [NSDebugDescriptionErrorKey: "Unable to apply AR effect to image"])
                    completion(.failure(error))
                    return
                }
                do {
                    try resultImage.pngData()?.write(to: destinationUrl)
                    completion(.success(()))
                    Self.logger.debug("Time to save image: \n path = \(destinationUrl.absoluteString), \n time = \(CACurrentMediaTime() - start) ms")
                } catch {
                    completion(.failure(error))
                }
            }
        }
    }
    
    func startEditingImage(sourceImageFilePath: String, completion: @escaping (Result<Void, Error>) -> Void) {
        Self.logger.debug("startEditingImage = \(sourceImageFilePath)")
        
        let imageUrl = URL(fileURLWithPath: sourceImageFilePath)
        
        guard
            let imageData = try? Data(contentsOf: imageUrl),
            let image = UIImage(data: imageData, scale: 1.0)
        else {
            let error = NSError(domain: pluginDomain, code: 0, userInfo: [NSDebugDescriptionErrorKey: "Unable to open image"])
            completion(.failure(error))
            return
        }
        
        banubaSdkManager.startEditingImage(image) { numberOfFaces, _ in
            completion(.success(()))
        }
    }
    
    func endEditingImage(destImageFilePath: String, completion: @escaping (Result<Void, Error>) -> Void) {
        Self.logger.debug("endEditingImage = \(destImageFilePath)")
        let imageURL = URL(fileURLWithPath: destImageFilePath)
        
        let start = CACurrentMediaTime()
        banubaSdkManager.captureEditedImage() { [weak banubaSdkManager] resultImage in
            defer { banubaSdkManager?.stopEditingImage() }
            guard let resultImage else {
                let error = NSError(domain: pluginDomain, code: 1, userInfo: [NSDebugDescriptionErrorKey: "Unable to apply AR effect to image"])
                completion(.failure(error))
                return
            }
            do {
                try resultImage.pngData()?.write(to: imageURL)
                completion(.success(()))
                Self.logger.debug("Time to save image: \n path = \(imageURL.absoluteString), \n time = \(CACurrentMediaTime() - start) ms")
            } catch {
                completion(.failure(error))
            }
        }
    }
    
    func discardEditingImage() {
        Self.logger.debug("discardEditingImage")
        banubaSdkManager.stopEditingImage()
    }

    func setZoom(zoom: Double) {
        Self.logger.debug("setZoom = \(zoom)")
        _ = banubaSdkManager.input.setZoomFactor(Float(zoom))
    }
    
    func enableFlashlight(enabled: Bool) {
        Self.logger.debug("enableFlashlight = \(enabled)")
        _ = banubaSdkManager.input.setTorch(
            mode: enabled ? AVCaptureDevice.TorchMode.on : AVCaptureDevice.TorchMode.off)
    }
    
    private func adjustPlayerSize(width: Int64, height: Int64) {
        let newSize = CGSize(width: Double(width), height: Double(height))
        if configuration.renderSize != newSize {
            configuration.renderSize = newSize
        }
    }
    
    // MARK: - VideoRecorderDelegate
    public func onRecorderStateChanged(_ state: BNBSdkApi.VideoRecordingState) {}
    
    public func onRecordingFinished(success: Bool, error: Error?) {
        banubaSdkManager.input.stopAudioCapturing()
        if !success, let error {
            Self.logger.warning("onRecordingFinished with error: \(error)")
            recordingVideoCompletion?(.failure(error))
        } else {
            Self.logger.debug("onRecordingFinished success")
            recordingVideoCompletion?(.success(()))
        }
        recordingVideoCompletion = nil
    }
    
    public func onRecordingProgress(duration: TimeInterval) {}
}
