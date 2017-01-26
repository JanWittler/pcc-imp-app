package de.pcc.privacycrashcam.data;

import java.io.File;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/24/17.
 */

public class Video {
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
