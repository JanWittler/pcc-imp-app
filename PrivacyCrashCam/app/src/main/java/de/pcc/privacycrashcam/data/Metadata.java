package de.pcc.privacycrashcam.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 01/23/2017
 */
public class Metadata {
    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/
    long date;
    String triggerType;
    float[] gForce = new float[3];
    /* #############################################################################################
     *                                  constructor
     * ###########################################################################################*/
    public Metadata(long date, String triggerType, float[] gForce) {
        this.date = date;
        this.triggerType = triggerType;
        this.gForce = gForce;
    }

    public Metadata(String json) {
        // in case, the matadata infos are in "meta": {}
        JSONObject obj = null;
        JSONObject meta = null;
        try {
            obj = new JSONObject(json);
            // go into account object
            meta = obj.getJSONObject("meta");
            // save Strings in account object to class attributes
            this.date = meta.getLong("date");
            this.triggerType = meta.getString("triggerType");
            this.gForce[0] = (float) meta.getDouble("gForceX");
            this.gForce[1] = (float) meta.getDouble("gForceY");
            this.gForce[2] = (float) meta.getDouble("gForceZ");
        } catch (JSONException e) {
            // TODO: android exception handling???
            e.printStackTrace();
        }
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
     * @return json String
     */
    public String getAsJSON() {
        //language=JSON
        String json = "{\n" +
                "  \"meta\": {\n" +
                "    \"date\": " + this.date + ",\n" +
                "    \"triggerType\": \"" + this.triggerType + "\",\n" +
                "    \"gForceX\": " + this.gForce[0] + ",\n" +
                "    \"gForceX\": " + this.gForce[1] + ",\n" +
                "    \"gForceX\": " + this.gForce[2] + "\n" +
                "  }\n" +
                "}";
        return json;
    }
}
