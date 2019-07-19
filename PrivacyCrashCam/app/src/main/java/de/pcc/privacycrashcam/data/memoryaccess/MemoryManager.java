package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.pcc.privacycrashcam.data.MemoryKeys;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.Video;
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
     * Gets saved user settings. Returns default settings if reading settings from shared
     * preferences failed.
     *
     * @return settings of user
     */
    public Settings getSettings() {
        Settings settings;
        String jSettings = appPreferences.getString(Settings.SETTINGS_MAIN_KEY, null);
        if(jSettings == null) return new Settings();
        try {
            settings = new Settings(jSettings);
        } catch (JSONException e) {
            settings = new Settings();
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
     * Deletes all account data of the user
     */
    public void deleteAccountData() {
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
     * Creates and returns a temporary file which can be used to save video content. This file is
     * located in the local directory of the app and cannot be accessed from outside.
     * <p>
     * <p>
     * This file won't be accessible the next time the app is started.
     * You can call {@link #deleteCurrentTempData()} to delete all files saved in the local
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

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.GERMANY).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + Video.PREFIX + timeStamp
                + "." + Video.SUFFIX);
    }

    /**
     * Creates and returns a temporary file which can be used to save metadata content. This file is
     * located in the local directory of the app and cannot be accessed from outside.
     * <p>
     * <p>
     * This file won't be accessible the next time the app is started.
     * You can call {@link #deleteCurrentTempData()} to delete all files saved in the local
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
     * Create new directory name to be used as temp directory. The directory will be created when
     * you call {@link #getTempMetadataFile()} or {@link #getTempVideoFile()} next time. You cannot
     * access the old directory anymore after calling this method, calls to
     * {@link #deleteCurrentTempData()} will only affect the new temp directory.
     */
    public void rebaseTempDir() {
        tempDirName = TEMP_DIR_PREFIX + System.currentTimeMillis();
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
     * Deletes the file containing the encrypted symmetric key associated with the passed video
     * tag.
     * <p>
     * <p>The video tag is determined when creating the video file with
     * {@link #createEncryptedVideoFile(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     */
    public boolean deleteEncryptedSymmetricKeyFile(String videoTag) {
        File dir = getFilesDir(File.separator + KEY_DIR);
        if(dir.exists()) {
            File file = new File(dir, KEY_PREFIX + videoTag + "." + KEY_SUFFIX);
            if (file.exists()) {
                file.delete();
                return true;
            } else {
                Log.d(TAG, "File: " + KEY_PREFIX + videoTag + "." + KEY_SUFFIX + " in dir: " +
                        KEY_DIR + "does not exist!");
            }
        } else {
            Log.d(TAG, KEY_DIR + " dir not existing");
        }
        return false;
    }

    /**
     * Deletes the file containing the encrypted metadata associated with the passed video tag.
     * <p>
     * <p>The video tag is determined when creating the video file with
     * {@link #createEncryptedVideoFile(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     */
    public boolean deleteEncryptedMetadataFile(String videoTag) {
        File dir = getFilesDir(File.separator + META_DIR);
        if(dir.exists()) {
            File file = new File(dir, Metadata.PREFIX + videoTag + "." + Metadata.SUFFIX);
            if (file.exists()) {
                file.delete();
                return true;
            } else {
                Log.d(TAG, "File: " + Metadata.PREFIX + videoTag + "." + Metadata.SUFFIX + " in dir: " +
                        META_DIR + "does not exist!");
            }
        } else {
            Log.d(TAG, META_DIR + " dir not existing");
        }
        return false;
    }


    /**
     * Deletes the file containing the readable metadata associated with the passed video tag.
     * <p>
     * <p>The video tag is determined when creating the video file with
     * {@link #createEncryptedVideoFile(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     */
    public boolean deleteReadableMetadata(String videoTag) {
        File dir = getFilesDir(File.separator + META_DIR);
        if(dir.exists()) {
            File file = new File(dir, Metadata.PREFIX_READABLE + videoTag + "." + Metadata.SUFFIX);
            if (file.exists()) {
                file.delete();
                return true;
            } else {
                Log.d(TAG, "File: " + Metadata.PREFIX_READABLE + videoTag + "." + Metadata.SUFFIX + " in dir: " +
                        META_DIR + "does not exist!");
            }
        } else {
            Log.d(TAG, META_DIR + " dir not existing");
        }
        return false;
    }

    /**
     * Deletes the file containing the encrypted video associated with the passed video tag.
     * <p>
     * <p>If you know the video name but not the tag use {@link Video#ExtractTagFromName(String)}</p>
     *
     * @param videoTag Tag of the video
     */
    public boolean deleteEncryptedVideoFile(String videoTag) {
        File dir = getFilesDir(File.separator + VIDEO_DIR);
        if(dir.exists()) {
            File file = new File(dir, Video.PREFIX + videoTag + "." + Video.SUFFIX);
            if (file.exists()) {
                file.delete();
                return true;
            } else {
                Log.d(TAG, "File: " + Video.PREFIX + videoTag + "." + Video.SUFFIX + " in dir: " +
                        VIDEO_DIR + " does not exist!");
            }
        } else {
            Log.d(TAG, VIDEO_DIR + " dir not existing");
        }
        return false;
    }

    /**
     * Adds a suiting prefix to the video name and uses that String to create a new file inside the
     * key folder. The file may be used to write the encrypted symmetric
     * key.
     * <p>
     * <p>The file name will be key_<@param videoTag> where the tag resembles the ending of the file
     * name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createEncryptedSymmetricKeyFile(String videoTag) {
        // use Key.PREFIX as prefix! See Video class for guidance
        File keyDir = getFilesDir(File.separator + KEY_DIR);
        // if dir is not existing, create dir
        if (!keyDir.exists()) {
            if (!keyDir.mkdirs()) {
                Log.d(TAG, "failed to create key directory");
                return null;
            }
        }
        return new File(keyDir, KEY_PREFIX + videoTag + "." + KEY_SUFFIX);
    }

    /**
     * Adds a suiting prefix to the video name and uses that String to create a new file inside the
     * video folder. The file may be used to write the encrypted video data.
     * <p>
     * <p>The file name will be {@link Video#PREFIX VIDEO_}<@param videoTag> where the tag resembles
     * the ending of the file name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createEncryptedVideoFile(String videoTag) {
        // use Video.PREFIX as prefix!
        File videoDir = getFilesDir(File.separator + VIDEO_DIR);
        // if dir is not existing, create dir
        if(!videoDir.exists()) {
            if(!videoDir.mkdir()){
                Log.d(TAG, "failed to create video directory");
                return null;
            }
        }
        return new File(videoDir, Video.PREFIX + videoTag + "." + Video.SUFFIX);
    }

    /**
     * Adds a suiting prefix to the video name and uses that String to create a new file inside the
     * metadata folder. The file may be used to write the encrypted metadata.
     * <p>
     * <p>
     * The file name will be {@link Metadata#PREFIX META_}<@param videoTag> where the tag
     * resembles the ending of the file name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createEncryptedMetaFile(String videoTag) {
        // use Metadata.PREFIX as prefix!
        File metaDir = getFilesDir(File.separator + META_DIR);
        // if dir is not existing, create dir
        if(!metaDir.exists()) {
            if(!metaDir.mkdir()){
                Log.d(TAG, "failed to create meta directory");
                return null;
            }
        }
        return new File(metaDir, Metadata.PREFIX + videoTag + "." + Metadata.SUFFIX);
    }

    /**
     * Adds a suiting prefix to the video name and appends "_readable". Uses that String to create
     * a new file inside the metadata folder. The file may be used to write
     * the readable metadata.
     * <p>
     * <p>The file name will be {@link Metadata#PREFIX_READABLE META_READABLE_}<@param videoTag>
     * where the tag resembles the ending of the file name, e.g. a date</p>
     *
     * @param videoTag Tag of the video this file will be associated with
     */
    public File createReadableMetadataFile(String videoTag) {
        // use Metadata.PREFIX_READABLE as prefix!
        File metaDir = getFilesDir(File.separator + META_DIR);
        // if dir is not existing, create dir
        if(!metaDir.exists()) {
            if(!metaDir.mkdir()){
                Log.d(TAG, "failed to create meta directory");
                return null;
            }
        }
        return new File(metaDir, Metadata.PREFIX_READABLE + videoTag + "." + Metadata.SUFFIX);
    }

    // todo add convenience method to delete all files associated with one video which will call
    // deleteEncryptedSymmetricKeyFile, deleteEncryptedVideoFile, deleteEncryptedMetadataFile and
    // deleteReadableMetadata (nice to have)


    /**
     * Creates and returns a list containing all encrypted videos saved in the video directory
     * <p>
     * <p>See {@link Video}</p>
     *
     * @return Videos as an ArrayList<Video>
     */
    public ArrayList<Video> getAllVideos() {
        ArrayList<Video> allVideos= new ArrayList<>();
        //get video directory
        File videosDir = getFilesDir(File.separator + VIDEO_DIR);
        //Initialize all videos from folder as videos
        List<File> videosInDir = getListFiles(videosDir);
        for (File video : videosInDir) {
            // access to Metadata
            File metaDataFile = getEncryptedMetadata(Video.ExtractTagFromName(video.getName()));
            //File metaDataFile = new File(context.getFilesDir() + File.separator + META_DIR +
             //       video.getName().replaceFirst(Video.PREFIX, Metadata.PREFIX).replaceFirst(Video.SUFFIX, Metadata.SUFFIX));
            Metadata readableMetadata = null;
            try {
                readableMetadata = new Metadata(getReadableMetadata(Video.ExtractTagFromName(video.getName())));
            } catch (JSONException|IOException e) {
                Log.d(TAG, "Error reading metadata file!");
            }
            // add video to arrayList
            allVideos.add(new Video(video.getName(), video,
                    metaDataFile,
                    getEncryptedSymmetricKey(Video.ExtractTagFromName(video.getName())),
                    readableMetadata
                    ));
        }
        return allVideos;
    }

    /**
     * Gets the file containing the encrypted symmetric key from the key directory
     *
     * @param videoTag Tag of the video the file is associated with
     * @return the encrypted symmetric key as a file or null if there is none
     */
    @Nullable
    public File getEncryptedSymmetricKey(String videoTag) {
        File keyDir = getFilesDir(File.separator + KEY_DIR);
        // if dir is not existing, create dir
        if (!keyDir.exists()) {
            if (!keyDir.mkdirs()) {
                Log.d(TAG, "no key directory existing");
                return null;
            }
        }
        File keyFile = new File(keyDir, KEY_PREFIX + videoTag + "." + KEY_SUFFIX);
        if (keyFile.exists()) return keyFile;
        return null;
    }

    /**
     * Gets the file containing the encrypted video from the video directory
     * <p>
     * <p>If you know the video name but not the tag use {@link Video#ExtractTagFromName(String)}</p>
     *
     * @param videoTag Tag of the video the file is associated with
     * @return the encrypted video as a file or null if there is none
     */
    @Nullable
    public File getEncryptedVideo(String videoTag) {
        File videoDir = getFilesDir(File.separator + VIDEO_DIR);
        // if dir is not existing, create dir
        if(!videoDir.exists()) {
                Log.d(TAG, "no video directory existing");
                return null;
        }
        File videoFile = new File(videoDir, Video.PREFIX + videoTag + "." + Video.SUFFIX);
        if (videoFile.exists()) return videoFile;
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
        File metaDir = getFilesDir(File.separator + META_DIR);
        // if dir is not existing, create dir
        if(!metaDir.exists()) {
                Log.d(TAG, "no meta directory existing");
                return null;
            }
        File metaFile = new File(metaDir, Metadata.PREFIX + videoTag + "." + Metadata.SUFFIX);
        if (metaFile.exists()) return metaFile;
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
        File metaDir = getFilesDir(File.separator + META_DIR);
        // if dir is not existing, create dir
        if(!metaDir.exists()) {
                Log.d(TAG, "no meta directory existing");
                return null;
        }
        File metaFile = new File(metaDir, Metadata.PREFIX_READABLE + videoTag + "." + Metadata.SUFFIX);
        if (metaFile.exists()) return metaFile;
        return null;
    }

    /**
     * creates a ArrayList of files in a directory recursively.
     * So all Files in this directory will be added, the dirs in a folder as well.
     * @param parentDir the directory which the files are in
     * @return the Files of the given directory
     */
    private ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        if (files == null) return inFiles;
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    /**
     * returns a directory rootProjectDir without an additional path
     * @return the path as File
     */
    private File getFilesDir() {
        return getFilesDir("");
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
