package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.CamcorderProfile;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.MemoryKeys;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.Video;

/**
 * Handles access to device storage.
 * @author David Laubenstein, Giorgio Gross
 * Created by Giorgio Gross
 */
public class MemoryManager {
    private Context context;

    private SharedPreferences appPreferences;

    public MemoryManager(Context context) {
        this.context = context;

        appPreferences = context.getSharedPreferences(MemoryKeys.APP_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    }

    /**
     * leave out singleton pattern as it leads to memory leaks
     */
    // private static MemoryManager instance;
    /*public MemoryManager GetInstance (Context context) {
        if (instance != null) return instance;

        instance = new MemoryManager(context);
        return instance;
    }*/

    /* #############################################################################################

                                  User Data saved in Shared Preferences

    ############################################################################################# */

    /**
     * Get saved user settings
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


    /* #############################################################################################

                                  Saving, creating and loading files

    ############################################################################################# */

    public File getTempVideoFile() {
        String fileName = "vid_" + System.currentTimeMillis();
        return new File(context.getFilesDir(), fileName);
    }

    /**
     * deletes all temporary data
     */
    public void deleteTempData() {

    }

    /**
     * returns the symmetric keyfile as a File
     * @return File symmetric key
     */
    public File getTempSymmetricKeyFile() {

        return null;
    }

    /**
     * saves the account data from parameter transfer
     * @param account is an Account object
     */
    public void saveAccountData(Account account) {

    }

    /**
     * Gibt eine Instanz der Klasse Account, in der sich die Accountdaten befin- den.
     * @return the Account object, which includes all account data
     */
    public Account getAccountData() {
        return null;
    }

    /**
     * delete all account data of a user
     */
    public void deleteAccount() {

    }

    /**
     * saves the encrypted symmetric key from a vodeo and returns the key as File
     * @param videoName
     * @param key
     */
    public File saveEncryptedSymmetricKey(String videoName, String key) {
       return null;
    }

    /**
     * save the encrypted Video and returns the video as file
     * @param videoName defines, which video has to be returned.
     * @param encryptedVideo represents the encrypted video file
     * @return video as File
     */
    public File saveEncryptedVideo(String videoName, File encryptedVideo) {
        return null;
    }

    /**
     * saves the encrypted metadata of a video and
     * @return a File as File
     */
    public File saveEncryptedMetaData() {
        return null;
    }

    /**
     * save the readable metadata of a video and returns a File as File
     * @param videoName represents the name of the video
     * @param metadata are the metadata as a Metadata object
     * @return a File as a File
     */
    public File saveReadableMetadata(String videoName, Metadata metadata) {
        return null;
    }


    /**
     * deletes the encrypted symmetric key
     * @param videoName is the name of the video to find the encrypted video key
     */
    public void deleteEncryptedSymmetricKey(String videoName) {

    }

    /**
     * deletes the encrypted video File
     * @param videoName is the name of the video to find the encrypted video file
     */
    public void deleteEncryptedVideo(String videoName) {

    }

    /**
     * deletes the encrypted metadata file
     * @param videoName is the name of the video
     */
    public void deleteEncryptedMetadata(String videoName) {

    }

    /**
     * deletes the readable metadata
     * @param videoName is the name of the video
     */
    public void deleteReadableMetadata(String videoName) {

    }

    /**
     * @return all videos as an ArrayList<Video>
     */
    public ArrayList<Video> getAllVideos() {
       return null;
    }

    /**
     *
     * @param videoName is the name of the video
     * @return the encrypted symmetric key as a File (video)
     */
    public File getEncryptedSymmetricKey(String videoName) {
        return null;
    }

    /**
     *
     * @param videoName is the name of the video
     * @return the encrypted video file as File
     */
    public File getEncryptedVideo(String videoName) {
        return null;
    }

    /**
     * @param videoName is the name of the video
     * @return encrypted metadata belongs to the video as a File
     */
    public File getEncryptedMetadata(String videoName) {
        return null;
    }

    /**
     * @param videoName is the name of the video
     * @return the readable metadata belongs to the Video as File
     */
    public Metadata getReadableMetadata(String videoName) {
       return null;
    }
}
