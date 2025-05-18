# Integration Guide

1. [Configuration](#configuration)
    - [Android](#android)
    - [IOS](#ios)
2. [Usage](#usage)
3. [Add AR Effects](#add-ar-effects)
4. [Additional Methods](#additional-methods)

## Configuration

### Android

1. Define Banuba SDK version in the android [build.gradle](../example/android/build.gradle):

```groovy
    ext {
        bnb_sdk_version = '1.17.+'
    }
```

### IOS

1. Add the source and version of the Banuba SDK in the IOS [Podfile](../example/ios/Podfile):

```
    source 'https://github.com/sdk-banuba/banuba-sdk-podspecs.git'
    $bnb_sdk_version = '~> 1.17.1'
```

2. Add NSCameraUsageDescription in the [Info.plist](../example/ios/Runner/Info.plist):

```
    <key>NSCameraUsageDescription</key>
    <string>We use camera to render AR effects</string>
```

### Usage

1. Init `BanubaSdkManager`:

```dart
    await _banubaSdkManager.initialize([],
        "Client Token",
        SeverityLevel.info);
```

2. Attach `EffectPlayerView` to `BanubaSdkManager`:

```typescript
    final _epWidget = EffectPlayerWidget(key: null);

    ...

    await _banubaSdkManager.attachWidget(_epWidget.banubaId);
```

3. Start player:

```typescript
    await _banubaSdkManager.openCamera();
    await _banubaSdkManager.startPlayer();
```

4. Load and apply Effect:

```typescript
    await _banubaSdkManager.loadEffect("path to the effect", false);
```

### Add AR effects

[Banuba Face AR SDK](https://www.banuba.com/facear-sdk/face-filters) product is used on camera for applying various AR effects while making a content:

1. Android - Add [the folder with your effects](../example/effects/) to your project and setup it in the android [build.gradle](../example/android/app/build.gradle#L67) app module:

```groovy
    task copyEffects {
        copy {
            from flutter.source + '/effects'
            into 'src/main/assets/bnb-resources/effects'
        }
    }

    gradle.projectsEvaluated {
        preBuild.dependsOn(copyEffects)
    }
```

2. IOS - just link effects folder into `Runner` Xcode project (`File` -> `Add Files to 'Runner'...`).

### Additional methods

* Releases common Banuba SDK resources:

```dart
    static void deinitialize() {}
```

* Creates and attaches render processing to a specific view:

```dart
    void attachWidget(int banubaId);
```

* Closes Camera:

```dart
    void closeCamera();
```

* Stops render processing. Effects will not be applied:

```dart
    void stopPlayer();
```

* Unloads effect. Invoke this method after startPlayer:

```dart
    void unloadEffect();
```

* Used for passing specific expressions to interact with an effect:

```dart
    void evalJs(String script);
```

* Reload current effect config from the string provided:

```dart
    const script = """
        {
            "camera" : {},
                "background" : {
                // ...
            }
        }
    """;

    void reloadConfig(script);
```

* Sets camera zoom level:

```dart
    void setZoom(double zoom);
```

* Enables flashlight. Available only for back camera facing:

```dart
    void enableFlashlight(bool enabled);
```

* Start video recording:

```dart
    void startVideoRecording(
        String filePath, bool captureAudio, int width, int height);
```

* Stops video recording:

```dart
    void stopVideoRecording();
```

* Takes photo from camera:

```dart
    void takePhoto(String filePath, int width, int height);
```

* Sets camera facing: front, back:

```dart
    void setCameraFacing(bool front);
```

* Processes image with applied effect:

```dart
    void processImage(String sourceFilePath, String destFilePath);
```

* Starts image editing mode:

```dart
    void startEditingImage(String sourceImageFilePath);
```

* Ending editing image and save result to destination file:

```dart
    void endEditingImage(String destImageFilePath);
```

* Discard editing image mode:

```dart
    void discardEditingImage();
```
