package de.pcc.privacycrashcam.applicationlogic.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.pcc.privacycrashcam.data.Video;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;

/**
 * A media recorder pool to preload media recorders
 *
 * @author Giorgio Gross
 */
public class MediaRecorderPool {
    private static final String TAG = MediaRecorderPool.class.toString();
    private final MemoryManager memoryManager;
    private Camera camera;
    private CamcorderProfile camcorderProfile = null;
    private MediaRecorder.OnInfoListener listener;

    private ArrayList<StorableMediaRecorder> mediaRecorders;
    private int activeRecorder = 0;
    private int poolSize;

    private boolean isPoolPrepared = false;
    private RecorderLoader loader;

    public MediaRecorderPool(int poolSize, Camera camera, MediaRecorder.OnInfoListener listener,
                             CamcorderProfile camcorderProfile, MemoryManager memoryManager) {
        if (poolSize <= 0) throw new IllegalArgumentException("Pool size must be higher than 0");

        this.camera = camera;
        this.camcorderProfile = camcorderProfile;
        this.memoryManager = memoryManager;
        this.listener = listener;

        this.poolSize = poolSize;
    }

    /**
     * Gets a ready to use media recorder from the pool. This operation may block if the media
     * recorder is not prepared yet.
     *
     * @return media recorder
     */
    public StorableMediaRecorder obtainActiveMediaRecorder() {
        StorableMediaRecorder recorder = mediaRecorders.get(activeRecorder);

        // spin until recorder is ready
        //noinspection StatementWithEmptyBody
        while (!recorder.isPrepared()) ;

        activeRecorder = (++activeRecorder) % mediaRecorders.size();
        return recorder;
    }

    /**
     * Resets and prepares the passed media recorder asynchronously
     *
     * @param mediaRecorder
     */
    public void suspendMediaRecorder(StorableMediaRecorder mediaRecorder) {
        mediaRecorder.setPrepared(false);
        mediaRecorder.reset();
        loader = new RecorderLoader();
        loader.execute(mediaRecorder);
    }

    /**
     * Prepares all media recorders. This will happen synchronously, possibly blocking the UI.
     *
     * @return false, if an error occurs
     */
    public boolean preparePool() {
        if (isPoolPrepared) return true;

        if (mediaRecorders == null) {
            mediaRecorders = new ArrayList<>();
            for (int i = 0; i < poolSize; i++) {
                StorableMediaRecorder mediaRecorder = prepareMediaRecorder();
                if (mediaRecorder == null) return false;
                mediaRecorders.add(mediaRecorder);
            }
        }

        isPoolPrepared = true;
        return true;
    }

    /**
     * Releases all media recorders. If you want to reuse the pool you will need to call
     * {@link MediaRecorderPool#preparePool()} again.
     */
    public void dropPool() {
        for (MediaRecorder recorder : mediaRecorders) {
            recorder.reset();
            recorder.release();
        }
        mediaRecorders = null;

        isPoolPrepared = false;

        loader.cancel(true);
    }

    /**
     * Sets up the existing media recorder with respect to the user's settings
     */
    private StorableMediaRecorder prepareMediaRecorder(StorableMediaRecorder mediaRecorder) {
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(camcorderProfile);

        // get new file and add it to buffer and media recorder
        File currentOutputFile = memoryManager.getTempVideoFile();
        if (currentOutputFile == null) return null;
        mediaRecorder.setCurrentOutputFile(currentOutputFile);

        mediaRecorder.setMaxDuration(Video.VIDEO_CHUNK_LENGTH * 1000);
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setOnInfoListener(listener);

        try {
            // this can be put in an async task if performance turns out to be poor
            mediaRecorder.prepare();
            Log.d(TAG, "Media Recorder is prepared " + mediaRecorder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
        mediaRecorder.setPrepared(true);
        return mediaRecorder;
    }

    /**
     * Sets up a new media recorder with respect to the user's settings
     */
    private StorableMediaRecorder prepareMediaRecorder() {
        // MutedMediaRecorder(); is not suitable for some devices
        StorableMediaRecorder mediaRecorder = new StorableMediaRecorder();
        return prepareMediaRecorder(mediaRecorder);
    }

    private class RecorderLoader extends AsyncTask<StorableMediaRecorder, Void, StorableMediaRecorder> {
        @Override
        protected StorableMediaRecorder doInBackground(StorableMediaRecorder... params) {
            return prepareMediaRecorder(params[0]);
        }
    }

}
