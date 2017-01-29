package de.pcc.privacycrashcam.utils.datastructures;

import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Buffer which stores files in a fifo-like queue.
 *
 * @author Giorgio Gross, Josh Romanowski
 */
public class FileRingBuffer {
    private final static String TAG = FileRingBuffer.class.getName();

    private Queue<File> queue;
    private AtomicInteger activeSaves = new AtomicInteger(0);

    /**
     * Creates a new queue with the passed capacity.
     *
     * @param capacity max number of elements
     */
    public FileRingBuffer(int capacity) {
        // add 1 to ensure that we will record at least capacity videos
        this.queue = new ArrayBlockingQueue<>(capacity + 1);
    }

    /**
     * Add new file to the buffer. <b>Will remove and delete the oldest file</b> from the buffer if
     * the buffer is filled.
     *
     * @param file element to be added
     */
    public void put(File file) {
        if (!queue.offer(file)) {
            // Queue reached its capacity. Remove head and retry.
            pop().delete();
            // retry inserting file
            put(file);
        }

        FinishWritingObserver observer = new FinishWritingObserver(file.getAbsolutePath(),
                FileObserver.CLOSE_WRITE);
        observer.startWatching();
        activeSaves.incrementAndGet();
    }

    /**
     * Removes all files from the buffer and deletes them.
     */
    public void flushAll() {
        for (File file : queue) {
            if (file.exists())
                file.delete();
        }
    }

    /**
     * Gets and removes the head of the queue. The file will (of course) not be deleted.
     *
     * @return the queue head or null
     */
    public
    @Nullable
    File pop() {
        return queue.poll();
    }

    /**
     * Demands the FileRingBuffer to provide the data.
     * As writing to the buffer happens asynchronous to demanding the data
     * so demand data waits until all writing has finished.
     *
     * @return the queue or null if there was an error
     */
    public Queue<File> demandData() {

        for (File file : queue) {
            try {
                while (!isFileIsWritten(file)) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Log.w(TAG, "Error while waiting for writing videos to finish");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null; // todo maybe remove just the file from the buffer and continue?
            }
        }


        return queue;
    }

    private boolean isFileIsWritten(File file) throws IOException {
        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
        FileLock lock;
        try {
            lock = channel.tryLock();
            Log.w(TAG, "lock successful");
            lock.release();
            return true;
        } catch (OverlappingFileLockException e) {
            Log.w(TAG, "lock failed");
            return false;
        }
    }

    /**
     * Observer used to notify that writing all video snippets has finished.
     */
    private class FinishWritingObserver extends FileObserver {
        private FinishWritingObserver(String path, int mask) {
            super(path, mask);
        }

        public void onEvent(int event, String path) {
            Log.w("WEE", "file write complete");
            activeSaves.decrementAndGet();
            this.stopWatching();
        }
    }
}
