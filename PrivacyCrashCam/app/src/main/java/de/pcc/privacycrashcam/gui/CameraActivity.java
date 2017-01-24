package de.pcc.privacycrashcam.gui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

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
    private View decorView;

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
        return R.layout.activity_main_drawer_transparenttoolbar;
    }

    @Override
    public int getMenuEntryId() {
        return R.id.nav_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decorView = getWindow().getDecorView();
        // make activity fullscreen and remove system bar shadows
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

        cameraPreview = (CameraView) findViewById(R.id.sv_camera);
        mCamHandler = new TriggeringCompatCameraHandler(getApplicationContext(),
                cameraPreview, recordCallback);
        cameraPreview.setOnClickListener((View.OnClickListener) mCamHandler);
        cameraPreview.setCameraHandler(mCamHandler);

        // don't show title
        setTitle("");
        // remove drawer shadow
        removeDrawerShadow();
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

    /**
     * Set Flags to hide system bars
     */
    private void hideSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // enter immersive mode
    }

    /**
     * Removes all the flags except for the ones that make the content appear under the system bars
     */
    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();

        // showing System Ui will happen after user the user swipes in from the edges of the screen
        // automatically..
    }
}
