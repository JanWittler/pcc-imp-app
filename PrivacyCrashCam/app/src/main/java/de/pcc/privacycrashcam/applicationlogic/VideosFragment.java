package de.pcc.privacycrashcam.applicationlogic;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.data.Video;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/27/17.
 */

public class VideosFragment extends Fragment {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private RelativeLayout base;

    private ArrayList<Video> videosList;
    private ListView videos;

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
        base = (RelativeLayout) inflater.inflate(R.layout.content_videos, container, false);

        // init view components
        return base;
    }

    /**
     * @param index
     */
    private void delete(int index) {

    }

    /**
     * @param index
     */
    private void upload(int index) {

    }

    /**
     * @param index
     */
    private void info(int index) {

    }
}
