package de.pcc.privacycrashcam.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.applicationlogic.CameraView;

public class CameraActivity extends MainActivity {

    private CameraView svCamera;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main_drawer_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = (FrameLayout) findViewById(R.id.content_container);
        View content = getLayoutInflater().inflate(R.layout.content_camera, container, true);
        // container.addView(content);

        svCamera = (CameraView) findViewById(R.id.sv_camera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        svCamera.releaseCamera();
    }

}
