package de.pcc.privacycrashcam.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author David Laubenstein, Giorgio Groß
 *         Created by David Laubenstein on 01/23/2017
 */
public class Metadata {
    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    public static final String PREFIX = "META_";
    public static final String PREFIX_READABLE = "META_R_";
    public static final String SUFFIX = "json";

    private final static String TRIGGER_TYPE_DEFAULT = "NONE";
    private final static String TRIGGER_TYPE_SENSOR = "SENSOR_INPUT";
    private final static String TRIGGER_TYPE_TOUCH = "TOUCH_INPUT";

    // JSON keys
    private final static String JSON_KEY_DATE = "date";
    private final static String JSON_KEY_TRIGGER_TYPE = "triggerType";
    private final static String JSON_KEY_TRIGGER_FORCE_X = "triggerForceX";
    private final static String JSON_KEY_TRIGGER_FORCE_Y = "triggerForceY";
    private final static String JSON_KEY_TRIGGER_FORCE_Z = "triggerForceZ";

    private final static String TAG = Metadata.class.getName();

    long date;
    String triggerType;
    float[] gForce = new float[3];

    /* #############################################################################################
     *                                  constructor
     * ###########################################################################################*/

    /**
     * Default constructor. Will initialize default values.
     */
    public Metadata() {
        this(System.currentTimeMillis(), TRIGGER_TYPE_DEFAULT, new float[]{0, 0, 0});
    }

    public Metadata(long date, String triggerType, float[] gForce) {
        this.date = date;
        this.triggerType = triggerType;
        this.gForce = gForce;
    }

    public Metadata(String json) throws JSONException {
        JSONObject obj;
        JSONObject meta;
        obj = new JSONObject(json);

        // retrieve json data
        meta = obj.getJSONObject("meta");
        this.date = meta.getLong("date");
        this.triggerType = meta.getString("triggerType");
        this.gForce[0] = (float) meta.getDouble("gForceX");
        this.gForce[1] = (float) meta.getDouble("gForceY");
        this.gForce[2] = (float) meta.getDouble("gForceZ");
    }

    public Metadata(File metaFile) throws JSONException, IOException {
        String json = null;

        InputStream inputStream = new FileInputStream(metaFile);

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            json = stringBuilder.toString();
        }

        JSONObject obj;
        JSONObject meta;
        obj = new JSONObject(json);

        // retrieve json data
        meta = obj.getJSONObject("meta");
        this.date = meta.getLong("date");
        this.triggerType = meta.getString("triggerType");
        this.gForce[0] = (float) meta.getDouble("gForceX");
        this.gForce[1] = (float) meta.getDouble("gForceY");
        this.gForce[2] = (float) meta.getDouble("gForceZ");
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /* #############################################################################################
     *                                  getter/ setter
     * ###########################################################################################*/

    public long getDate() {
        return date;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public float[] getgForce() {
        return gForce;
    }

    /**
     * returns JSON String of metadata info
     *
     * @return json String
     */
    public String getAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_KEY_DATE, this.date);
            json.put(JSON_KEY_TRIGGER_TYPE, this.triggerType);
            json.put(JSON_KEY_TRIGGER_FORCE_X, this.gForce[0]);
            json.put(JSON_KEY_TRIGGER_FORCE_Y, this.gForce[1]);
            json.put(JSON_KEY_TRIGGER_FORCE_Z, this.gForce[2]);
        } catch (JSONException e) {
            Log.w(TAG, "Error creating metadata json");
        }
        return json.toString();
    }
}
