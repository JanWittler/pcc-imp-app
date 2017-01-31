package de.pcc.privacycrashcam.utils.datastructures;

import android.os.FileObserver;
import android.util.Log;

import java.io.File;

/**
 * Created by Josh Romanowski on 31.01.2017.
 */

public class BufferItem {
    private FileObserver observer;
    private File file;
    private boolean saved = false;

    public BufferItem(File file) {
        this.file = file;
        Log.i("RingBuffer", "adding item named " + this.file.getAbsolutePath());
        observer = new FileObserver(file.getAbsolutePath(), FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String path) {
                if(event == FileObserver.CLOSE_WRITE) {
                    Log.i("RingBuffer", "saved file named " + BufferItem.this.file.getAbsolutePath());
                    saved = true;
                    this.stopWatching();
                }
            }
        };
        observer.startWatching();
    }

    public File getFile() {
        return file;
    }

    public boolean isSaved() {
        return saved;
    }
}