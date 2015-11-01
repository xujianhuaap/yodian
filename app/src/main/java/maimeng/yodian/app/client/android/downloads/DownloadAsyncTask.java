package maimeng.yodian.app.client.android.downloads;

import android.os.AsyncTask;
import android.os.Environment;

import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.anntoation.Get;
import org.henjue.library.hnet.anntoation.Path;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.utils.LogUtil;


/**
 * Created by henjue on 2015/6/8.
 */
public class DownloadAsyncTask extends AsyncTask<String, Long, File> {
    private final FileService service;

    public interface FileService {
        @Get(value = "{path}", append = false, intercept = false)
        Response getFile(@Path("path") String path);
    }

    DownloadHandler handler;

    public DownloadAsyncTask(DownloadHandler handler) {
        this.handler = handler;
        service = Network.getService(FileService.class);
    }

    @Override
    protected File doInBackground(String... params) {
        final File target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".apk");
        String param = params[0];
        Response response = service.getFile(param);
        FileOutputStream fos = null;
        try {
            InputStream in = response.getBody().in();
            long max = in.available();
            int len;
            int size = 1024;
            byte[] buf;
            fos = new FileOutputStream(target);
            buf = new byte[size];
            long current = 0;
            while ((len = in.read(buf, 0, size)) != -1) {
                current += len;
                fos.write(buf, 0, len);
                publishProgress(max, current);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return target;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        LogUtil.i(DownloadAsyncTask.class.getSimpleName(), "Max:%d,current:%d", values[0], values[1]);
        handler.sendProgress(values[0].intValue(), values[1].intValue());
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        handler.complite(file);
    }
}
