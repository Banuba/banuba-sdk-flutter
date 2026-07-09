package com.banuba.sdk.flutter.banuba_sdk;

import static com.banuba.sdk.flutter.BanubaSdkPluginGen.BanubaSdkManager;
import static com.banuba.sdk.flutter.BanubaSdkPluginImpl.BanubaSdkManagerIml;

import androidx.annotation.NonNull;

import com.banuba.sdk.flutter.EffectPlayerView.NativeViewFactory;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

/**
 * BanubaSdkPlugin
 */
public class BanubaSdkPlugin implements FlutterPlugin {
    private BanubaSdkManagerIml mManagerImpl;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(
            "effect_player_view", new NativeViewFactory()
        );
        mManagerImpl = new BanubaSdkManagerIml(flutterPluginBinding.getApplicationContext());
        BanubaSdkManager.setUp(flutterPluginBinding.getBinaryMessenger(), mManagerImpl);

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        BanubaSdkManager.setUp(binding.getBinaryMessenger(), null);
    }
}
