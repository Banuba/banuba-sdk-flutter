import 'dart:async';

import '../banuba_sdk_plugin.dart';

class FrameStream extends FrameDataFlutterApi {
  FrameStream() {
    FrameDataFlutterApi.setUp(this);
  }

  final StreamController<FrameDataDto> _controller =
      StreamController<FrameDataDto>.broadcast();

  Stream<FrameDataDto> get stream => _controller.stream;

  @override
  void onFrame(FrameDataDto data) {
    _controller.add(data);
  }

  void dispose() {
    _controller.close();
    FrameDataFlutterApi.setUp(null);
  }
}



