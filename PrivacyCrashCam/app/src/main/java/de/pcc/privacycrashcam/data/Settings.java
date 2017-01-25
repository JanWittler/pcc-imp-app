package de.pcc.privacycrashcam.data;

import android.media.CamcorderProfile;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to store user settings.
 *
 * @author Giorgio Gross, David Laubenstein
 * Created by Giorgio Gross at 01/20/2017
 */
public class Settings {
    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/
    // JSON keys
    public static final String SETTINGS_MAIN_KEY = "SETTINGS";
    public static final String JSON_KEY_FPS = "fps";
    public static final String JSON_KEY_BUFFER_SIZE_SEC = "bufferSizeSec";
    public static final String JSON_KEY_QUALITY = "quality";

    // default values
    public static final int FPS_DEFAULT = 10;
    public static final int BUFFER_SIZE_SEC_DEFAULT = 10;
    public static final int QUALITY_DEFAULT = CamcorderProfile.QUALITY_480P;
    // Language=JSON
    public static final String JSON_DEFAULT =
            "{\"fps\":" + FPS_DEFAULT
            + ", \"bufferSizeSec\":"+ BUFFER_SIZE_SEC_DEFAULT
            + ", \"quality\":"+QUALITY_DEFAULT+"}";

    private int fps;
    private int bufferSizeSec;
    private int quality;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/
    /**
     * Creates a settings instance with the passed parameters
     *
     * @param fps frames per seconds
     * @param bufferSizeSec size of the buffer in seconds
     * @param quality the quality. See {@link android.media.CamcorderProfile CamcorderProfile} for
     *                quality options
     */
    public Settings (int fps, int bufferSizeSec, int quality) {
        this.fps = fps;
        this.bufferSizeSec = bufferSizeSec;
        this.quality = quality;
    }

    /**
     * Created a settings instance from the passed json string
     *
     * @param jSettings settings in JSON string
     */
    public Settings (String jSettings) throws JSONException {
        JSONObject mJsonSettings = new JSONObject(jSettings);
        this.fps = mJsonSettings.getInt(JSON_KEY_FPS);
        this.bufferSizeSec = mJsonSettings.getInt(JSON_KEY_BUFFER_SIZE_SEC);
        this.quality = mJsonSettings.getInt(JSON_KEY_QUALITY);
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/
    /**
     * Returns the Settings object as JSON string
     *
     * @return Settings as JSON string or the default settings if there was an error
     */
    public String getAsJSON() {
        JSONObject mJsonSettings = new JSONObject();
        try {
            mJsonSettings.put(JSON_KEY_FPS, this.fps);
            mJsonSettings.put(JSON_KEY_BUFFER_SIZE_SEC, this.bufferSizeSec);
            mJsonSettings.put(JSON_KEY_QUALITY, this.quality);
        } catch (JSONException e) {
            e.printStackTrace();
            return JSON_DEFAULT;
        }
        return mJsonSettings.toString();
    }

    /* #############################################################################################
     *                                  getter/ setter
     * ###########################################################################################*/
    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getBufferSizeSec() {
        return bufferSizeSec;
    }

    public void setBufferSizeSec(int bufferSizeSec) {
        this.bufferSizeSec = bufferSizeSec;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}
