package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.File;

import de.pcc.privacycrashcam.data.MemoryKeys;
import edu.kit.informatik.pcc.android.settings.Settings;
import edu.kit.informatik.pcc.android.Client;

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
 * <li>A tag marks a certain video and consists of the date the video was captured.
 * All  data related to that video will have the same tag appended after their prefix</li>
 * <p>
 * <li>Also note that files containing JSON Strings solely will have the suffix .json</li>
 * </ul>
 * </p>
 *
 * @author David Laubenstein, Giorgio Gross
 *         Created by Giorgio Gross
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MemoryManager {
    private static final String TAG = MemoryManager.class.getName();
    private static final String TEMP_PARENT_DIR_NAME = "temp";
    private static final String TEMP_DIR_PREFIX = "temp_";


    private static final String KEY_DIR = "keys";
    private static final String VIDEO_DIR = "videos";
    private static final String META_DIR = "meta";

    private static final String KEY_PREFIX = "KEY_";
    private static final String KEY_SUFFIX = "key";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private Context context;
    private String tempDirName = TEMP_DIR_PREFIX + "_0"; // default temp dir name
    private SharedPreferences appPreferences;

    /**
     * if we test, we need access to the files, so here is a boolean to change between
     * internal and external storage
     */
    private final boolean saveInInternalStorage = true;


    /**
     * this is the File of the Directory where the rootProjectDir should be in external storage
     * in case, the boolean saveInInternalStorage is false
     */
    private final File EXTERNAL_STORAGE_DIR_PICTURES = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES);

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Constructor which assign local context, appPreferences and tempDirName
     * @param context is the context
     */
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
     * Deletes all account data of the user
     */
    public void deleteAccountData() {
        for (int videoId : Client.getGlobal().getLocalVideoManager().getLocallyStoredVideoIds()) {
            Client.getGlobal().getLocalVideoManager().deleteContentForVideo(videoId);
        }
        Client.getGlobal().getSessionManager().storeAuthenticationToken(null);
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
     * Creates the temp parent directory if not existing and creates a directory to be used as temp
     * directory for this MemoryManager instance inside of the parent directory. The temp directory
     * will only be created if it does not already exist.
     *
     * @return directory to be used as temp directory for this MemoryManager instance or null
     */
    @Nullable
    private File createTempDir() {
        // Create the parent temp directory if it does not exist
        File tempParentDir = getFilesDir(File.separator + TEMP_PARENT_DIR_NAME);
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
        File tempParentDir = getFilesDir(File.separator + TEMP_PARENT_DIR_NAME);
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
     * returns a directory rootProjectDir/additionalPath
     * the rootProjectDir depends on the boolean saveInternalStorage, so if the boolean is
     * true: the path is in internalStorage, false: the path is in externalStorage
     * @param additionalPath is the additional Path after the rootProjectDir
     * @return the path as File
     */
    private File getFilesDir(String additionalPath) {
        File file;
        if (saveInInternalStorage) {
            file = new File(context.getFilesDir() + additionalPath);
        } else {
            file = new File(EXTERNAL_STORAGE_DIR_PICTURES + additionalPath);
        }
        return file;
    }
}
