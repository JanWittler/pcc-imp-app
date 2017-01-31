package de.pcc.privacycrashcam.utils.datastructures;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Buffer which stores files in a fifo queue.
 *
 * @author Giorgio Groß, Josh Romanowski
 */
public class FileRingBuffer {

    private Queue<BufferItem> queue;
    private int capacity;

    /**
     * Creates a new queue with the passed capacity.
     *
     * @param capacity max number of elements
     */
    public FileRingBuffer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(++capacity);
        this.capacity = capacity;
    }

    /**
     * Add new file to the buffer. Will remove and delete the oldest file from the buffer if the buffer
     * is filled.
     *
     * @param file element to be added
     */
    public void put(File file) {
        BufferItem newItem = new BufferItem(file);
        if (!queue.offer(newItem)) {
            // Queue reached its capacity. Remove head and retry.
            pop().delete();
            queue.add(newItem);
        }
    }

    /**
     * Removes all files from the buffer and deletes them.
     */
    public void flushAll() {
        BufferItem item;
        while ((item = queue.poll()) != null) {
            item.getFile().delete();
        }
    }

    /**
     * Gets and removes the head of the queue. The file will (of course) not be deleted.
     *
     * @return the queue head or null
     */
    @Nullable
    public File pop() {
        return queue.poll().getFile();
    }

    /**
     * Demands the FileRingBuffer to provide the data.
     * As writing to the buffer happens asynchronous to demanding the data
     * so demand data waits until all writing has finished.
     */
    public Queue<File> demandData() {
        Queue<File> ret = new ArrayBlockingQueue<>(capacity);

        for (BufferItem item : queue) {
            Log.i("FileRingBuffer", "checking if file is saved: " + item.getFile().getAbsolutePath());

            try {
                while (!item.isSaved()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ret.add(item.getFile());
        }
        return ret;
    }
}