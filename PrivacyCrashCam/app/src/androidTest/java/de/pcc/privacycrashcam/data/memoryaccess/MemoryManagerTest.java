package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import de.pcc.privacycrashcam.BaseTest;
import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.Metadata;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.Video;
import de.pcc.privacycrashcam.testUtils.FileUtils;

/**
 * @author Giorgio Gross
 */
public class MemoryManagerTest extends BaseTest{

    private static final String KEY_DIR = "keys";
    private static final String VIDEO_DIR = "videos";
    private static final String META_DIR = "meta";

    private static final String KEY_PREFIX = "KEY_";
    private static final String KEY_SUFFIX = "key";

    MemoryManager memoryManager;
    Settings settings;
    Account account;
    Context appContext = InstrumentationRegistry.getTargetContext();

    @BeforeClass
    public static void beforeClass() {

    }

    @Before
    public void before() {
        memoryManager = new MemoryManager(appContext);
    }

    @AfterClass
    public static void afterClass() {

    }

    @After
    public void after() {
        File[] files = context.getFilesDir().listFiles();
        for (File f : files) {
            FileUtils.recDeleteDir(f);
        }
    }

    @Test
    public void settingsTest() {
        settings = new Settings(20,30,Settings.QUALITY_DEFAULT);
        memoryManager.saveSettings(settings);
        Settings s = memoryManager.getSettings();
        Assert.assertTrue(settings.getFps() == s.getFps() &&
                settings.getBufferSizeSec() == s.getBufferSizeSec() &&
                settings.getQuality() == s.getQuality());
    }

    @Test
    public void accountDataTest() {
        account = new Account("test@123.de", "bcd");
        memoryManager.saveAccountData(account);
        Assert.assertTrue(memoryManager.getAccountData().getMail().equals("test@123.de") &&
            memoryManager.getAccountData().getPassword().equals("bcd"));
    }

    @Test
    public void deleteAccountDataTest() {
        account = new Account("a@b.de", "123");
        memoryManager.saveAccountData(account);
        Assert.assertTrue(memoryManager.getAccountData().getMail().equals(account.getMail()) &&
                memoryManager.getAccountData().getPassword().equals(account.getPassword()));
        memoryManager.deleteAccountData();
        Assert.assertTrue(memoryManager.getAccountData() == null);
    }

    @Test
    public void getTempVideoFileTest() {
        File gTVFT = memoryManager.getTempVideoFile();
        Assert.assertTrue(gTVFT != null);
    }

    @Test
    public void getTempMetaDataFileTest() {
        File gTMDFT = memoryManager.getTempMetadataFile();
        Assert.assertTrue(gTMDFT!= null);
    }

    @Test
    public void deleteEncryptedSymmetricKeyFileTest() {
        File dESKF = new File(context.getFilesDir() + File.separator +
                KEY_DIR + File.separator + KEY_PREFIX + "testDESKF" + "." + KEY_SUFFIX);
        dESKF.mkdirs();
        Assert.assertTrue(memoryManager.deleteEncryptedSymmetricKeyFile("testDESKF"));
        Assert.assertTrue(!dESKF.exists());
    }

    @Test
    public void deleteEncryptedMetadataFileTest() {
        File dEMFT = new File(context.getFilesDir() + File.separator +
                META_DIR + File.separator + Metadata.PREFIX +
                "testDEMFT" + "." + Metadata.SUFFIX);
        dEMFT.mkdirs();
        Assert.assertTrue(memoryManager.deleteEncryptedMetadataFile("testDEMFT"));
    }

    @Test
    public void deleteReadableMetadataTest() {
        File dRMT = new File(context.getFilesDir() + File.separator +
                META_DIR + File.separator + Metadata.PREFIX_READABLE +
                "testDRMT" + "." + Metadata.SUFFIX);
        dRMT.mkdirs();
        Assert.assertTrue(memoryManager.deleteReadableMetadata("testDRMT"));
        Assert.assertTrue(!dRMT.exists());
    }

