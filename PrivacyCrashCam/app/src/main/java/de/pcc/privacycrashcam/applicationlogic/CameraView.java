package de.pcc.privacycrashcam.applicationlogic;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import de.pcc.privacycrashcam.applicationlogic.camera.CameraHandler;

/**
 * Surface view optimized to be used as camera preview.
 *
 * @author Giorgio Gross
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private final static String TAG = "CAM_VIEW";
    private SurfaceHolder mHolder;
    private CameraHandler cameraHandler;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera handler about it
        cameraHandler.resumeHandler();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // pause camera handler before making changes
        cameraHandler.pauseHandler();

        // ... do changes to surface here.
        // this will be necessary if we support landscape and portrait mode views

        // resume camera handler. This will invalidate the camera.
        cameraHandler.resumeHandler();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "Received preview frame");

    }

    public void setCameraHandler(CameraHandler cameraHandler) {
        this.cameraHandler = cameraHandler;
    }
}
