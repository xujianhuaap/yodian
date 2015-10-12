package maimeng.yodian.app.client.android.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by android on 15-8-6.
 */
public class FileUtils {
    /**
     * @return
     */
    public static boolean isMount() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /***
     * @param fileName
     * @return
     */
    public static File createFile(String fileName) {
        if (isMount()) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "yodian");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return file;
        }
        return null;
    }


}
