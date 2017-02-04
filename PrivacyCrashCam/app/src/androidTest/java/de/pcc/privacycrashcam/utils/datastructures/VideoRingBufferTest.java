package de.pcc.privacycrashcam.utils.datastructures;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class VideoRingBufferTest {
    private static final int CAPACITY = 3;
    /**
     * Parent directory for all files needed for or created in this test
     */
    private static final String TEST_DIRECTORY_NAME = "bufferTestData";

    private File bufferTestDirectory;
    private File[] videoChunks;

    private VideoRingBuffer mBuffer;

    @BeforeClass
    public static void initialize() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        bufferTestDirectory = InstrumentationRegistry.getTargetContext().getDir(TEST_DIRECTORY_NAME, Context.MODE_PRIVATE);

        mBuffer = new VideoRingBuffer(CAPACITY, bufferTestDirectory, "mp4");
        videoChunks = new File[CAPACITY * 2];
        for (int i = 0; i < videoChunks.length; i++) {
            videoChunks[i] = createFile("VID_"+i+".mp4");
        }
    }

    private File createFile(String name) throws IOException {
        File file = new File(bufferTestDirectory, name);
        file.createNewFile();
        return file;
    }

    @Test
    public void bufferValidation() {
        assertEquals(CAPACITY, mBuffer.getCapacity());
    }

    @Test
    public void putCapacity() throws Exception {
        for(int i = 0; i < mBuffer.getCapacity(); i++) mBuffer.put(videoChunks[i]);
        Queue<File> bufferContent = mBuffer.demandData();

        assertTrue(bufferContent.size() == mBuffer.getCapacity());
        assertEquals(bufferContent.poll(), videoChunks[0]);
        assertEquals(mBuffer.pop(), videoChunks[0]);
    }

    @Test
    public void putMoreThanCapacity() throws Exception {
        for (File videoChunk : videoChunks) mBuffer.put(videoChunk);

        Queue<File> bufferContent = mBuffer.demandData();
        assertEquals(CAPACITY, bufferContent.size());
        assertTrue(videoChunks.length > bufferContent.size());
        for (int i = videoChunks.length - bufferContent.size(); i < videoChunks.length; i++) {
            assertEquals(videoChunks[i], bufferContent.poll());
        }
    }

    @Test
    public void popCapacity() throws Exception {
        for(int i = 0; i < mBuffer.getCapacity(); i++) mBuffer.put(videoChunks[i]);

        assertTrue(videoChunks.length >= mBuffer.getCapacity());
        for(int i = 0; i < mBuffer.getCapacity(); i++) {
            assertEquals(videoChunks[i], mBuffer.pop());
        }
    }

    @Test
    public void popMoreThanCapacity() throws Exception {
        for(int i = 0; i < mBuffer.getCapacity(); i++) mBuffer.put(videoChunks[i]);

        assertTrue(videoChunks.length >= mBuffer.getCapacity());
        for(int i = 0; i < mBuffer.getCapacity(); i++) {
            assertEquals(videoChunks[i], mBuffer.pop());
        }

        assertNull(mBuffer.pop());
    }

    @Test
    public void demandData() throws Exception {

    }

    @Test
    public void destroy() throws Exception {

    }

    @Test
    public void flushAll() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        for (File file : videoChunks) {
            if (file != null) file.delete();
        }
    }

    @AfterClass
    public static void shutDown() throws Exception {

    }

    /**
     * Recursively delete directory and files inside directory
     * @param dir directory or file to be deleted
     */
    private static void recDeleteDir(File dir) {
        if(dir.isDirectory()) {
            for (File file : dir.listFiles()){
                recDeleteDir(file);
            }
        }
        dir.delete();
    }
}