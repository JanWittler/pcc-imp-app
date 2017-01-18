package de.pcc.privacycrashcam.applicationlogic.camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.utils.dataprocessing.AsyncPersistor;
import de.pcc.privacycrashcam.utils.dataprocessing.PersistCallback;

/**
 * Camera handler which uses the old Camera API. Handles access to the camera, displaying the
 * preview and writing the buffer. Also manages persisting the buffer's content.
 */
public class CompatCameraHandler implements CameraHandler, MediaRecorder.OnInfoListener {
    private final static String TAG = "CMP_CAM_HANDLER";
    private final static int VIDEO_CHUNK_LENGTH = 5; // length of video chunks in seconds

    private Camera camera;
    private Camera.Parameters cameraParameters;
    private CamcorderProfile camcorderProfile;
    private MediaRecorder mediaRecorder;

    private Context context;
    private SurfaceView previewView;

    private Settings settings;
    private Metadata metadata;
    private MemoryManager memoryManager;

    private RecordCallback recordCallback;
    private File currentOutputFile;
    private AsyncPersistor mPersistor;
    private Queue<File> ringbuffer;

    private PersistCallback persistCallback;

    /**
     * Creates a new camera handler with the passed parameters and sets up callbacks, camera, media
     * recorder and buffer.
     *
     * @param context application context
     * @param previewView view to be used as preview for the camera
     * @param recordCallback callback to be notified about recording state
     */
    public CompatCameraHandler(Context context, SurfaceView previewView,
                               RecordCallback recordCallback) {
        this.context = context;
        this.previewView = previewView;
        this.recordCallback = recordCallback;

        // init persist callback
        this.persistCallback = new PersistCallback() {
            @Override
            public void onPersistingStarted() {
                CompatCameraHandler.this.recordCallback.onRecordingStopped();
            }

            @Override
            public void onPersistingStopped() {
                // ignored
            }
        };

        // todo get memory manager instance and get setting + do other init stuff
    }

    /**
     * Sets up the camera with respect to the user's settings
     */
    private boolean prepareCamera() {
        camera = getCameraInstance();
        if(camera == null) return false;

        // todo set params and settings

        Camera.Parameters p = camera.getParameters();
        // p.setPreviewSize(240, 160);
        camera.setParameters(p);

        try {
            camera.setPreviewDisplay(previewView.getHolder());
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance(){
        if(camera != null) return camera;

        Camera c = null;
        try {
            // attempt to get a Camera instance
            c = Camera.open(0);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "error opening camera: ");
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public void releaseCamera() {
        if(camera == null) return;
        camera.stopPreview();
        camera.release();
    }

    /**
     * Sets up the media recorder with respect to the user's settings
     */
    private boolean prepareMediaRecorder() {
        return false;
    }

    @Override
    public void schedulePersisting() {
        recordCallback.onRecordingStarted();

        // create async task to persist the buffer
        AsyncPersistor mPersistor = new AsyncPersistor(memoryManager, ringbuffer, persistCallback);
        mPersistor.execute(metadata);
    }

    @Override
    public void setMetadata(Metadata metadata) {

    }

    @Override
    public void resumeHandler() {
        // take care of setting up camera, media recorder and recording itself
        // prepareCamera();
        // prepareMediaRecorder();
    }

    @Override
    public void pauseHandler() {
        // take care of stopping preview and recording
        // releaseCamera();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

    }
}
