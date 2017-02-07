package de.pcc.privacycrashcam.data.memoryaccess;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.pcc.privacycrashcam.applicationlogic.camera.CameraHelper;
import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.MemoryKeys;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.Video;

/**
 * Handles access to the device storage.
 * <p>
 * <p>User related data like settings and account are saved in shared preferences.</p>
 * <p>
 * <p>Videos, Metadata and Keys are organized in files. There are three directories on the external
 * storage for those files, respectively.
 * On the internal storage we have one directory for temporary data. This temporary directory is
 * created uniquely for each instance of this class. Callers must remember to call
 * {@link #deleteCurrentTempData()} in order to delete that directory as soon as they stop using this
 * class.</p>
 * <p>
 * <p>For file organisation, we use prefixes and tags:
 * <ul>
 * <li>A prefix indicates the type of file, like META_* or VIDEO_*</li>
 * <li>A tag marks a certain video and probably consists of the date the video was captured.
 * All  data related to that video will have the same tag appended after their prefix</li>
 * <p>
 * <li>Also note that files containing JSON Strings solely will have the suffix .json</li>
 * </ul>
 * </p>
 *
 * @author David Laubenstein, Giorgio Gross
 *         Created by Giorgio Gross
 */
public class MemoryManager {
    private static final String TAG = MemoryManager.class.getName();
    private static final String TEMP_PARENT_DIR_NAME = "temp";
    private static final String TEMP_DIR_PREFIX = "temp_";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private Context context;
    private String tempDirName = TEMP_DIR_PREFIX + "_0"; // default temp dir name
    private SharedPreferences appPreferences;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    public MemoryManager(Context context) {
        this.context = context;

        appPreferences = context.getSharedPreferences(MemoryKeys.APP_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

        tempDirName = TEMP_DIR_PREFIX + System.currentTimeMillis();
    }

    /* #############################################################################################

                                                                                  XXXXXXX
                                                                                XX       XX
                                                                               XX         XX
                                                                               XX         XX
                                                                                 XX     XX
                                    methods                                    XXXXXXXXXXXXX
                                    User Data saved in Shared Preferences    XXX           XXX
                                                                            XX               XX
                                                                            X   X         X   X
                                                                            X   X         X   X

    ############################################################################################# */

    /**
     * Gets saved user settings. Returns default settings if reading settings from shared
     * preferences failed.
     *
     * @return settings of user
     */
    public Settings getSettings() {
        Settings settings;
        try {
            settings = new Settings(appPreferences.getString(Settings.SETTINGS_MAIN_KEY, ""));
        } catch (JSONException e) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * Saves a Settings instance by overriding the previous Settings values in memory.
     *
     * @param settings the Settings instance to be saved
     */
    public void saveSettings(Settings settings) {
        SharedPreferences.Editor mAppPrefEditor = appPreferences.edit();
        mAppPrefEditor.putString(Settings.SETTINGS_MAIN_KEY, settings.getAsJSON());
        mAppPrefEditor.apply();
    }


    /**
     * delete all account data of a user
     */
    public void deleteAccount() {

    }

    /**
     * Saves an Account instance by overriding the previous Account values in memory.
     *
     * @param account the Settings instance to be saved
     */
    public void saveAccountData(Account account) {

    }

    /**
     * Gets saved user account
     *
     * @return account of user containing user data or null if none was found
     */
    public Account getAccountData() {
        return null;
    }

    /**
     * Deletes all account data of the user
     */
    public void deleteAccountData() {
    }


    /* #############################################################################################

                                                                                      +----+
                                                                                    +-|....|
                                  methods                                           | |....|
                                  Saving, creating and loading files                | |..  |
                                                                                    | +----+
                                                                                    +----+

    ############################################################################################# */

    /**
     * Creates and returns a temporary file which can be used to save video content. This file is
     * located in the local directory of the app and cannot be accessed from outside.
     * <p>
     * <p>
     * This file won't be accessible the next time the app is started.
     * You can call {@link #deleteCurrentTempData()} to delete all files saved in the local
     * temp directory.
     * The directory will be created uniquely for each instance of {@link MemoryManager}.
     * </p>
     *
     * @return file for video data or null if the file could not be created
     */
    @Nullable
    public File getTempVideoFile() {
        File mediaStorageDir = createTempDir();
        if (mediaStorageDir == null) return null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.GERMANY).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + Video.PREFIX + timeStamp
                + "." + Video.SUFFIX);
    }

