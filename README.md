# Banuba Flutter Plugin

## Overview

Quickly add AR filters, virtual backgrounds, digital makeup, and other features 
to your app with 
[Banuba Face AR Plugin](https://docs.banuba.com/far-sdk/tutorials/development/basic_integration?platform=flutter).

Diverse functionalities and easy integration makes this plugin a perfect fit for 
many niches:

- Video conferencing
- Social media
- eCommerce
- eLearning
- Security
- Banking & Finance
- Healthcare
- Gaming

Key features:

- AR face tracking
- Hand tracking
- 3D face filters
- Virtual makeup
- Color filters (LUTs)
- Face touch-up
- Virtual backgrounds
- Virtual try-on of accessories, rings, piercings, etc.
- Biometric matching
- Teeth bleaching simulation
- Etc.

Banuba Face AR Flutter plugin doesn’t collect any user data and everything is 
processed on the user’s device. This means it is secure by design and 
**compliant with GDPR and other similar regulations**.

Besides Flutter, Banuba Face AR SDK is also compatible with:

- Native Android
- Native iOS
- React Native
- Web
- Mac
- Windows
- Unity


## [Requirements](https://docs.banuba.com/far-sdk/tutorials/capabilities/system_requirements)

## Usage

### License

[Start a 14-day free trial](https://www.banuba.com/facear-sdk/face-filters#form) 
and see how Face AR SDK Flutter plugin works. No credit card information is 
needed.

Feel free to [contact us](https://www.banuba.com/support) if you have any 
questions regarding this plugin.

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
4. Run `flutter pub get`  in the root directory to install dependencies.

#### IOS Specific

* Go to `ios` directory and execute `pod install`. This will install all required iOS dependencies.
* Return back to root.

#### Run the Example

* Connect a device and run `flutter run`.
* Alternatively, use an IDE like XCode, IntelliJ, or Visual Studio Code to launch the app.

### Useful Docs

- [Minimal Sample](example/)
- [Example App on GitHub](https://github.com/Banuba/quickstart-flutter-plugin)
- [Documentation](https://docs.banuba.com)
- [Flutter AR Features](https://www.banuba.com/blog/flutter-ar-features-integration)
- [Face AR SDK](https://www.banuba.com/facear-sdk/face-filters)
- [More about Banuba](https://www.banuba.com/)