    @Test
    public void deleteEncryptedVideoFileTest() {
        File dEVFT = new File(context.getFilesDir() + File.separator +
                VIDEO_DIR + File.separator + Video.PREFIX +
                "testDEVFT" + "." + Video.SUFFIX);
        dEVFT.mkdirs();
        Assert.assertTrue(memoryManager.deleteEncryptedVideoFile("testDEVFT"));

    }

    @Test
    public void createEncryptedSymmetricKeyFileTest() {
        File cESKFT = memoryManager.createEncryptedSymmetricKeyFile("testCESKFT");
        Assert.assertTrue(cESKFT.getParent().equals(context.getFilesDir() +
                File.separator + KEY_DIR));
        Assert.assertTrue(cESKFT.getName().equals(KEY_PREFIX + "testCESKFT" + "." + KEY_SUFFIX));
    }

    @Test
    public void createEncryptedVideoFileTest() {
        File cEVFT = memoryManager.createEncryptedVideoFile("testCEVFT");
        Assert.assertTrue(cEVFT.getParent().equals(context.getFilesDir() +
                File.separator + VIDEO_DIR));
        Assert.assertTrue(cEVFT.getName().equals(Video.PREFIX +
                "testCEVFT" + "." + Video.SUFFIX));
    }

    @Test
    public void createEncryptedMetaFileTest() {
        File cEMFT = memoryManager.createEncryptedMetaFile("testCEMFT");
        Assert.assertTrue(cEMFT.getParent().equals(context.getFilesDir() +
                File.separator + META_DIR));
        Assert.assertTrue(cEMFT.getName().equals(Metadata.PREFIX +
                "testCEMFT" + "." +Metadata.SUFFIX));
    }

    @Test
    public void createReadableMetadataFileTest() {
        File cRMFT = memoryManager.createReadableMetadataFile("testCRMFT");
        Assert.assertTrue(cRMFT.getName().equals(Metadata.PREFIX_READABLE +
                "testCRMFT" + "." + Metadata.SUFFIX));
    }

    @Test
    public void getAllVideosTest() {
        File v1 = new File(context.getFilesDir() + File.separator +
                VIDEO_DIR + File.separator + Video.PREFIX + "v1" + "." + Video.SUFFIX);
        File v2 = new File(context.getFilesDir() + File.separator +
                VIDEO_DIR + File.separator + Video.PREFIX + "v2" + "." + Video.SUFFIX);
        File v3 = new File(context.getFilesDir() + File.separator +
                VIDEO_DIR + File.separator + Video.PREFIX + "v3" + "." + Video.SUFFIX);
        File v4 = new File(context.getFilesDir() + File.separator +
                VIDEO_DIR + File.separator + Video.PREFIX + "v4" + "." + Video.SUFFIX);
        v1.mkdirs();
        List<Video> gAVT= memoryManager.getAllVideos();
        int test = gAVT.size();
        for (Video v : gAVT) {
            Assert.assertTrue(v.getEncVideoFile().exists());
        }
    }

    @Test
    public void getEncryptedSymmetricKeyTest() {
        File gESKT = new File(context.getFilesDir() + File.separator +
                KEY_DIR + File.separator + KEY_PREFIX + "testGESKT" + "." + KEY_SUFFIX);
        gESKT.mkdirs();
        Assert.assertTrue(memoryManager.getEncryptedSymmetricKey("testGESKT") != null);
        gESKT.delete();
    }

    @Test
    public void getEncryptedVideoTest() {
        File gESKT = new File(context.getFilesDir() + File.separator +
                VIDEO_DIR + File.separator + Video.PREFIX + "testGESKT" + "." + Video.SUFFIX);
        gESKT.mkdirs();
        Assert.assertTrue(memoryManager.getEncryptedVideo("testGESKT") != null);
        gESKT.delete();
    }

    @Test
    public void getReadableMetadataTest() {
        File gRMT = new File(context.getFilesDir() + File.separator +
                META_DIR + File.separator + Metadata.PREFIX_READABLE +
                "testGRMT" + "." + Metadata.SUFFIX);
        gRMT.mkdirs();
        Assert.assertTrue(memoryManager.getReadableMetadata("testGRMT") != null);
        memoryManager.deleteReadableMetadata("testGRMT");
    }
}