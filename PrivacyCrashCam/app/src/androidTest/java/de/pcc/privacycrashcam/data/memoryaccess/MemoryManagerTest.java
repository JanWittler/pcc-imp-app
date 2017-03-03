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
import java.io.IOException;

import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.Settings;

import static org.junit.Assert.*;

/**
 * @author Giorgio Gross
 */
public class MemoryManagerTest {

    MemoryManager memoryManager;
    Settings settings;
    Account account;
    Context appContext = InstrumentationRegistry.getTargetContext();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File CreateFile (File dir, String name) throws IOException {
        File file = new File(dir, name);
        file.createNewFile();
        return file;
    }

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

    @Test
    public void deleteEncryptedSymmetricKeyFileTest() {
    }

    @Test
    public void deleteEncryptedMetadataFileTest() {

    }

    @Test
    public void deleteReadableMetadataTest() {

    }

    @Test
    public void deleteEncryptedVideoFileTest() {

    }

    @Test
    public void createEncryptedSymmetricKeyFileTest() {

    }

    @Test
    public void createEncryptedVideoFileTest() {

    }

    @Test
    public void createEncryptedMetaFileTest() {

    }

    @Test
    public void createReadableMetadataFileTest() {

    }

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