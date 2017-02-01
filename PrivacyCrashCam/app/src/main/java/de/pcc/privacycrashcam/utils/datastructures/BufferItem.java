package de.pcc.privacycrashcam.utils.datastructures;

import android.util.Log;

import java.io.File;

/**
 * Buffer item encapsulating a file and its item. Used in {@link VideoRingBuffer}.
 *
 * @author Josh Romanowski, Giorgio Gross
 */

public class BufferItem {
    private File file;
    private boolean saved = false;

    public BufferItem(File file) {
        this.file = file;
        Log.i("RingBuffer", "adding item named " + this.file.getAbsolutePath());
    }

    /**
     * Sets saved to true.
     */
    public void setSaved() {
        saved = true;
    }

    public File getFile() {
        return file;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setFile(File file) {
        this.file = file;
    }
}