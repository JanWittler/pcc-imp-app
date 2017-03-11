package de.pcc.privacycrashcam;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.test.InstrumentationRegistry;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import de.pcc.privacycrashcam.applicationlogic.camera.CompatCameraHandler;
import de.pcc.privacycrashcam.applicationlogic.camera.TriggeringCompatCameraHandler;
import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.Video;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.testUtils.FileUtils;
import de.pcc.privacycrashcam.utils.datastructures.VideoRingBuffer;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Mocks all necessary dependencies to be reused with all other test cases. If you need the mocks to
 * return more specific values you will need to add these with when(..).thenReturn(..). This class
 * just sets everything up so that the mocks return their default value.
 *
 * @author Giorgio Gross
 */
public class BaseTest {
    protected Context context;

    /**
     * Encryptor
     */
    protected File encKey;
    protected File encOutputTest;

    /**
     * Parent directory for all files needed for or created in this test
     */
    protected File testDirectory;
    private static final String DEFAULT_TEST_DIRECTORY_NAME = "testData";
    protected static final int CAPACITY = 3;
    protected ArrayBlockingQueue<File> mFiles = new ArrayBlockingQueue<>(CAPACITY);

    /**
     * Video data
     */
    protected static final String VIDEO_TAG = "123456789";
    protected static final long VIDEO_TAG_VAL = 123456789;
    protected static final String TEST_VIDEO = Video.PREFIX + VIDEO_TAG + "." + Video.SUFFIX;
    protected static final String VIDEO_TAG_TEMP = "1234567890";
    protected static final long VIDEO_TAG_TMEP_VAL = 1234567890;
    protected static final String TEST_VIDEO_TEMP = Video.PREFIX + VIDEO_TAG_TEMP + "." + Video.SUFFIX;
    @Mock
    protected VideoRingBuffer bufferMock;

    /**
     * Camera Handler
     */
    @Mock
    protected SurfaceView surfaceViewMock;
    @Mock
    protected SurfaceHolder surfaceHolderMock;
    protected CompatCameraHandler compatCameraHandlerMock; // don't sure if we will ever need them
    protected TriggeringCompatCameraHandler triggeringCompatCameraHandlerMock;

    /**
     * SensorEvent mock in order to use test values
     */
    @Mock
    protected SensorEvent event;

    /**
     * Metadata
     */
    @Mock
    protected Metadata metadataMock;
    protected static final String TEST_METADATA_TEMP = Metadata.PREFIX_READABLE + VIDEO_TAG_TEMP + "." + Metadata.SUFFIX;
    protected static final String TEST_METADATA_R = Metadata.PREFIX_READABLE + VIDEO_TAG + "." + Metadata.SUFFIX;
    protected static final String TEST_METADATA = Metadata.PREFIX + VIDEO_TAG + "." + Metadata.SUFFIX;

    /**
     * Key
     */
    protected static final String TEST_ENC_SYMM_KEY = "KEY_" + VIDEO_TAG + ".key";

    /**
     * Settings
     */
    @Mock
    protected Settings settingsMock;

    /**
     * Memory manager
     */
    @Mock
    protected MemoryManager memoryManagerMock;

    /**
     * Account
     */
    @Mock
    protected Account accountMock;

    /**
     * Keep all mocks in this list
     */
    private ArrayList<Object> mocks;

    @Before
    public void init() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        testDirectory = context.getDir(DEFAULT_TEST_DIRECTORY_NAME, Context.MODE_PRIVATE);

        mocks = new ArrayList<>();
        // init mocks is broken, so use the following line
        MockitoAnnotations.initMocks(this);

        InputStream encKeyStream = getClass().getClassLoader().getResourceAsStream("publickey.key");
        encKey = FileUtils.CreateFile(testDirectory, "publickey.key");
        FileUtils.CopyInputStreamToFile(encKeyStream, encKey);

        encOutputTest = FileUtils.CreateFile(testDirectory, "output.txt");

        // mock ring bufferMock
        for (int i = 0; i < CAPACITY; i++) {
            InputStream video = getClass().getClassLoader().getResourceAsStream("input_small.mp4");
            File videoDest = FileUtils.CreateFile(testDirectory, Video.PREFIX + i + "." + Video.SUFFIX);
            FileUtils.CopyInputStreamToFile(video, videoDest);
            mFiles.add(videoDest);
        }
        when(bufferMock.demandData()).thenReturn(mFiles);
        when(bufferMock.getCapacity()).thenReturn(CAPACITY);
        mocks.add(bufferMock);

        // mock memory manager
        when(memoryManagerMock.createEncryptedSymmetricKeyFile(VIDEO_TAG))
                .thenReturn(FileUtils.CreateFile(testDirectory, TEST_ENC_SYMM_KEY));
        when(memoryManagerMock.createReadableMetadataFile(VIDEO_TAG))
                .thenReturn(FileUtils.CreateFile(testDirectory, TEST_METADATA_R));
        when(memoryManagerMock.createEncryptedMetaFile(VIDEO_TAG)).
                thenReturn(FileUtils.CreateFile(testDirectory, TEST_METADATA));
        when(memoryManagerMock.createEncryptedVideoFile(VIDEO_TAG))
                .thenReturn(FileUtils.CreateFile(testDirectory, TEST_VIDEO));
        when(memoryManagerMock.getTempMetadataFile())
                .thenReturn(FileUtils.CreateFile(testDirectory, TEST_METADATA_TEMP));
        when(memoryManagerMock.getTempVideoFile())
                .thenReturn(FileUtils.CreateFile(testDirectory, TEST_VIDEO_TEMP));
        when(memoryManagerMock.getSettings()).thenReturn(settingsMock);
        mocks.add(memoryManagerMock);


        // mock metadataMock
        when(metadataMock.getDate()).thenReturn(VIDEO_TAG_VAL);
        when(metadataMock.getgForce()).thenReturn(new float[3]);
        when(metadataMock.getTriggerType()).thenReturn(Metadata.TRIGGER_TYPE_DEFAULT);
        when(metadataMock.getAsJSON()).thenReturn("{\n" +
                "  \"date\":123456789,\n" +
                "  \"triggerType\":\"NONE\",\n" +
                "  \"triggerForceX\":0,\n" +
                "  \"triggerForceY\":0,\n" +
                "  \"triggerForceZ\":0\n" +
                "}");
        mocks.add(memoryManagerMock);

        // mock settings

        when(settingsMock.getFps()).thenReturn(Settings.FPS_DEFAULT);
        when(settingsMock.getBufferSizeSec()).thenReturn(Settings.BUFFER_SIZE_SEC_DEFAULT);
        when(settingsMock.getQuality()).thenReturn(Settings.QUALITY_DEFAULT);
        when(settingsMock.getAsJSON()).thenReturn("{\n" +
                "  \"fps\": 10,\n" +
                "  \"bufferSizeSec\": 10,\n" +
                "  \"quality\": 4\n" +
                "}");
        mocks.add(settingsMock);

        // mock camera handler
        when(surfaceViewMock.getHolder()).thenReturn(surfaceHolderMock);
        mocks.add(surfaceViewMock);
        mocks.add(surfaceHolderMock);

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        event.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void cleanUp() throws Exception {
        for (File file : testDirectory.listFiles()) {
            if (file != null) file.delete();
        }

        reset(mocks.toArray());
    }



}
