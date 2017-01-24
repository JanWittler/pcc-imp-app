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
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
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
        // The Surface has been created. Wait for surfaceChanged to be called.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // pause camera handler before making changes
        cameraHandler.pauseHandler();

        // ... do changes to surface here.
        // this will be necessary if we support landscape and portrait mode views
        // requestLayout();

        // resume camera handler. This will invalidate the camera.
        cameraHandler.resumeHandler();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface has been destroyed. Pause the camera handler
        cameraHandler.pauseHandler();
    }

    public void setCameraHandler(CameraHandler cameraHandler) {
        this.cameraHandler = cameraHandler;
    }
}
