package com.banuba.sdk.flutter.banuba_sdk;

import static com.banuba.sdk.flutter.BanubaSdkPluginGen.BanubaSdkManager;
import static com.banuba.sdk.flutter.BanubaSdkPluginImpl.BanubaSdkManagerIml;

import androidx.annotation.NonNull;

import com.banuba.sdk.flutter.EffectPlayerView.NativeViewFactory;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/**
 * BanubaSdkPlugin
 */
public class BanubaSdkPlugin implements FlutterPlugin, ActivityAware {
    private BanubaSdkManagerIml mManagerImpl;
    private ActivityPluginBinding mActivityBinding;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(
            "effect_player_view", new NativeViewFactory()
        );
        mManagerImpl = new BanubaSdkManagerIml(flutterPluginBinding.getApplicationContext());
        BanubaSdkManager.setup(flutterPluginBinding.getBinaryMessenger(), mManagerImpl);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        BanubaSdkManager.setup(binding.getBinaryMessenger(), null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mManagerImpl.setActivity(binding.getActivity());
        mActivityBinding = binding;
    }

    @Override
    public void onDetachedFromActivity() {
        mManagerImpl.setActivity(null);
        mActivityBinding = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }
}
