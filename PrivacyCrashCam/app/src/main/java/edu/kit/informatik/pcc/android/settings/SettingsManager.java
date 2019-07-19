package edu.kit.informatik.pcc.android.settings;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import edu.kit.informatik.pcc.core.data.IFileManager;

public class SettingsManager implements ISettingsManager {
    private static final String settingsFileName = "settings.json";
    private static final String settingsEncoding = "UTF-8";

    private IFileManager fileManager;

    public void setFileManager(IFileManager fileManager) {
        assert this.fileManager == null;
        this.fileManager = fileManager;
    }


    @Override
    public void storeSettings(Settings settings) {
        assertCompletelySetup();
        String settingsJSON = settings.getAsJSON();
        File settingsFile = fileManager.file(settingsFileName);
        try {
            FileUtils.writeStringToFile(settingsFile, settingsJSON, settingsEncoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Settings loadSettings() {
        assertCompletelySetup();
        Settings settings = new Settings();
        File settingsFile = fileManager.existingFile(settingsFileName);
        if (settingsFile == null) {
            return settings;
        }
        try {
            String settingsJSON = FileUtils.readFileToString(settingsFile, settingsEncoding);
            settings = new Settings(settingsJSON);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    private void assertCompletelySetup() {
        assert fileManager != null;
    }
}
