package de.pcc.privacycrashcam.data.memoryaccess;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Giorgio Gross
 */
public class MemoryManagerTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File CreateFile (File dir, String name) throws IOException {
        File file = new File(dir, name);
        file.createNewFile();
        return file;
    }

}