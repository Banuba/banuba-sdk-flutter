import 'package:pigeon/pigeon.dart';

enum SeverityLevel {
  debug,
  info,
  warning,
  error,
}

/// An entry point to Banuba SDK
@HostApi()
abstract class BanubaSdkManager {
  /// Intialize common banuba SDK resources. This must be called before any
  /// other call. Counterpart `deinitialize` exists.
  ///
  /// parameter resourcePath: paths to cutom resources folders
  /// parameter clientTokenString: client token
  /// parameter logLevel: log level
  static void initialize(List<String> resourcePath, String clientTokenString,
      SeverityLevel logLevel) {}

  /// Releases common Banuba SDK resources.
  static void deinitialize() {}

  /// Creates and attaches render processing to a specific view
  void attachWidget(int banubaId);

  /// Opens Camera
  void openCamera();

  /// Closes Camera
  void closeCamera();

  /// Starts render processing. Next, use loadEffect for applying effects
  void startPlayer();

  /// Stops render processing. Effects will not be applied.
  void stopPlayer();

  /// Loads effect. Invoke this method after startPlayer
  void loadEffect(String path, bool synchronously);

  /// Unloads effect. Invoke this method after startPlayer
  void unloadEffect();

  /// Used for passing specific expressions to interact with an effect.
  void evalJs(String script);

  /// TODO document 
  void reloadConfig(String script);

  /// Sets camera zoom level
  void setZoom(double zoom);

  /// Enables flashlight. Available only for back camera facing.
  void enableFlashlight(bool enabled);

  /// Start video recording
  void startVideoRecording(
      String filePath, bool captureAudio, int width, int height, [bool frontCameraMirror = true]);

  /// Stops video recording
  @async
  void stopVideoRecording();

  /// Takes photo from camera
  @async
  void takePhoto(String filePath, int width, int height);

  /// Sets camera facing: front, back
  void setCameraFacing(bool front);

  /// Processes image with applied effect
  @async
  void processImage(String sourceFilePath, String destFilePath);

  /// Starts image editing mode
  @async
  void startEditingImage(String sourceImageFilePath);

  /// Ending editing image and save result to destination file
  @async
  void endEditingImage(String destImageFilePath);

  /// Discard editing image mode
  void discardEditingImage();
}
