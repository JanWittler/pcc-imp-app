package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.CamcorderProfile;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
 * {@link #deleteTempData()} in order to delete that directory as soon as they stop using this
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
    private static final String TEMP_DIR_PREFIX = "temp_";
    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private Context context;
    private String tempDirName = "temp_0"; // default dir name
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

                                                                                  XXXXXX
                                                                                XX      XX
                                                                               XX        XX
                                                                               XX        XX
                                                                                 XXXXXXXXX
                                    methods                                    XXXXXXXXXXXXX
                                    User Data saved in Shared Preferences    XXX           XXX
                                                                            XX               XX
                                                                            X   X         X   X
                                                                            X   X         X   X

    ############################################################################################# */

    /**
     * Gets saved user settings
     *
     * @return settings of user
     */
    public Settings getSettings() {
        Settings settings;
        try {
            settings = new Settings(appPreferences.getString(Settings.SETTINGS_MAIN_KEY,
                    Settings.JSON_DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
            settings = new Settings(Settings.FPS_DEFAULT, Settings.BUFFER_SIZE_SEC_DEFAULT,
                    Settings.QUALITY_DEFAULT);
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

                                  methods
                                  Saving, creating and loading files

    ############################################################################################# */

    /**
     * Creates and returns a temporary file which can be used to save video content. This file is
     * located in the local directory of the app and cannot be accessed from outside.
     * <p>
     * <p>
     * As video files tend to become pretty large you are responsible for deleting the file.
     * Alternatively, you can call {@link #deleteTempData()} to delete all files saved in the local
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

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + Video.PREFIX + timeStamp
                + "." + Video.SUFFIX);
    }

    /**
     * Creates and returns a temporary file which can be used to save metadata content. This file is
     * located in the local directory of the app and cannot be accessed from outside.
     * <p>
     * <p>
     * Yu are responsible for deleting the file.
     * Alternatively, you can call {@link #deleteTempData()} to delete all files saved in the local
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

    @Nullable
    private File createTempDir() {
        // todo this creates a folder in external memory. change this at the end to create the folder on the internal starage. see context.getFilesDir()

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), tempDirName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * Create new directory name to be used as temp directory. The directory will be created when
     * you call {@link #getTempMetadataFile()} or {@link #getTempVideoFile()} next time. You cannot
     * access the old directory anymore after calling this method, calls to
     * {@link #deleteTempData()} will only affect the new temp directory.
     */
    public void rebaseTempDir() {
        tempDirName = TEMP_DIR_PREFIX + System.currentTimeMillis();
    }

    /**
     * Deletes all temporary data located inside the {@link MemoryManager} instance's temp folder.
     */
    public void deleteTempData() {
        File dir = createTempDir();
        if(dir == null) return;
        for (File file : dir.listFiles()) file.delete();
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

        // stub to test other classes. Replace the following line..
        return CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
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

        // stub to test other classes. Replace the following line..
        return CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
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
        return null;
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
