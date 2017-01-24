package de.pcc.privacycrashcam.utils.datastructures;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Buffer which stores files in a fifo-like queue.
 *
 * @author Giorgio
 */
public class FileRingBuffer {

    private Queue<File> queue;

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
            put(file);
        }
    }

    /**
     * Removes all files from the buffer and deletes them.
     */
    public void flushAll() {
        // todo see javadoc
    }

    /**
     * Gets and removes the head of the queue
     *
     * @return the queue head or null
     */
    public File pop() {
        // todo
        return null;
    }

    /**
     * Gets the whole queue. Note that some of the files in the queue might not have been completely
     * written to memory yet.
     *
     * @return Queue
     */
    public Queue<File> getData() {
        return queue;
    }

    /*
    pseudo code for checking if file was written completely:

        while (file not complete)
        sleep for 1 sec
        read the fourth byte of the file
        if it is not 0 (contains 'f' of the 'ftyp' header) then
            file is complete, break


        --

    If this does not work we can add a file observer along with each file added to the queue.

     */

}
