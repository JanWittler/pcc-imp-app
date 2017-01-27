package de.pcc.privacycrashcam.applicationlogic;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import de.pcc.privacycrashcam.R;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/27/17.
 */

public class VideosFragment extends Fragment {
    private RelativeLayout base;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       // get the main layout describing the content
        base = (RelativeLayout) inflater.inflate(R.layout.content_videos, container, false);

        // init view components
        return base;
    }
}
