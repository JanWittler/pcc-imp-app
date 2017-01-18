package de.pcc.privacycrashcam.applicationlogic.camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

/**
 * Decorates the CompatCameraHandler so that it triggers recording on its own after recognizing a
 * button click or after measured acceleration force exceeds the set maximum.
 */
public class TriggeringCompatCameraHandler extends CompatCameraHandler implements
        SensorEventListener, View.OnClickListener {
    private final static String TAG = "TRG_CMP_CAM_HANDLER";
    private final static float MAX_G_FORCE = 3.0f;

    private Sensor accelSensor;
    private SensorManager sensorManager;

    /**
     * Creates a new camera handler with the passed parameters and sets up callbacks, camera, media
     * recorder and buffer. Also sets up the sensor.
     *
     * @param context        application context
     * @param previewView    view to be used as preview for the camera
     * @param recordCallback callback to be notified about recording state
     */
    public TriggeringCompatCameraHandler(Context context, SurfaceView previewView,
                                         RecordCallback recordCallback) {
        super(context, previewView, recordCallback);

        // set up sensor
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor acceleration sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "Click received");
    }
}
