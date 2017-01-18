package de.pcc.privacycrashcam.utils.dataprocessing;

import android.os.AsyncTask;

import java.io.File;
import java.util.Queue;

import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.utils.encryption.Encryptor;

/**
 * Created by chris on 18.01.17.
 */

public class AsyncPersistor extends AsyncTask<Metadata, Void, Boolean> {

    private MemoryManager memoryManager;
    private Queue<File> ringbuffer;
    private Encryptor encryptor;
    private PersistCallback persistCallback;

    public AsyncPersistor(MemoryManager memoryManager, Queue<File> ringbuffer,
                          PersistCallback persistCallback){
        this.memoryManager = memoryManager;
        this.ringbuffer = ringbuffer;
        this.persistCallback = persistCallback;
    }

    @Override
    protected Boolean doInBackground(Metadata... params) {
        return null;
    }


}
