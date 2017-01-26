package de.pcc.privacycrashcam.applicationlogic.camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.utils.dataprocessing.AsyncPersistor;
import de.pcc.privacycrashcam.utils.dataprocessing.PersistCallback;
import de.pcc.privacycrashcam.utils.datastructures.FileRingBuffer;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Camera handler which uses the old Camera API. Handles access to the camera, displaying the
 * preview and writing the buffer. Also manages persisting the buffer's content.
 */
@SuppressWarnings("deprecation")
public class CompatCameraHandler implements CameraHandler, MediaRecorder.OnInfoListener {
    private final static String TAG = "CMP_CAM_HANDLER";
    private final static int VIDEO_CHUNK_LENGTH = 5; // length of video chunks in seconds

    private Camera camera = null;
    private Camera.Parameters cameraParameters = null;
    private CamcorderProfile camcorderProfile = null;
    private MediaRecorder mediaRecorder = null;
    private boolean isHandlerRunning = false;

    private Context context;
    private SurfaceView previewView;

    private Settings settings;
    private Metadata metadata;
    private MemoryManager memoryManager;

    private RecordCallback recordCallback;
    private File currentOutputFile;
    private AsyncPersistor mPersistor;
    private FileRingBuffer fileRingBuffer;

    private PersistCallback persistCallback;

    /**
     * Creates a new camera handler with the passed parameters and sets up callbacks, camera, media
     * recorder and buffer.
     *
     * @param context        application context
     * @param previewView    view to be used as preview for the camera
     * @param recordCallback callback to be notified about recording state
     */
    public CompatCameraHandler(Context context, SurfaceView previewView,
                               RecordCallback recordCallback) {
        this.context = context;
        this.previewView = previewView;
        this.recordCallback = recordCallback;

        // get notified about state changes during persisting a video
        this.persistCallback = new PersistCallback() {
            @Override
            public void onPersistingStarted() {
                // use new ring buffer to avoid conflicts
                setUpBuffer();

                // update UI
                CompatCameraHandler.this.recordCallback.onRecordingStopped();
            }

            @Override
            public void onPersistingStopped() {
                // ignored
            }
        };

        // Load and apply settings
        this.memoryManager = new MemoryManager(context);
        this.settings = memoryManager.getSettings();
        setUpBuffer();
        setUpCamcorderProfile();

        // avoid NPE's if a client forgets to set the metadata
        //this.metadata = new Metadata();
    }

    private void setUpBuffer() {
        int bufferCapacity = settings.getBufferSizeSec() / VIDEO_CHUNK_LENGTH;
        this.fileRingBuffer = new FileRingBuffer(bufferCapacity);
    }

    /**
     * Sets all presets and settings applying to the camcorder profile. Camcorder profile needs to
     * be set up only once and can be reused later.
     */
    private void setUpCamcorderProfile() {
        camcorderProfile = CamcorderProfile.get(settings.getQuality());

        // Set camcorder profile's video width, height, fps which will be applied to the
        // mediaRecorder by MediaRecorder.setProfile(..);
        camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        camcorderProfile.videoFrameRate = settings.getFps();
    }

