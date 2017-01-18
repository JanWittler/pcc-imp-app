package de.pcc.privacycrashcam.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.applicationlogic.CameraView;
import de.pcc.privacycrashcam.applicationlogic.camera.CameraHandler;
import de.pcc.privacycrashcam.applicationlogic.camera.RecordCallback;
import de.pcc.privacycrashcam.applicationlogic.camera.TriggeringCompatCameraHandler;

public class CameraActivity extends MainActivity {
    private final static String TAG = "CAM_ACTIVITY";

    private ImageView statusSymbol;
    private CameraView cameraPreview;
    private CameraHandler mCamHandler;

    private RecordCallback recordCallback;


    /**
     * Get the base layout resource for this activity. The layout must contain a toolbar with an id
     * named <b>toolbar</b>.
     * <p>If there is a navigation drawer in the layout it will be displayed. Navigation is handled
     * by the {@link MainActivity MainActivity} class.</p>
     *
     * @return resource id for this activity
     */
    @Override
    public int getLayoutRes() {
        return R.layout.activity_main_drawer_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = (FrameLayout) findViewById(R.id.content_container);
        getLayoutInflater().inflate(R.layout.content_camera, container, true);

        recordCallback = new RecordCallback() {
            @Override
            public void onRecordingStarted() {
                // hide "ready" icon

                // show "recorder" icon

            }

            @Override
            public void onRecordingStopped() {
                // hide "recorder" icon

                // show "ready" icon

            }
        };

        cameraPreview = (CameraView) findViewById(R.id.sv_camera);
        mCamHandler = new TriggeringCompatCameraHandler(getApplicationContext(),
                cameraPreview, recordCallback);
        cameraPreview.setOnClickListener((View.OnClickListener) mCamHandler);
        cameraPreview.setCameraHandler(mCamHandler);
    }

    @Override
    protected void onResume() {
        super.onPostResume();
        cameraPreview.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
