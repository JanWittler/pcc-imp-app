package de.pcc.privacycrashcam.applicationlogic;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Video;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.data.serverconnection.RequestState;
import de.pcc.privacycrashcam.data.serverconnection.ServerProxy;
import de.pcc.privacycrashcam.data.serverconnection.ServerResponseCallback;

/**
 * Shows all videos which were recorded by the user.
 *
 * @author David Laubenstein, Giorgio Gross
 */

public class VideosFragment extends Fragment {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private RelativeLayout base;
    private ListView videosListView;
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
        // get the main layout describing the content and init view components
        base = (RelativeLayout) inflater.inflate(R.layout.content_videos, container, false);
        videosListView = (ListView) base.findViewById(R.id.lv_videos);

        // set up content
        MemoryManager memoryManager = new MemoryManager(getContext());
        videoListAdapter = new VideoListAdapter(memoryManager.getAllVideos(), memoryManager);
        videosListView.setAdapter(videoListAdapter);
        videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                videoListAdapter.info(position);
            }
        });
        return base;
    }

    protected class VideoViewHolder {
        ProgressBar progressUpload;
        ImageButton upload;
        ImageButton delete;
        TextView title;
        TextView caption;
    }

    private class VideoListAdapter extends BaseAdapter {
        private MemoryManager memoryManager;
        private LayoutInflater inflater;
        private ArrayList<Video> videos;

        private VideoListAdapter(ArrayList<Video> videos, MemoryManager memoryManager) {
            this.inflater = LayoutInflater.from(getContext());
            this.videos = videos;
            this.memoryManager = memoryManager;
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
        public View getView(final int position, View view, ViewGroup viewGroup) {
            final VideoViewHolder mHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_video, viewGroup, false);

                mHolder = new VideoViewHolder();
                mHolder.title = (TextView) view.findViewById(R.id.title);
                mHolder.caption = (TextView) view.findViewById(R.id.caption);
                mHolder.upload = (ImageButton) view.findViewById(R.id.upload);
                mHolder.progressUpload = (ProgressBar) view.findViewById(R.id.progress_upload);
                mHolder.delete = (ImageButton) view.findViewById(R.id.ib_delete);

                view.setTag(mHolder);
            } else {
                mHolder = (VideoViewHolder) view.getTag();
            }

            mHolder.title.setText(videos.get(position).getName());
            mHolder.caption.setText(String.format(getDate(videos.get(position).getReadableMetadata().getDate(), "dd.MM.yyyy hh:mm:ss")));

            mHolder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload(position, mHolder);
                }
            });
            mHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(position);
                }
            });

            return view;
        }

        private void toggleProgressBar(VideoViewHolder mHolder) {
            if(mHolder.progressUpload.getVisibility() == View.VISIBLE) {
                mHolder.progressUpload.setVisibility(View.GONE);
                mHolder.upload.setVisibility(View.VISIBLE);
            } else {
                mHolder.progressUpload.setVisibility(View.VISIBLE);
                mHolder.upload.setVisibility(View.GONE);
            }
        }

        // todo following methods might throw a NPE untill the memory manager is implemented or mocked properly..
        // todo javadoc

        /**
         * @param index
         */
        private void delete(int index) {
            Video item = videos.get(index);
            String videoTag = item.extractTagFromName(item.getName());
            memoryManager.deleteEncryptedVideoFile(videoTag);
            memoryManager.deleteEncryptedMetadataFile(videoTag);
            memoryManager.deleteReadableMetadata(videoTag);
            memoryManager.deleteEncryptedSymmetricKeyFile(videoTag);

            videos.remove(item);
            this.notifyDataSetChanged();
        }

        /**
         * @param index
         * @param mHolder
         */
        private void upload(int index, final VideoViewHolder mHolder) {
            toggleProgressBar(mHolder);

            Video item = videos.get(index);
            ServerProxy proxy = new ServerProxy(getContext());
            proxy.videoUpload(item.getEncVideoFile(), item.getEncMetaFile(),
                    item.getEncSymKeyFile(), memoryManager.getAccountData(),
                    new ServerResponseCallback<RequestState>() {
                        @Override
                        public void onResponse(RequestState response) {
                            Toast.makeText(getContext(), getString(R.string.video_upload_success),
                                    Toast.LENGTH_SHORT).show();
                            toggleProgressBar(mHolder);
                        }

                        @Override
                        public void onProgress(int percent) {
                            // ignored
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), getString(R.string.error_no_connection),
                                    Toast.LENGTH_SHORT).show();
                            toggleProgressBar(mHolder);
                        }
                    });
        }

        /**
         * @param index
         */
        private void info(int index) {
            // set up the metadata content so that a user can read it easily
            Metadata videoMeta = videos.get(index).getReadableMetadata();
            String unformatted = getContext().getString(R.string.meta_info);
            // todo format date according to localization   and print g force as a vector (maybe add a method to Metadata.java for this)
            String formatted = String.format(unformatted, getDate(videoMeta.getDate(), "dd.MM.yyyy hh:mm:ss"),
                    videoMeta.getTriggerType(), videoMeta.getgForce().toString());

            // show a dialog
            new HTMLDialogViewer(getContext(), inflater, getContext().getResources().getString(R.string.meta_info_title), formatted).showDialog();
        }

        /**
         * Return date in specified format.
         * @param milliSeconds Date in milliseconds
         * @param dateFormat Date format
         * @return String representing date in specified format
         */
        public String getDate(long milliSeconds, String dateFormat)
        {
            // Create a DateFormatter object for displaying date in specified format.
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }
    }
}