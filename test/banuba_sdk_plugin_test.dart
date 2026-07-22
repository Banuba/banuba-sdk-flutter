import 'package:banuba_sdk/banuba_sdk_plugin.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channelName =
      'dev.flutter.pigeon.banuba_sdk.BanubaSdkManager.startVideoRecording';
  const channel = BasicMessageChannel<Object?>(
    channelName,
    BanubaSdkManager.pigeonChannelCodec,
  );

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockDecodedMessageHandler(channel, null);
  });

  test('startVideoRecording sends the default orientation setting',
      () async {
    Object? receivedMessage;
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockDecodedMessageHandler<Object?>(channel, (message) async {
      receivedMessage = message;
      return <Object?>[null];
    });

    await BanubaSdkManager()
        .startVideoRecording('/tmp/video.mp4', true, 1080, 1920);

    expect(
      receivedMessage,
      <Object?>['/tmp/video.mp4', true, 1080, 1920, true, false],
    );
  });

  test('startVideoRecording sends an enabled orientation setting',
      () async {
    Object? receivedMessage;
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockDecodedMessageHandler<Object?>(channel, (message) async {
      receivedMessage = message;
      return <Object?>[null];
    });

    await BanubaSdkManager()
        .startVideoRecording('/tmp/video.mp4', false, 1920, 1080, false, true);

    expect(
      receivedMessage,
      <Object?>['/tmp/video.mp4', false, 1920, 1080, false, true],
    );
  });
}
