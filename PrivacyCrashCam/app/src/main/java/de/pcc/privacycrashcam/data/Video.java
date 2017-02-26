package de.pcc.privacycrashcam.data;

import java.io.File;

/**
 * Encapsulates a video object.
 *
 * @author David Laubenstein, Giorgio Gross
 */

public class Video {
    public static final String PREFIX = "VIDEO_";
    public static final String SUFFIX = "mp4";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private String name;
    private File encVideoFile;
    private File encMetaFile;
    private File encSymKeyFile;
    private Metadata readableMetadata;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    public Video(String name, File encVideoFile, File encMetaFile, File encSymKeyFile, Metadata readableMetadata) {
        this.name = name;
        this.encVideoFile = encVideoFile;
        this.encMetaFile =  encMetaFile;
        this.readableMetadata = readableMetadata;
        this.encSymKeyFile = encSymKeyFile;
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /* #############################################################################################
     *                                  getter/ setter
     * ###########################################################################################*/

    public String getName() {
        return name;
    }

    /**
     * Get the tag from the passed video name
     * @param name video name
     * @return the video tag or the passed string if the name does not match the Video RegEx
     */
    public static String ExtractTagFromName(String name) {
        if(!name.matches(PREFIX + ".*\\." + SUFFIX)) return name;

        String tmp = "";
        if(name.startsWith(PREFIX)) {
            tmp = name.replaceFirst(PREFIX, "");
        }
        if (tmp.endsWith(SUFFIX)) {
            return tmp.replace("." + SUFFIX,"");
        }
        return tmp;
    }

    public File getEncVideoFile() {
        return encVideoFile;
    }

    public File getEncMetaFile() {
        return encMetaFile;
    }

    public Metadata getReadableMetadata() {
        return readableMetadata;
    }

    public File getEncSymKeyFile() {
        return encSymKeyFile;
    }
}
