package de.pcc.privacycrashcam.data;

import java.io.File;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/24/17.
 */

public class Video {
    public static final String PREFIX = "VIDEO_";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/
    String name;
    File encVideoFile;
    File encMetaFile;
    Metadata readableMetadata;
    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/
    public Video(String name, File encVideoFile, File encMetaFile, Metadata readableMetadata) {
        this.name = name;
        this.encVideoFile = encVideoFile;
        this.encMetaFile =  encMetaFile;
        this.readableMetadata = readableMetadata;
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

    public String extractTagFromName() {
        if(!name.startsWith(PREFIX)) return name;
        return name.replaceFirst(PREFIX, "");
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
}