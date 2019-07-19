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

    public final static int VIDEO_CHUNK_LENGTH = 5; // length of video chunks in seconds
}