    /**
     * Creates and returns a temporary file which can be used to save metadata content. This file is
     * located in the local directory of the app and cannot be accessed from outside.
     * <p>
     * <p>
     * This file won't be accessible the next time the app is started.
     * You can call {@link #deleteCurrentTempData()} to delete all files saved in the local
     * temp directory.
     * The directory will be created uniquely for each instance of {@link MemoryManager}.
     * </p>
     *
     * @return file for metadata or null if the file could not be created
     */
    @Nullable
    public File getTempMetadataFile() {
        File mediaStorageDir = createTempDir();
        if (mediaStorageDir == null) return null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + Metadata.PREFIX + timeStamp + ".mp4");
    }

    /**
     * Creates the temp parent directory if not existing and creates a directory to be used as temp
     * directory for this MemoryManager instance inside of the parent directory. The temp directory
     * will only be created if it does not already exist.
     *
     * @return directory to be used as temp directory for this MemoryManager instance or null
     */
    @Nullable
    private File createTempDir() {
        // todo this creates the temp directory in external memory. change this later to create the folder on the internal storage. see context.getFilesDir()

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        // Create the parent temp directory if it does not exist
        File tempParentDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), TEMP_PARENT_DIR_NAME);
        if (!tempParentDir.exists()) {
            if (!tempParentDir.mkdirs()) {
                Log.d(TAG, "failed to create parent directory");
                return null;
            }
        }

        // Create the temp directory if it does not exist
        File tempDirectory = new File(tempParentDir, tempDirName);
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                Log.d(TAG, "failed to create temp directory");
                return null;
            }
        }

        return tempDirectory;
    }

    /**
     * Create new directory name to be used as temp directory. The directory will be created when
     * you call {@link #getTempMetadataFile()} or {@link #getTempVideoFile()} next time. You cannot
     * access the old directory anymore after calling this method, calls to
     * {@link #deleteCurrentTempData()} will only affect the new temp directory.
     */
    public void rebaseTempDir() {
        tempDirName = TEMP_DIR_PREFIX + System.currentTimeMillis();
    }

    /**
     * Deletes all temporary data located inside the {@link MemoryManager} instance's temp folder.
     */
    public void deleteCurrentTempData() {
        File dir = createTempDir();
        if(dir == null) return;
        recDeleteDir(dir);
    }

    /**
     * Deletes all directories in internal memory inside the {@link #TEMP_PARENT_DIR_NAME}.
     */
    public void deleteAllTempData() {
        File tempParentDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), TEMP_PARENT_DIR_NAME); // todo see createTempDir for parent directory of temp parent directory.
        if (!tempParentDir.exists()) return;

        for (File file : tempParentDir.listFiles()){
            recDeleteDir(file);
        }
    }

    /**
     * Recursively delete directory and files inside directory
     * @param dir directory or file to be deleted
     */
    private void recDeleteDir(File dir) {
        if(dir.isDirectory()) {
            for (File file : dir.listFiles()){
                recDeleteDir(file);
            }
        }
        dir.delete();
    }

    /**
     * Adds a suiting prefix to the video name and uses that String to create a new file inside the
     * key folder on the external memory. The file may be used to write the encrypted symmetric
     * key.
     * <p>
     * <p>The file name will be key_<@param videoTag> where the tag resembles the ending of the file
     * name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createEncryptedSymmetricKeyFile(String videoTag) {
        // use Key.PREFIX as prefix! See Video class for guidance
        File keyDir = new File(context.getFilesDir() + File.separator + "keys");
        if (!keyDir.exists()) {
            if (!keyDir.mkdirs()) {
                Log.d(TAG, "failed to create key directory");
                return null;
            }
        }
        return new File(keyDir, "key_" + videoTag + ".key");
    }

    /**
     * Adds a suiting prefix to the video name and uses that String to create a new file inside the
     * video folder on the external memory. The file may be used to write the encrypted video data.
     * <p>
     * <p>The file name will be {@link Video#PREFIX VIDEO_}<@param videoTag> where the tag resembles
     * the ending of the file name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createEncryptedVideoFile(String videoTag) {
        // use Video.PREFIX as prefix!
        File videoDir = new File(context.getFilesDir() + File.separator + "videos");
        if(!videoDir.exists()) {
            if(!videoDir.mkdir()){
                Log.d(TAG, "failed to create key directory");
                return null;
            }
        }
        return new File(videoDir, "video_" + videoTag + ".avi");
    }

    /**
     * Adds a suiting prefix to the video name and uses that String to create a new file inside the
     * metadata folder on the external memory. The file may be used to write the encrypted metadata.
     * <p>
     * <p>
     * The file name will be {@link Metadata#PREFIX META_}<@param videoTag> where the tag
     * resembles the ending of the file name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createEncryptedMetaFile(String videoTag) {
        // use Metadata.PREFIX as prefix!

        // stub to test other classes. Replace the following line..
        return CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
    }

    /**
     * Adds a suiting prefix to the video name and appends "_readable". Uses that String to create
     * a new file inside the metadata folder on the external memory. The file may be used to write
     * the readable metadata.
     * <p>
     * <p>The file name will be {@link Metadata#PREFIX_READABLE META_READABLE_}<@param videoTag>
     * where the tag resembles the ending of the file name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createReadableMetadataFile(String videoTag) {
        // use Metadata.PREFIX_READABLE as prefix!

        // stub to test other classes. Replace the following line..
        return CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
    }

    /**
     * Deletes the file containing the encrypted symmetric key associated with the passed video
     * tag.
     * <p>
     * <p>The video tag is determined when creating the video file with
     * {@link #createEncryptedVideoFile(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     */
    public void deleteEncryptedSymmetricKeyFile(String videoTag) {
    }

    /**
     * Deletes the file containing the encrypted metadata associated with the passed video tag.
     * <p>
     * <p>The video tag is determined when creating the video file with
     * {@link #createEncryptedVideoFile(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     */
    public void deleteEncryptedMetadataFile(String videoTag) {

    }


    /**
     * Deletes the file containing the readable metadata associated with the passed video tag.
     * <p>
     * <p>The video tag is determined when creating the video file with
     * {@link #createEncryptedVideoFile(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     */
    public void deleteReadableMetadata(String videoTag) {

    }

    /**
     * Deletes the file containing the encrypted video associated with the passed video tag.
     * <p>
     * <p>If you know the video name but not the tag use {@link Video#extractTagFromName(String)}</p>
     *
     * @param videoTag Tag of the video
     */
    public void deleteEncryptedVideoFile(String videoTag) {

    }

    // todo add convenience method to delete all files associated with one video which will call deleteEncryptedSymmetricKeyFile, deleteEncryptedVideoFile, deleteEncryptedMetadataFile and deleteReadableMetadata


    /**
     * Creates and returns a list containing all encrypted videos saved in the video directory on
     * the external memory.
     * <p>
     * <p>See {@link Video}</p>
     *
     * @return Videos as an ArrayList<Video>
     */
    public ArrayList<Video> getAllVideos() {
        ArrayList<Video> allVideos= new ArrayList<>();
        Video video1 = new Video("video1", null, null, null);
        Video video2 = new Video("video1", null, null, null);
        Video video3 = new Video("video1", null, null, null);
        Video video4 = new Video("video1", null, null, null);
        allVideos.add(video1);
        allVideos.add(video2);
        allVideos.add(video3);
        allVideos.add(video4);
        return allVideos;
    }

    /**
     * Gets the file containing the encrypted symmetric key from the key directory located on the
     * external memory.
     *
     * @param videoTag Tag of the video the file is associated with
     * @return the encrypted symmetric key as a file or null if there is none
     */
    @Nullable
    public File getEncryptedSymmetricKey(String videoTag) {

        return null;
    }

    /**
     * Gets the file containing the encrypted video from the video directory located on the
     * external memory.
     * <p>
     * <p>If you know the video name but not the tag use {@link Video#extractTagFromName(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     * @return the encrypted video as a file or null if there is none
     */
    @Nullable
    public File getEncryptedVideo(String videoTag) {
        return null;
    }

    /**
     * Gets the file containing the encrypted metadata from the meta directory located on the
     * external memory.
     *
     * @param videoTag Tag of the video the file is associated with
     * @return the encrypted metadata as a file or null if there is none
     */
    @Nullable
    public File getEncryptedMetadata(String videoTag) {
        return null;
    }

    /**
     * Gets the file containing the readable metadata from the key directory located on the
     * external memory.
     *
     * @param videoTag Tag of the video the file is associated with
     * @return the readable metadata as a file or null if there is none
     */
    @Nullable
    public File getReadableMetadata(String videoTag) {
        return null;
    }

}
