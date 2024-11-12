# Banuba Flutter Plugin

## Overview

The [Banuba Face AR Plugin](https://www.banuba.com/facear-sdk/face-filters) offers a comprehensive suite of augmented reality features for enhancing photos and live video feeds. Key features include:
- Face and hand tracking
- 3D face filters
- Virtual try-on for various accessories
- Color filters (LUTs)
- Face touch-up
- Virtual backgrounds
- Screen recording and screenshot capabilities


## Requirements

- **Android**: API level 23 (Android 6) or higher
- **iOS**: Version 13.0 or higher

## Usage

### License

Test all SDK features for free during a 14-day trial. Send us a message to start the to start your [Face AR trial](https://www.banuba.com/facear-sdk/face-filters#form).
Feel free to [contact us](https://www.banuba.com/support) if you have any questions regarding Banuba Flutter Plugin.

### Installation

To install the Banuba Face AR Flutter plugin, run the following command in your terminal:

```bash
flutter pub add banuba_sdk
```

### Integration guide

Follow our [Integration Guide](mdDocs/integration_guide.md) for detailed integration steps.

### Launch

1. Set up your [Flutter development environment](https://docs.flutter.dev/get-started/editor).
2. Clone the repository.
3. Copy and Paste your Client Token into the appropriate section of [example/lib/main.dart](example/lib/main.dart#L31).
4. Run `flutter pup get`  in the root directory to install dependencies.

#### IOS Specific

* Go to `ios` directory and execute `pod install`. This will install all required iOS dependencies.
* Return back to root.

#### Run the Example

* Connect a device and run `flutter run`.
* Alternatively, use an IDE like XCode, IntelliJ, or Visual Studio Code to launch the app.

### Useful Docs

- [Minimal Sample](example/)
- [Example App on GitHub](https://github.com/Banuba/quickstart-flutter-plugin)
- [Documentation](https://docs.banuba.com/face-ar-sdk-v1)
- [Flutter AR Features](https://www.banuba.com/blog/flutter-ar-features-integration)
- [Face AR SDK](https://www.banuba.com/facear-sdk/face-filters)
- [More about Banuba](https://www.banuba.com/)

### Dependencies

| Platform  | Version |
|:---------:|:-------:|
|   Dart    |  3.3.0  |
|  Flutter  | 3.19.2  |
|  Android  |  6.0+   |
|    iOS    |  13.0+  |
