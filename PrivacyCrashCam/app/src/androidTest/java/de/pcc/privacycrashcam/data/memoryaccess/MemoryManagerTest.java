package de.pcc.privacycrashcam.data.memoryaccess;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.Settings;
import de.pcc.privacycrashcam.data.Video;

/**
 * @author Giorgio Gross
 */
public class MemoryManagerTest {
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
        Assert.assertTrue(memoryManager.getTempVideoFile() != null);
    }

    @Test
    public void getTempMetaDataFileTest() {
        Assert.assertTrue(memoryManager.getTempMetadataFile() != null);
    }

    @Ignore
    @Test
    public void deleteEncryptedSymmetricKeyFileTest() {
        Assert.assertTrue(memoryManager.deleteEncryptedSymmetricKeyFile(
                memoryManager.createEncryptedSymmetricKeyFile("test").getName()
        ));
    }

    @Ignore
    @Test
    public void deleteEncryptedMetadataFileTest() {
        Assert.assertTrue(memoryManager.deleteEncryptedMetadataFile(
                memoryManager.createEncryptedMetaFile("test").getName()
        ));
    }

    @Ignore
    @Test
    public void deleteReadableMetadataTest() {
        File test = memoryManager.createReadableMetadataFile("test");
        Assert.assertTrue(memoryManager.deleteReadableMetadata("test"));
    }

    @Ignore
    @Test
    public void deleteEncryptedVideoFileTest() {
        File test = memoryManager.createEncryptedVideoFile("test");
        Assert.assertTrue(memoryManager.deleteEncryptedVideoFile("test"));
    }

    @Test
    public void createEncryptedSymmetricKeyFileTest() {
        Assert.assertTrue(
                memoryManager.createEncryptedSymmetricKeyFile(
                        "test").getName().equals("KEY_test.key")
        );
        memoryManager.deleteEncryptedSymmetricKeyFile("test");
    }

    @Test
    public void createEncryptedVideoFileTest() {
        Assert.assertTrue(
                memoryManager.createEncryptedVideoFile(
                        "test").getName().equals("VIDEO_test.mp4")
        );
        memoryManager.createEncryptedVideoFile("test");
    }

    @Test
    public void createEncryptedMetaFileTest() {
        Assert.assertTrue(
                memoryManager.createEncryptedMetaFile(
                        "test").getName().equals("META_test.json")
        );
        memoryManager.deleteEncryptedMetadataFile("test");
    }

    @Test
    public void createReadableMetadataFileTest() {
        Assert.assertTrue(
                memoryManager.createReadableMetadataFile(
                        "test").getName().equals("META_R_test.json")
        );
        memoryManager.deleteReadableMetadata("test");
    }

    @Ignore
    @Test
    public void getAllVideosTest() {

    }

    @Test
    public void getEncryptedSymmetricKeyTest() {

    }

    @Test
    public void getEncryptedVideoTest() {

    }

    @Test
    public void getReadableMetadataTest() {

    }

    @Test
    public void getListFilesTest() {

    }
}