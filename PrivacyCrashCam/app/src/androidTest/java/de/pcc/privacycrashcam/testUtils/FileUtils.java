package testUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Giorgio Gross
 */

public class FileUtils {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File CreateFile (File dir, String name) throws IOException {
        File file = new File(dir, name);
        file.createNewFile();
        return file;
    }

}
