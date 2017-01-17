package de.pcc.privacycrashcam;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Giorgio on 08.12.16.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static String TAG = "CAM_VIEW";

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCamera = getCameraInstance();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    /** A safe way to get an instance of the Camera object. */
    private Camera getCameraInstance(){
        if(mCamera != null) return mCamera;

        // todo apply camera settings
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance

            Camera.Parameters p = c.getParameters();
            // p.setPreviewSize(240, 160);
            c.setParameters(p);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "error opening camera: ");
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            // todo make this cleaner
            mCamera = getCameraInstance();
            Log.d(TAG, "surface created, holder is "+holder +" camera is "+mCamera);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "Received preview frame");

    }

    public void acquireCamera() {
        mCamera = getCameraInstance();
    }

    public void releaseCamera() {
        if(mCamera == null) return;
        mCamera.release();
    }
}
