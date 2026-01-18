import 'dart:async';

import '../banuba_sdk_plugin.dart';

class EffectActivationCompletionStream extends EffectActivationCompletionFlutterApi {
  EffectActivationCompletionStream() {
    EffectActivationCompletionFlutterApi.setUp(this);
  }

  final StreamController<EffectActivationCompletionDto> _controller =
      StreamController<EffectActivationCompletionDto>.broadcast();

  Stream<EffectActivationCompletionDto> get stream => _controller.stream;

  @override
  void onEffectActivationFinished(EffectActivationCompletionDto data) {
    _controller.add(data);
  }

  void dispose() {
    _controller.close();
    EffectActivationCompletionFlutterApi.setUp(null);
  }
}
