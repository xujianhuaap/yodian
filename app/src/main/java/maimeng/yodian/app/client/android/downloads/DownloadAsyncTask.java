package maimeng.yodian.app.client.android.downloads;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.henjue.library.hnet.Request;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.anntoation.Get;
import org.henjue.library.hnet.anntoation.Path;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.utils.LogUtil;


/**
 * Created by henjue on 2015/6/8.
 */
public class DownloadAsyncTask extends AsyncTask<String, Long, File> {
    private final FileService service;
    private static final int READ_TIME_OUT=10*1000;
    private static final int CONNECT_TIME_OUT=10*1000;
    public interface FileService {
        @Get(value = "{path}", intercept = false)
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
        if(!target.exists()){
            try {
                target.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getResponse(params[0],"GET",target);

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
        if(file.exists()){
            handler.complite(file);
        }
    }


    public void getResponse(String url,String method,File file ){
        if(!file.exists()){
            return;
        }
        FileOutputStream fileOutputStream=null;
        InputStream is=null;
        try {
            //获得url
            URL httpUrl=new URL(url);
            //获得HttpUrlConection
            HttpURLConnection urlConnection=(HttpURLConnection)httpUrl.openConnection();
            //设置相关参数
            urlConnection.setReadTimeout(READ_TIME_OUT);//设置
            urlConnection.setRequestMethod(method);//设置请求方法
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.setDoInput(true);//允许下载
            urlConnection.setDoOutput(false);//允上传
            urlConnection.setDefaultUseCaches(false);//是否允许缓存
            urlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            urlConnection.connect();
            is=urlConnection.getInputStream();
            long max=is.available();
            long cnt=0;
            fileOutputStream=new FileOutputStream(file);
            byte[] buffer=new byte[1024];
            int read=0;
            while ((read=is.read(buffer))!=-1){
                fileOutputStream.write(buffer,0,read);
                fileOutputStream.flush();
                cnt+=buffer.length;
                publishProgress(max,cnt*100/max);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }
}
