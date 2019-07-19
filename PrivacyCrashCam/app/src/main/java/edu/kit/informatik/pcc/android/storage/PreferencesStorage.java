package edu.kit.informatik.pcc.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

import de.pcc.privacycrashcam.data.MemoryKeys;

public class PreferencesStorage implements ISimpleValueStorage {
    private SharedPreferences preferences;

    public PreferencesStorage(Context context) {
        preferences = context.getSharedPreferences("de.pcc.privacycrashcam.APP_PREFERENCES",
                Context.MODE_PRIVATE);
    }

    @Override
    public void putString(String key, String value) {
        preferences.edit().putString(key, value);
    }

    @Override
    public String getString(String key) {
        try {
            return preferences.getString(key, null);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
