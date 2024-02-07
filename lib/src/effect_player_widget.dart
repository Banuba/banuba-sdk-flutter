import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

// This is used in the platform side to register the view.
const _platfromViewType = "effect_player_view";

class EffectPlayerWidget extends StatelessWidget {
  EffectPlayerWidget({super.key, this.onPlatformViewCreated});

  final banubaId = Random().nextInt(1 << 31);
  final PlatformViewCreatedCallback? onPlatformViewCreated;

  @override
  Widget build(BuildContext context) {
    // Pass parameters to the platform side.
    final Map<String, dynamic> creationParams = <String, dynamic>{
      "banubaId": banubaId
    };

    switch (defaultTargetPlatform) {
      case TargetPlatform.iOS:
        return UiKitView(
          viewType: _platfromViewType,
          creationParams: creationParams,
          onPlatformViewCreated: onPlatformViewCreated,
          creationParamsCodec: const StandardMessageCodec(),
        );
      case TargetPlatform.android:
        return AndroidView(
          viewType: _platfromViewType,
          creationParams: creationParams,
          onPlatformViewCreated: onPlatformViewCreated,
          creationParamsCodec: const StandardMessageCodec(),
        );
      default:
        throw UnsupportedError('Unsupported platform view');
    }
  }
}
