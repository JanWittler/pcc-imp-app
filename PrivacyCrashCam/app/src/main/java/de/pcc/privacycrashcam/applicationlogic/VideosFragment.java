package de.pcc.privacycrashcam.applicationlogic;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.Inflater;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.applicationlogic.video.ViewHolder;
import de.pcc.privacycrashcam.data.Video;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;

import static android.R.attr.data;

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
    private VideoListAdapter videoListAdapter;


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
        videos = (ListView) base.findViewById(R.id.lv_videos);
        // get videosList from MemoryManager
        MemoryManager mM = new MemoryManager(getContext());
        videosList = mM.getAllVideos();
        // create private listadapter extends baseadapter
        videoListAdapter = new VideoListAdapter(videosList);
        videos.setAdapter(videoListAdapter);
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

    private class VideoListAdapter extends BaseAdapter {

        ArrayList<Video> videos;

        public VideoListAdapter(ArrayList<Video> videos) {
            this.videos = videos;
        }

        @Override
        public int getCount() {
            return videos.size();
        }

        @Override
        public Object getItem(int i) {
            return videos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View customView = layoutInflater.inflate(R.layout.list_item_video, viewGroup,false);

            //get elements of videos and store them into custom view
            TextView title = (TextView) customView.findViewById(R.id.title);
            TextView tv_caption = (TextView) customView.findViewById(R.id.tv_caption);
            ImageButton ib_upload = (ImageButton) customView.findViewById(R.id.ib_upload);
            ImageButton ib_delete = (ImageButton) customView.findViewById(R.id.ib_delete);

            title.setText(videos.get(position).getName());
            //tv_caption.setText(String.format(Long.toString(videosList.get(position).getReadableMetadata().getDate())));
            //TODO: listeners for ImageButtons, so that the methods delete info and meta will called
            return customView;
        }
    }
}