package com.banuba.sdk.flutter;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class EffectPlayerView implements PlatformView {
    public static class NativeViewFactory extends PlatformViewFactory {
        private static final HashMap<Integer, WeakReference<EffectPlayerView>> mKnownViews =
            new HashMap<>();

        public NativeViewFactory() {
            super(StandardMessageCodec.INSTANCE);
        }

        @NonNull
        @Override
        public PlatformView create(Context context, int viewId, @Nullable Object args) {
            @SuppressWarnings("unchecked")
            Map<String, Object> argsMap = (Map<String, Object>) args;
            EffectPlayerView view = new EffectPlayerView(context, viewId, argsMap);

            assert argsMap != null;
            Integer banubaId = (Integer) argsMap.get("banubaId");
            mKnownViews.put(banubaId, new WeakReference<>(view));

            return view;
        }

        public static SurfaceView getEffectPlayerView(int banubaId) {
            return requireNonNull(mKnownViews.get(banubaId)).get().getEffectPlayerView();
        }
    }

    private final SurfaceView mView;

    @SuppressWarnings("unused")
    EffectPlayerView(
        @NonNull Context context,
        int id,
        @Nullable Map<String, Object> creationParams
    ) {
        mView = new SurfaceView(context);
    }

    @Nullable
    @Override
    public View getView() {
        return mView;
    }

    SurfaceView getEffectPlayerView() {
        return mView;
    }

    @Override
    public void dispose() {
    }
}
