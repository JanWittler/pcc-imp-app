package de.pcc.privacycrashcam.utils.datastructures;

import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Buffer which stores files in a fifo-like queue.
 *
 * @author Giorgio Groß, Josh Romanowski
 */
public class FileRingBuffer {

    private Queue<File> queue;
    private AtomicInteger activeSaves = new AtomicInteger(0);

    /**
     * Creates a new queue with the passed capacity.
     *
     * @param capacity max number of elements
     */
    public FileRingBuffer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Add new file to the buffer. Will remove and delete the oldest file from the buffer if the buffer
     * is filled.
     *
     * @param file element to be added
     */
    public void put(File file) {
        if (!queue.offer(file)) {
            // Queue reached its capacity. Remove head and retry.
            queue.poll().delete();
            queue.add(file);
        }

        onFileStarted(file);
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
    @Nullable
    public File pop() {
        return queue.poll();
    }

    /**
     * Demands the FileRingBuffer to provide the data.
     * As writing to the buffer happens asynchronous to demanding the data
     * so demand data waits until all writing has finished.
     */
    public Queue<File> demandData() {

        try {
            while (!activeSaves.compareAndSet(0, 0)) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Log.w("WEE", "Error while waiting for writing videos to finish");
        }

        return queue;
    }

    protected void onFileStarted(File file) {
        FinishWritingObserver observer = new FinishWritingObserver(file.getAbsolutePath(),
                FileObserver.CLOSE_WRITE);
        observer.startWatching();
        activeSaves.incrementAndGet();
    }

    protected void onFileFinished() {
        activeSaves.decrementAndGet();
    }

    /**
     * Observer used to notify that writing all video snippets has finished.
     */
    private class FinishWritingObserver extends FileObserver {
        private FinishWritingObserver(String path, int mask) {
            super(path, mask);
        }

        public void onEvent(int event, String path) {
            onFileFinished();
            this.stopWatching();
        }
    }
}
