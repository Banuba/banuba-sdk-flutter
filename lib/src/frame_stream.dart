import 'dart:async';

import '../banuba_sdk_plugin.dart';

class FrameStream extends FrameDataFlutterApi {
  FrameStream() {
    FrameDataFlutterApi.setUp(this);
  }

  final StreamController<String> _controller =
      StreamController<String>.broadcast();

  Stream<String> get stream => _controller.stream;

  @override
  void onFrame(String data) {
    _controller.add(data);
  }

  void dispose() {
    _controller.close();
    FrameDataFlutterApi.setUp(null);
  }
}



