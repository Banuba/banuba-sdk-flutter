package com.banuba.sdk.flutter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.banuba.sdk.camera.Facing;
import com.banuba.sdk.effect_player.CameraOrientation;
import com.banuba.sdk.effect_player.Effect;
import com.banuba.sdk.effect_player.FrameDataListener;
import com.banuba.sdk.effect_player.LowLightListener;
import com.banuba.sdk.entity.ContentRatioParams;
import com.banuba.sdk.entity.RecordedVideoInfo;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.manager.BanubaSdkTouchListener;
import com.banuba.sdk.manager.IEventCallback;
import com.banuba.sdk.types.Data;
import com.banuba.sdk.types.FrameData;
import com.banuba.sdk.types.FullImageData;
import com.banuba.sdk.types.PixelFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public class BanubaSdkPluginImpl {
    private static final String TAG = "BanubaSdkPlugin";

    public static class BanubaSdkManagerIml implements BanubaSdkPluginGen.BanubaSdkManager {
        private static final String[] PERMISSIONS = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        private final Context mContext;
        private final Handler mMainHandler = new Handler(Looper.getMainLooper());
        private BanubaSdkPluginGen.FrameDataFlutterApi mFramesApi;
        private BanubaSdkManager mSdkManager;

        private BanubaSdkPluginGen.VoidResult mTakePhotoCallback;
        private BanubaSdkPluginGen.VoidResult mVideoRecordingCallback;
        private BanubaSdkPluginGen.VoidResult mEditedImageCallback;

        private File mPhotoFile;

        private File mEditedImageFile;

        private final ExecutorService mThreadPool = Executors.newFixedThreadPool(2);

        private final IEventCallback mCallback = new IEventCallback() {
            @Override
            public void onCameraOpenError(@NonNull Throwable throwable) {
                Log.w(TAG, "onCameraOpenError", throwable);
            }

            @Override
            public void onCameraStatus(boolean opened) {
                Log.w(TAG, "onCameraStatus = " + opened);
            }

            @Override
            public void onScreenshotReady(@NonNull Bitmap bitmap) {
                Log.d(TAG, "onScreenshotReady");
                final BanubaSdkPluginGen.VoidResult cleanup  = new BanubaSdkPluginGen.VoidResult() {
                    @Override
                    public void success() {
                        Log.d(TAG, "Clean up take photo input args");
                        mPhotoFile = null;
                        mTakePhotoCallback = null;
                    }

                    @Override
                    public void error(@NonNull Throwable error) {

                    }
                };
                saveImageAndNotify(bitmap, 100, mPhotoFile, mTakePhotoCallback, cleanup);
            }

            @Override
            public void onHQPhotoReady(@NonNull Bitmap bitmap) {
                Log.d(TAG, "onHQPhotoReady");
            }

            @Override
            public void onVideoRecordingFinished(@NonNull RecordedVideoInfo recordedVideoInfo) {
                Log.d(TAG, "onVideoRecordingFinished = " + recordedVideoInfo);
                try {
                    final File f = new File(recordedVideoInfo.getFilePath());
                    Log.d(TAG, "onVideoRecordingFinished: recorded file exists = " + f.exists());
                } catch (Exception e) {
                    Log.w(TAG, "onVideoRecordingFinished: unknown file state");
                }
                if (mVideoRecordingCallback != null) {
                    mVideoRecordingCallback.success();
                    mVideoRecordingCallback = null;
                }
            }

            @Override
            public void onVideoRecordingStatusChange(boolean status) {
                Log.d(TAG, "onVideoRecordingStatusChange = " + status);
            }

            @Override
            public void onImageProcessed(@NonNull Bitmap bitmap) {
                Log.d(TAG, "onImageProcessed");
            }

            @Override
            public void onFrameRendered(@NonNull Data data, int i, int i1) {
            }

            @Override
            public void onEditedImageReady(@NonNull Bitmap bitmap) {
                Log.d(TAG, "onEditedImageReady");
                try {
                    final BanubaSdkPluginGen.VoidResult cleanup  = new BanubaSdkPluginGen.VoidResult() {
                        @Override
                        public void success() {
                            Log.d(TAG, "Clean up save edited image input args");
                            mEditedImageFile = null;
                            mEditedImageCallback = null;
                        }

                        @Override
                        public void error(@NonNull Throwable error) {

                        }
                    };

                    saveImageAndNotify(bitmap, 100, mEditedImageFile, mEditedImageCallback, cleanup);
                } catch (Exception e) {
                    Log.w(TAG, "Cannot saved edited image!", e);
                    if (mEditedImageCallback != null) {
                        mEditedImageCallback.error(e);
                    }
                }
            }

            @Override
            public void onEditingModeFaceFound(boolean b) {
                IEventCallback.super.onEditingModeFaceFound(b);
            }

            @Override
            public void onTextureRendered(int i, int i1, int i2, long l, float[] floats) {
                IEventCallback.super.onTextureRendered(i, i1, i2, l, floats);
            }
        };

        private final FrameDataListener mFrameDataListener = new FrameDataListener() {
            @Override
            public void onFrameDataProcessed(FrameData frameData) {
                mFaceAttributes = frameData.getFaceAttributes();
                final Double lightCorrection = (mFaceAttributes != null) ? Double.valueOf(frameData.getLightCorrection()) : null;
                mLightSourceCorrection = lightCorrection != null ? String.valueOf(lightCorrection) : null;

                if (mFramesApi != null) {
                    final BanubaSdkPluginGen.FrameDataDto dto = new BanubaSdkPluginGen.FrameDataDto.Builder()
                            .setFaceAttributesJson(frameData.getFaceAttributes())
                            .setLightCorrection(lightCorrection)
                            .build();
                    mMainHandler.post(() -> mFramesApi.onFrame(dto, new BanubaSdkPluginGen.VoidResult() {
                        @Override
                        public void success() {}

                        @Override
                        public void error(@NonNull Throwable error) {
                            Log.w(TAG, "Failed to send onFrame via Pigeon", error);
                        }
                    }));
                }
            }
        };

        public BanubaSdkManagerIml(@NonNull Context context, @NonNull io.flutter.plugin.common.BinaryMessenger messenger) {
            mContext = context;
            mFramesApi = new BanubaSdkPluginGen.FrameDataFlutterApi(messenger);
        }

        public void setActivity(Activity activity) {
        }

        private String mFaceAttributes;

        @Override
        public void initialize(
                @NonNull List<String> resourcePath,
                @NonNull String clientTokenString,
                @NonNull BanubaSdkPluginGen.SeverityLevel logLevel
        ) {
            Log.d(TAG,"initialize");
            BanubaSdkManager.initialize(
                    mContext.getApplicationContext(),
                    clientTokenString,
                    resourcePath.toArray(new String[]{})
            );
        }

        @Override
        public void deinitialize() {
            Log.d(TAG, "deinitialize");
            getSdkManager().onSurfaceDestroyed();
            BanubaSdkManager.deinitialize();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void attachWidget(@NonNull Long banubaId) {
            Log.d(TAG, "attachWidget");
            SurfaceView effectPlayerView = EffectPlayerView.NativeViewFactory.getEffectPlayerView(banubaId.intValue());
            effectPlayerView.setOnTouchListener(
                    new BanubaSdkTouchListener(
                            mContext.getApplicationContext(),
                            getSdkManager().getEffectPlayer()
                    )
            );
            getSdkManager().attachSurface(effectPlayerView);

            getSdkManager().onSurfaceCreated();

            final int w = effectPlayerView.getWidth();
            final int h = effectPlayerView.getHeight();

            Log.d(TAG, "attachWidget: w = " + w + ", h = " + h);
            if (w == 0 && w == h) {
                Log.w(TAG, "Invalid surface view state!");
                return;
            }
            getSdkManager().onSurfaceChanged(0, effectPlayerView.getWidth(), effectPlayerView.getHeight());
        }

        @Override
        public void openCamera() {
            Log.d(TAG, "openCamera");
            if (allPermissionsGranted()) {
                getSdkManager().openCamera();
            } else {
                Log.w(TAG, "Cannot open camera. Required permissions not granted: "
                        + Arrays.toString(PERMISSIONS));
            }
        }

        @Override
        public void closeCamera() {
            Log.d(TAG, "closeCamera");
            getSdkManager().closeCamera();
        }

        @Override
        public void startPlayer() {
            Log.d(TAG, "startPlayer");
            getSdkManager().effectPlayerPlay();
        }

        @Override
        public void stopPlayer() {
            Log.d(TAG, "stopPlayer");
            getSdkManager().effectPlayerPause();
        }

        @Override
        public void loadEffect(@NonNull String path, @NonNull Boolean synchronously) {
            Log.d(TAG, "loadEffect = " + path + ", synchronously = " + synchronously);
            getSdkManager().loadEffect(path, synchronously);
        }

        @Override
        public void unloadEffect() {
            Log.d(TAG, "unloadEffect");
            getSdkManager().loadEffect("", true);
        }

        @Override
        public void evalJs(@NonNull String script) {
            Log.d(TAG, "evalJs = " + script);
            Effect current = getSdkManager().getEffectManager().current();
            if (current != null) {
                current.evalJs(script, null);
            }
        }

        @Override
        public void reloadConfig(@NonNull String script) {
            Log.d(TAG, "reloadConfig = " + script);
            getSdkManager().getEffectPlayer().effectManager().reloadConfig(script);
        }

        @Override
        public void startVideoRecording(
            @NonNull String filePath,
            @NonNull Boolean captureAudio,
            @NonNull Long width,
            @NonNull Long height,
            @NonNull Boolean frontCameraMirror // ignored for Android
        ) {
            Log.d(TAG, "startVideoRecording = " + filePath + "; audio = " + captureAudio
                    + ", w = " + width + "; h = " + height);
            getSdkManager().startVideoRecording(
                    filePath,
                    captureAudio,
                    new ContentRatioParams(width.intValue(), height.intValue(), false),
                    1f
            );
        }

        @Override
        public void stopVideoRecording(@NonNull BanubaSdkPluginGen.VoidResult result) {
            Log.d(TAG, "stopVideoRecording");
            mVideoRecordingCallback = result;
            getSdkManager().stopVideoRecording();
        }

        @Override
        public void pauseVideoRecording() {
            Log.d(TAG, "pauseVideoRecording");
            getSdkManager().pauseVideoRecording();
        }

        @Override
        public void resumeVideoRecording() {
            Log.d(TAG, "resumeVideoRecording");
            getSdkManager().unpauseVideoRecording();
        }

        @Override
        public void takePhoto(
            @NonNull String filePath,
            @NonNull Long width,
            @NonNull Long height,
            @NonNull BanubaSdkPluginGen.VoidResult result
        ) {
            Log.d(TAG, "takePhoto = " + filePath + "; w = " + width + "; h = " + height);
            mTakePhotoCallback = result;
            mPhotoFile = new File(filePath);
            getSdkManager().takePhoto(new ContentRatioParams(
                    width.intValue(), height.intValue(), false
            ));
        }

        private void saveImageAndNotify(
                final Bitmap bitmap,
                final int quality,
                final File file,
                final BanubaSdkPluginGen.VoidResult callback,
                final BanubaSdkPluginGen.VoidResult cleanup
        ) {
            if (file == null) {
                throw new IllegalArgumentException("File cannot be null");
            }

            if (bitmap == null) {
                throw new IllegalArgumentException("Bitmap cannot be null");
            }

            Log.d(TAG, "Saving image = " + file.getAbsolutePath() + "; q = " + quality);

            final Runnable task = () -> {
                final long start = System.currentTimeMillis();
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    final FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
                    fos.close();

                    // Flutter handles thread dispatching
                    if (callback != null) {
                        callback.success();
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Cannot save image to  = " + file.getAbsolutePath(), e);

                    // Flutter handles thread dispatching
                    if (callback != null) {
                        callback.error(e);
                    }
                } finally {
                    bitmap.recycle();
                    cleanup.success();
                }

                Log.d(TAG, "Time to save image: " + file.getAbsolutePath() + "; q = " + quality
                        + ", \n time = " + (System.currentTimeMillis() - start) + " ms");
            };

            mThreadPool.submit(task);
        }

        @Override
        public void setCameraFacing(Boolean front) {
            Log.d(TAG, "setCameraFacing front = " + front);
            if (front) {
                getSdkManager().setCameraFacing(Facing.FRONT, true);
            } else {
                getSdkManager().setCameraFacing(Facing.BACK, false);
            }
        }

        @Override
        public void processImage(
            @NonNull String sourceFilePath,
            @NonNull String destFilePath,
            @NonNull BanubaSdkPluginGen.VoidResult res
        ) {
            final File destFile = new File(destFilePath);

            Log.w(TAG, "DEPTECATED! processImage: dest file exists = " + new File(sourceFilePath).exists());

            final Callable<Bitmap> callable = () -> {
                final long start = System.currentTimeMillis();

                final Bitmap source = BitmapFactory.decodeFile(sourceFilePath);
                final FullImageData image = new FullImageData(source,
                        new FullImageData.Orientation(CameraOrientation.DEG_0));

                Data processed = null;
                try {
                    processed = getSdkManager().getEffectPlayer().processImage(
                            image,
                            PixelFormat.RGBA
                    );

                    final int width;
                    final int height;
                    final CameraOrientation cameraOrientation =
                            image.getOrientation().getCameraOrientation();
                    final Size size = image.getSize();

                    if (cameraOrientation == CameraOrientation.DEG_90
                            || cameraOrientation == CameraOrientation.DEG_270
                    ) {
                        width = size.getHeight();
                        height = size.getWidth();
                    } else {
                        width = size.getWidth();
                        height = size.getHeight();
                    }

                    final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    result.copyPixelsFromBuffer(processed.getData());

                    Log.d(TAG, "Time to process image = "
                            + (System.currentTimeMillis() - start) + " ms");
                    return result;

                } finally {
                    if (processed != null) {
                        processed.close();
                    }
                    processed = null;
                }
            };

            final RunnableFuture<Bitmap> future = new FutureTask<Bitmap>(callable);
            getSdkManager().runOnRenderThread(future);
            try {
                final Bitmap processed = future.get(30, TimeUnit.SECONDS);
                final BanubaSdkPluginGen.VoidResult cleanup = new BanubaSdkPluginGen.VoidResult() {
                    @Override
                    public void success() {
                        Log.d(TAG, "Clean up edited image input args");
                        mEditedImageFile = null;
                        mEditedImageCallback = null;
                    }

                    @Override
                    public void error(@NonNull Throwable error) {

                    }
                };

                saveImageAndNotify(processed, 100, destFile, res, cleanup);
            } catch (Exception e) {
                Log.w(TAG, "Cannot process image!", e);
                res.error(e);
            }
        }

        @Override
        public void startEditingImage(
            @NonNull String sourceImageFilePath,
            @NonNull BanubaSdkPluginGen.VoidResult result
        ) {
            Log.d(TAG, "startEditingImage = " + sourceImageFilePath);

            final Bitmap source = BitmapFactory.decodeFile(sourceImageFilePath);
            final FullImageData image = new FullImageData(source,
                    new FullImageData.Orientation(CameraOrientation.DEG_0));
            getSdkManager().startEditingImage(image);
            result.success();
        }

        @Override
        public void endEditingImage(
            @NonNull String destImageFilePath,
            @NonNull BanubaSdkPluginGen.VoidResult result
        ) {
            Log.d(TAG, "endEditingImage = " + destImageFilePath);
            mEditedImageFile = new File(destImageFilePath);
            mEditedImageCallback = result;
            getSdkManager().takeEditedImage();
        }

        @Override
        public void discardEditingImage() {
            Log.d(TAG, "discardEditingImage");
            getSdkManager().stopEditingImage();
        }

        @Override
        public void getFaceAttributes(@NonNull BanubaSdkPluginGen.NullableResult<String> result) {
            Log.d(TAG, "getFaceAttributes");
            result.success(mFaceAttributes);
        }

        @Override
        public void addFrameDataListener(@NonNull BanubaSdkPluginGen.VoidResult result) {
            Log.d(TAG, "addFrameDataListener");
            getSdkManager().getEffectPlayer().addFrameDataListener(mFrameDataListener);
            result.success();
        }

        @Override
        public void removeFrameDataListener(@NonNull BanubaSdkPluginGen.VoidResult result) {
            Log.d(TAG, "removeFrameDataListener");
            getSdkManager().getEffectPlayer().removeFrameDataListener(mFrameDataListener);
            result.success();
        }

        @Override
        public void setZoom(@NonNull Double zoom) {
            if (zoom == null) {
                Log.w(TAG, "Zoom value cannot be null");
                return;
            }

            Log.d(TAG, "setZoom = " + zoom);
            getSdkManager().setCameraZoom(zoom.floatValue());
        }

        @Override
        public void enableFlashlight(@NonNull Boolean enabled) {
            Log.d(TAG, "enableFlashlight = " + enabled);
            getSdkManager().setFlashlightEnabled(enabled);
        }

        private BanubaSdkManager getSdkManager() {
            if (mSdkManager == null) {
                mSdkManager = new BanubaSdkManager(mContext.getApplicationContext());
                mSdkManager.setCallback(mCallback);
            }
            return mSdkManager;
        }

        private boolean allPermissionsGranted() {
            if (Build.VERSION.SDK_INT >= 23) {
                for (String p : PERMISSIONS) {
                    if (mContext.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED
                            && !Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        }
    }
}
