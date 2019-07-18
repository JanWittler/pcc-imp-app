package edu.kit.informatik.pcc.android.account;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import edu.kit.informatik.pcc.core.data.IFileManager;

public class SessionManager implements ISessionManager {
    private final static String tokenFileName = "token.txt";
    private final static String encoding = "UTF-8";

    private IFileManager fileManager;

    public void setFileManager(IFileManager fileManager) {
        assert this.fileManager == null;
        this.fileManager = fileManager;
    }

    @Override
    public void storeAuthenticationToken(String authenticationToken) {
        assertCompletelySetup();
        File file = fileManager.file(tokenFileName);
        try {
            FileUtils.writeStringToFile(file, authenticationToken, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().warning("Failed to store authentication token");
        }
    }

    @Override
    public String loadAuthenticationToken() {
        assertCompletelySetup();
        File file = fileManager.existingFile(tokenFileName);
        if (file == null) {
            return null;
        }
        try {
            return FileUtils.readFileToString(file, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().warning("Failed to load authentication token");
            return null;
        }
    }

    private void assertCompletelySetup() {
        assert fileManager != null;
    }
}
