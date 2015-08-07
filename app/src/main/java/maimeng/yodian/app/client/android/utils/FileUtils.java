package maimeng.yodian.app.client.android.utils;

import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by android on 15-8-6.
 */
public class FileUtils {
    private static final File rootDir=Environment.getExternalStorageDirectory();

    /**
     *
     * @return
     */
    public static boolean isMount(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /***
     *
     * @param fileName
     * @return
     */
    public static File createFile(String fileName){
        if(isMount()){
            File dir=new File(rootDir,"clothe");
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file=new File(dir,fileName);
            if(!file.exists()){
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
