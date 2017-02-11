package de.pcc.privacycrashcam.utils.dataprocessing;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Video;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManagerTest;
import de.pcc.privacycrashcam.utils.datastructures.VideoRingBuffer;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * @author Giorgio Gross
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncPersistorTest {
    private static final int CAPACITY = 3;
    private static final String VIDEO_TAG = "123456789";
    private static final String TEST_DIRECTORY_NAME = "PersistorTestData";
    private static final String TEST_METADATA_R = Metadata.PREFIX_READABLE + VIDEO_TAG + "." + Metadata.SUFFIX;
    private static final String TEST_METADATA = Metadata.PREFIX + VIDEO_TAG + "." + Metadata.SUFFIX;
    private static final String TEST_VIDEODATA = Video.PREFIX + VIDEO_TAG + "." + Video.SUFFIX;

    private File persistorTestDirectory;

    @Mock
    private VideoRingBuffer buffer = mock(VideoRingBuffer.class);
    @Mock
    private MemoryManager memoryManager = mock(MemoryManager.class);
    private ArrayBlockingQueue<File> files = new ArrayBlockingQueue<>(CAPACITY);

    @Before
    public void setUp() throws Exception {
        persistorTestDirectory = InstrumentationRegistry.getTargetContext().getDir(TEST_DIRECTORY_NAME, Context.MODE_PRIVATE);

        for (int i = 0; i < CAPACITY; i++) {
            files.add(MemoryManagerTest.CreateFile(persistorTestDirectory, Video.PREFIX + i + "." + Video.SUFFIX));
        }
        when(buffer.demandData()).thenReturn(files);
        when(buffer.getCapacity()).thenReturn(CAPACITY);

        when(memoryManager.createReadableMetadataFile(VIDEO_TAG)).thenReturn(MemoryManagerTest.CreateFile(persistorTestDirectory, TEST_METADATA_R));
        when(memoryManager.createEncryptedMetaFile(VIDEO_TAG)).thenReturn(MemoryManagerTest.CreateFile(persistorTestDirectory, TEST_METADATA));
        when(memoryManager.createEncryptedVideoFile(VIDEO_TAG)).thenReturn(MemoryManagerTest.CreateFile(persistorTestDirectory, TEST_VIDEODATA));


    }

    @Test
    public void doInBackground() throws Exception {

    }

    @Test
    public void onPostExecute() throws Exception {

    }

    @Test
    public void concatVideos() throws Exception {

    }

    @Test
    public void saveMetadataToFile() throws Exception {


    }

    @After
    public void tearDown() throws Exception {
        for(File file : persistorTestDirectory.listFiles()) {
            file.delete();
        }

    }
}