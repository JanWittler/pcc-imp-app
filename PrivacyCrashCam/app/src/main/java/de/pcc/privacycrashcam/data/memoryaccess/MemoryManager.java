package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.CamcorderProfile;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import de.pcc.privacycrashcam.data.MemoryKeys;
import de.pcc.privacycrashcam.data.Settings;

/**
 * Handles access to device storage.
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
            settings = new Settings(appPreferences.getString(Settings.SETTINGS_MAIN_KEY, Settings.JSON_DEFAULT));
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

    public void deleteTempData() {

    }
}
