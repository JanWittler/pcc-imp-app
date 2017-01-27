package de.pcc.privacycrashcam.utils.dataprocessing;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.utils.datastructures.FileRingBuffer;
import de.pcc.privacycrashcam.utils.encryption.Encryptor;

/**
 * The AsyncPersistor saves all data after recording gets invoked in the app.
 * First it saves the metadata of the recording to a json file.
 * Then it takes all recorded video snippets and creates one coherent file.
 * After that all data gets encrypted and save to the app's data storage.
 * <p>
 *     The process of persisting is asynchronous to the app's main thread.
 *     Therefore callbacks are used to inform the app about the persisting's progress.
 * </p>
 *
 * @author Josh Romanowski, Giorgio Groß
 */

public class AsyncPersistor extends AsyncTask<Metadata, Void, Boolean> {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Tag used for logging.
     */
    private final static String TAG = AsyncPersistor.class.getName();

    /**
     * Manager used to access the app's memory e.g. saving the video data.
     */
    private MemoryManager memoryManager;
    /**
     * Callback used to inform the app about the progress of persisting.
     */
    private PersistCallback persistCallback;
    /**
     * Context of the persisting. Used to load resources.
     */
    private Context context;
    /**
     * Ringbuffer that contains the recorded video snippets
     */
    private FileRingBuffer ringbuffer;
    /**
     * Encryptor used to encrypt files and keys.
     */
    private Encryptor encryptor;
    /**
     * Settings used to determine the ringbuffer size.
     */
    private Settings settings;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Creates a new persistor with the given parameters.
     *
     * @param memoryManager   Manager for the app's memory access.
     * @param ringbuffer      Buffer containing the recorded video snippets.
     * @param persistCallback Callback used to give asynchronous response.
     * @param context         Android context of the recording.
     */
    public AsyncPersistor(MemoryManager memoryManager, FileRingBuffer ringbuffer,
                          PersistCallback persistCallback, Context context) {
        this.memoryManager = memoryManager;
        this.ringbuffer = ringbuffer;
        this.persistCallback = persistCallback;
        this.context = context;
        this.encryptor = new Encryptor();
        this.settings = memoryManager.getSettings();
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    protected Boolean doInBackground(Metadata... params) {

        // wait half a buffer size
        int timeToWait = settings.getBufferSizeSec() / 2;

        try {
            Thread.sleep(timeToWait * 1000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Interrupted while waiting");
            return false;
        }

        persistCallback.onPersistingStarted();

        // save metadata
        Metadata metaData = params[0];
        if (metaData == null) {
            Log.w(TAG, "Did not receive metadata");
            return false;
        }

        String videoName = String.valueOf(metaData.getDate());
        File metaLocation = memoryManager.saveReadableMetadata(videoName);
        if (!saveMetadataToFile(metaLocation, metaData))
            return false;

        // concat video snippets
        Queue<File> vidSnippets = ringbuffer.demandData();
        File concatVid = memoryManager.getTempVideoFile();
        if (!concatVideos(vidSnippets, concatVid))
            return false;

        // encrypt files
        if (!encrypt(videoName, concatVid, metaLocation))
            return false;

        // delete temporary files
        memoryManager.deleteTempData();
        ringbuffer.flushAll();

        persistCallback.onPersistingStopped();
        return true;
    }

    /* #############################################################################################
     *                                  helper methods
     * ###########################################################################################*/

    /**
     * Encrypts metadata and video with a hybrid encryption algorithm.
     * Saves the files on the app. Destination files will be created according to the
     * to MemoryManager.
     *
     * @param videoName   Video name of the video.
     * @param concatVideo Location of the video to encrypt.
     * @param meta        Location of the metadata to encrypt.
     * @return Returns whether encrypting was successful or not.
     */
    private boolean encrypt(String videoName, File concatVideo, File meta) {
        File[] input = new File[]{
                concatVideo,
                meta};
        File[] output = new File[]{
                memoryManager.createEncryptedVideoFile(videoName),
                memoryManager.createEncryptedMetaFile(videoName)};
        File encKey = memoryManager.createEncryptedSymmetricKeyFile(videoName);

        InputStream publicKey = context.getResources().openRawResource(R.raw.publickey);

        return encryptor.encrypt(input, output, publicKey, encKey);
    }

    /**
     * Takes a collection of videos and appends them in order.
     * Through that creates a continuous video and saves it to the desired location.
     *
     * @param videos      Collection of video snippets.
     * @param concatVideo Location of the merged video.
     * @return Returns whether concatting the videos was successful or not.
     */
    private boolean concatVideos(Queue<File> videos, File concatVideo) {
        // read all video snippets
        List<Movie> clips = new LinkedList<>();
        try {
            for (File video : videos) {
                Movie tm = MovieCreator.build(video.getAbsolutePath());
                clips.add(tm);
            }
        } catch (IOException e) {
            Log.w(TAG, "Error reading video snippets");
            return false;
        }

        // filter out video tracks and ignore audio tracks
        List<Track> videoTracks = new LinkedList<>();
        for (Movie m : clips) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        // create video
        Movie result = new Movie();
        try {
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }
            Container out = new DefaultMp4Builder().build(result);

            FileChannel fc = new RandomAccessFile(concatVideo, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (IOException e) {
            Log.w(TAG, "Error while writing concat video");
            return false;
        }
        return true;
    }

    /**
     * Takes a metadata object, parses it into json format and saves it to a file.
     *
     * @param output   Output location of the metadata file.
     * @param metadata Metadata to be saved.
     * @return Returns whether saving was successful or not.
     */
    private boolean saveMetadataToFile(File output, Metadata metadata) {
        String metaJson = metadata.getAsJSON();
        try (PrintWriter out = new PrintWriter(output)) {
            out.println(metaJson);
        } catch (IOException e) {
            Log.w(TAG, "Error when saving metadata to files");
            return false;
        }
        return true;
    }
}