    /**
     * Sets up the camera with respect to the user's settings
     */
    private boolean prepareCamera() {
        Log.d(TAG, "preparing camera");
        if (!CameraHelper.hasCameraHardware(context)) {
            recordCallback.onError(context.getResources().getString(R.string.error_no_camera));
            return false;
        }

        Log.d(TAG, "cam");
        // set up camera
        camera = getCameraInstance();
        Log.d(TAG, "cam instance" + camera);
        if (camera == null) {
            // camera was not available
            recordCallback.onError(context.getResources().
                    getString(R.string.error_camera_unavailable));
            return false;
        }

        // set up camera parameters
        cameraParameters = camera.getParameters();
        // choose best video preview size
        List<Camera.Size> mSupportedPreviewSizes = cameraParameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = cameraParameters.getSupportedVideoSizes();

        // pay attention to screen orientation
        Camera.CameraInfo info = CameraHelper.getDefaultBackFacingCameraInfo();
        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        Camera.Size optimalSize;
        int deviceAngle = 0;
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                        mSupportedPreviewSizes, previewView.getWidth(), previewView.getHeight());
                cameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
                deviceAngle = 0;
                break;
            case Surface.ROTATION_90:
                optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                        mSupportedPreviewSizes, previewView.getHeight(), previewView.getWidth());
                cameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
                deviceAngle = 90;
                break;
            case Surface.ROTATION_180:
                optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                        mSupportedPreviewSizes, previewView.getWidth(), previewView.getHeight());
                cameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
                deviceAngle = 180;
                break;
            case Surface.ROTATION_270:
                optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                        mSupportedPreviewSizes, previewView.getHeight(), previewView.getWidth());
                cameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
                deviceAngle = 270;
                break;
        }
        camera.setDisplayOrientation((info.orientation - deviceAngle + 360) % 360);
        camera.setParameters(cameraParameters);

        try {
            camera.setPreviewDisplay(previewView.getHolder());
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            e.printStackTrace();
            // Do not send error message to UI.
            // We might be experiencing a bug if we reach this part.
            return false;
        }
        return true;
    }

    /**
     * Get an instance of the Camera object.
     *
     * @return the camera or null if the camera is unavailable
     */
    private
    @Nullable
    Camera getCameraInstance() {
        if (camera != null) return camera;
        // returns null if camera is unavailable
        return CameraHelper.getDefaultBackFacingCameraInstance();
    }

    /**
     * Stops the preview and releases the camera so that other applications can use it.
     */
    private void releaseCamera() {
        Log.d(TAG, "releasing camera");
        if (camera == null) return;

        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /**
     * Sets up the media recorder with respect to the user's settings
     */
    private boolean prepareMediaRecorder() {
        mediaRecorder = new MutedMediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        Log.d(TAG, "set sources");
        mediaRecorder.setProfile(camcorderProfile);
        Log.d(TAG, "set profile");
        currentOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO); // = memoryManager.getTempVideoFile();
        Log.d(TAG, "set file");
        mediaRecorder.setOutputFile(currentOutputFile.getPath());
        mediaRecorder.setMaxDuration(VIDEO_CHUNK_LENGTH * 1000);
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setOnInfoListener(this);

        Log.d(TAG, "Settings set up");
        try {
            // maybe put this in an async task if performance turns out to be poor
            mediaRecorder.prepare();
            Log.d(TAG, "prepared media recorder");
        } catch (IOException e) {
            e.printStackTrace();
            recordCallback.onError(context.getResources().getString(R.string.error_recorder));
            return false;
        }
        return true;
    }

    /**
     * Releases the media recorder and locks the camera
     */
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            // Clear recorder configuration.
            mediaRecorder.reset();
            // Release the recorder object
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (camera != null) {
            Log.d(TAG, "LOCKING CAMERA");
            camera.lock();
        }
    }

    /**
     * Starts recording and writing into buffer
     */
    private void startRecordingChunk() {
        mediaRecorder.start();
    }

    /**
     * Stops recording and writing into buffer
     */
    private void stopRecordingChunk() throws IllegalStateException {
        if (mediaRecorder != null) mediaRecorder.stop();
    }

    @Override
    public void schedulePersisting() {
        // todo
        /*recordCallback.onRecordingStarted();

        // create async task to persist the buffer
        AsyncPersistor mPersistor = new AsyncPersistor(memoryManager, fileRingBuffer, persistCallback);
        mPersistor.execute(metadata);*/
    }


    @Override
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void resumeHandler() {
        if(isHandlerRunning) return;

        try {
            // take care of setting up camera, media recorder and recording
            if (!prepareCamera() || !prepareMediaRecorder()) {
                pauseHandler();
                return;
            }
        } catch (RuntimeException e) {
            // Unexpected exception was thrown. This will make sure that our app doesn't bother
            // other apps if it crashes as we ensure by this routine to release the camera on crash
            e.printStackTrace();
            recordCallback.onError(context.getResources().getString((R.string.error_undefined)));
            pauseHandler();
            return;
        }

        // all set ups were successful. Start recording and buffering
        startRecordingChunk();
        isHandlerRunning = true;
    }

    @Override
    public void pauseHandler() {
        if(!isHandlerRunning) return;

        isHandlerRunning = false;
        // take care of stopping preview and recording
        try {
            stopRecordingChunk();
            fileRingBuffer.put(currentOutputFile);
        } catch (RuntimeException re) {
            // No valid data was recorded as MediaRecorder.stop() was called before or right after
            // MediaRecorder.start(). Delete the incomplete file; a new one will be allocated as
            // soon as the Handler is resumed
            currentOutputFile.delete();
            re.printStackTrace();
        }
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        // Video is saved automatically, no need to call stopRecordingChunk() here..

        // Insert output file into ring buffer
        fileRingBuffer.put(currentOutputFile);

        if (!isHandlerRunning) return;
        // todo ensure that this section will never be called after pauseHandler was called. onInfo will be called asynchronously...
        releaseMediaRecorder();
        // start recording new chunk
        prepareMediaRecorder(); // will allocate also a new output file
        startRecordingChunk();
    }
}
