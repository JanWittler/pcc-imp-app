package de.pcc.privacycrashcam.applicationlogic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.pcc.privacycrashcam.R;

/**
 * Shows the settings view along with its view components and handles user input.
 *
 * @author Giorgio
 */
public class SettingsFragment extends Fragment {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private RelativeLayout base;

    TextView qualityLow;
    TextView qualityMedium;
    TextView qualityHigh;
    TextView fps;
    TextView bufferSize;
    SeekBar fpsBar;
    Button incBuffer;
    Button decBuffer;
    Button logOut;

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // get the main layout describing the content
        base = (RelativeLayout) inflater.inflate(R.layout.content_settings, container, false);

        // init view components

        return base;
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
    }
}
