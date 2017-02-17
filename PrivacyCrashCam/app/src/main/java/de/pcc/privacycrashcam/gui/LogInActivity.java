package de.pcc.privacycrashcam.gui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.applicationlogic.LogInFragment;
import de.pcc.privacycrashcam.applicationlogic.LogInHelper;
import de.pcc.privacycrashcam.applicationlogic.WelcomeFragment;

/**
 * Log In Activity which handles requesting permission, authenticating the user or starting the
 * {@link CameraActivity}.
 *
 * @author Giorgio Armani or better known as the one and only G.I.Giorgio, David Laubenstein
 */

public class LogInActivity extends MainActivity {
    private final static String TAG = LogInActivity.class.getName();
    private static final int PERMISSIONS_REQUEST = 4711;

    /**
     * Starts a new intent with the {@link CameraActivity CameraActivity}
     *
     * @param calling the activity which is doing this call
     */
    public static void Launch(Activity calling){
        Intent intent = new Intent(calling, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        calling.startActivity(intent);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main_nodrawer_notoolbar;
    }

    @Override
    public int getMenuEntryId() {
        return 0; // no menu entry
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageUiFlow();
    }

    private void manageUiFlow() {
        if (!hasPermissions()) {
            replaceFragment(new WelcomeFragment());
        } else if (!LogInHelper.IsUserLoggedIn(getApplicationContext())) {
            replaceFragment(new LogInFragment());
        } else {
            CameraActivity.Launch(this);
            finish();
        }
    }

    public void grantPermissions(View v) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                manageUiFlow();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make no call to super. This prevents a bug in Android..
    }

    /**
     * Checks if we have permission to access cam, mic and storage by both checking the API and making a demo
     * request and checking its results.
     *
     * @return true if we have all permissions
     */
    private boolean hasPermissions() {
        int cam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return cam == PackageManager.PERMISSION_GRANTED
                && audio == PackageManager.PERMISSION_GRANTED
                && storage == PackageManager.PERMISSION_GRANTED;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.commit();
    }
}
