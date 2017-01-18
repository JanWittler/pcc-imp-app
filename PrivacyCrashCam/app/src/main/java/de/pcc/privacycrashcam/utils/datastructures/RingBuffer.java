package de.pcc.privacycrashcam.utils.datastructures;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Buffer who stores its data in a fifo.
 *
 * todo this class is not necessary as ArrayBlockingQueue does all we want
 */
public class RingBuffer<E> {

    private int size;// number of elements
    private Queue<E> queue;

    /**
     * Creates a new buffer with the passed size.
     * @param size max number of elements
     */
    public RingBuffer(int size) {
        this.queue = new ArrayBlockingQueue<>(size);
        this.size = size;
    }

    /**
     * Add new element to the buffer. Will remove the oldest element from the buffer if the buffer
     * is filled.
     * @param element element to be added
     * @return true if element was added
     */
    public boolean offer(E element){
        return false;
    }

    public E pop() {
        return null;
    }

    public Queue<E> getData() {
        return queue;
    }

}
