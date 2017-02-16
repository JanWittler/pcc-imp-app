package de.pcc.privacycrashcam.applicationlogic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.gui.LogInActivity;

/**
 * Shows the settings view along with its view components and handles user input.
 *
 * @author Giorgio Gross, David Laubenstein
 *         Created by Giorgio Gross at 01/20/2017
 */
public class SettingsFragment extends Fragment {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private RelativeLayout base;
    private MemoryManager memoryManager;
    private Settings settings;


    TextView fps;
    TextView bufferSize;
    SeekBar fpsBar;
    Button res_High;
    Button res_Med;
    Button res_Low;
    Button b_incBuffer;
    Button b_decBuffer;
    Button logOut;

    private final int BUFFER_CHUNK_SIZE = 5;

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

        memoryManager = new MemoryManager(getContext());
        settings = memoryManager.getSettings();
        // init view components

        /**
         * resolution handling
         */
        View.OnClickListener resHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_resHigh:
                        settings.setQuality(Settings.QUALITY_HIGH);
                        resetButtonColors();
                        res_High.setTextColor(
                                ContextCompat.getColor(getContext(), R.color.colorAccent));
                        break;
                    case R.id.tv_resMed:
                        settings.setQuality(Settings.QUALITY_MEDIUM);
                        resetButtonColors();
                        res_Med.setTextColor(
                                ContextCompat.getColor(getContext(), R.color.colorAccent));
                        break;
                    case R.id.tv_resLow:
                        settings.setQuality(Settings.QUALITY_LOW);
                        resetButtonColors();
                        res_Low.setTextColor(
                                ContextCompat.getColor(getContext(), R.color.colorAccent));
                        break;
                    default:
                        System.out.println("default");
                        break;
                }
            }
        };
        res_High = (Button) base.findViewById(R.id.tv_resHigh);
        res_High.setOnClickListener(resHandler);
        res_Med = (Button) base.findViewById(R.id.tv_resMed);
        res_Med.setOnClickListener(resHandler);
        res_Low = (Button) base.findViewById(R.id.tv_resLow);
        res_Low.setOnClickListener(resHandler);

        /**
         * frames handling
         */
        fpsBar = (SeekBar) base.findViewById(R.id.seekBar);
        fpsBar.setProgress(settings.getFps());
        fpsBar.requestLayout();
        fpsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // fps should be between 20 and 60
                // there is no min value for progress, so we have to set the value + the min value
                // and add this to the max value
                fps = (TextView) base.findViewById(R.id.tv_frames);
                int fps_Int = progress + 10;
                settings.setFps(fps_Int);
                fps.setText(Integer.toString(fps_Int));
            }
        });

        /**
         * buffer handling
         */

        View.OnClickListener bufferHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.b_incBuffer:
                        settings.setBufferSizeSec(settings.getBufferSizeSec() + BUFFER_CHUNK_SIZE);
                        bufferSize.setText(settings.getBufferSizeSec() + "sec");
                        break;
                    case R.id.b_decBuffer:
                        if ((settings.getBufferSizeSec() - BUFFER_CHUNK_SIZE) > 0) {
                            settings.setBufferSizeSec(settings.getBufferSizeSec() -
                                    BUFFER_CHUNK_SIZE);
                            bufferSize.setText(settings.getBufferSizeSec() + "sec");
                        } else {
                            Log.d("SettingsFragment", "Buffer size cannot set under 0!");
                        }
                        break;
                    default:
                        System.out.println("default");
                        break;
                }
            }
        };

        b_incBuffer = (Button) base.findViewById(R.id.b_incBuffer);
        b_incBuffer.setOnClickListener(bufferHandler);
        b_decBuffer = (Button) base.findViewById(R.id.b_decBuffer);
        b_decBuffer.setOnClickListener(bufferHandler);

        bufferSize = (TextView) base.findViewById(R.id.tv_bufferSize);
        bufferSize.setText(settings.getBufferSizeSec() + "sec");

        logOut = (Button) base.findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryManager.deleteAccountData();
                LogInActivity.Launch(getActivity());
            }
        });
        return base;
    }

    private void resetButtonColors() {
        res_High.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonColorDefault));
        res_Med.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonColorDefault));
        res_Low.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonColorDefault));
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        memoryManager.saveSettings(settings);
    }
}
