package de.pcc.privacycrashcam.applicationlogic.camera;

import android.media.MediaRecorder;

import java.io.File;

/**
 * MediaRecorder which allows reading of its output file.
 *
 * @author Giorgio Gross
 */
public class StorableMediaRecorder extends MediaRecorder {
    private File outputFile;
    private boolean isPrepared = false;

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public void setCurrentOutputFile(File outputFile) {
        this.outputFile = outputFile;
        super.setOutputFile(outputFile.getPath());
    }
}
