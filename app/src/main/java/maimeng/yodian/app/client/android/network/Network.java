package maimeng.yodian.app.client.android.network;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;


import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.http.ClientStack;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.common.ApacheHttpStack;
import maimeng.yodian.app.client.android.network.common.GsonConverter;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.network.response.TypeData;
import maimeng.yodian.app.client.android.utils.LogUtil;


/**
 * Created by android on 15-6-25.
 */
public class Network {
    private static Network network;
    private ConcurrentHashMap<String,Object> services=new ConcurrentHashMap<>();
    public synchronized static Network getOne(){
        synchronized (Network.class){
            if(network==null){
                network=new Network();
                return network;
            }else{
                return network;
            }
        }
    }
    private Network(){
    }
    private HNet net;
    public void init(Application app){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(TypeData.class, new GsonConverter.TypeDataAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(Date.class, new GsonConverter.DateAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(String.class, new GsonConverter.StringAdapter());
        net=new HNet.Builder()
                .setEndpoint(ApiConfig.API_HOST)
                .setClient(new ClientStack.Provider() {
                    @Override
                    public ClientStack get() {
                        return new ApacheHttpStack();
                    }
                })
                .setIntercept(new RequestIntercept(app.getApplicationContext()))
                .setConverter(new GsonConverter(gsonBuilder.create()))
                .build();
        if(BuildConfig.LOG_DEBUG) {
            net.setLogLevel(HNet.LogLevel.FULL);
        }
    }
    public static HNet getNet(){
        return getOne().net;
    }
    public static <T> T getService(Class<T> clazz){
        T service=(T)getOne().services.get(clazz.getName());
        if(service!=null){
            return service;
        }else{
            service=getOne().net.create(clazz);
            getOne().services.put(clazz.getName(), service);
            return service;
        }
    }


    public static Bitmap image(final Context context,final String uri){
        final CountDownLatch latch = new CountDownLatch(1);
        final Bitmap[] bitmaps = {null};
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Picasso with = Picasso.with(context);
                    with.setLoggingEnabled(BuildConfig.LOG_DEBUG);
                    bitmaps[0]= with.load(uri).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            }
        }.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmaps[0];

    }
    public static void image(Context context,String uri,Target target){
        image(context,uri,-1,target);

    }
    public static void image(Context context,String uri,int placeHolderDrawable,Target target){
        image(context, uri, placeHolderDrawable, -1, target);

    }
    public static void image(Context context,String uri,int placeHolderDrawable,int errorDrawable,Target target){
        Picasso with = Picasso.with(context);
        with.setLoggingEnabled(BuildConfig.LOG_DEBUG);
        RequestCreator load = with.load(uri);
        if(placeHolderDrawable!=-1)load.placeholder(placeHolderDrawable);
        if(errorDrawable!=-1)load.error(errorDrawable);
        load.into(target);

    }



    public static void image(String url,ImageView iv){
        image(url,iv,-1,-1);
    }
    public static void image(String url,ImageView iv,int placeHolderDrawable){
        image(url,iv,placeHolderDrawable,-1);
    }
    public static void image(String url,ImageView iv,int placeHolderDrawable,int errorDrawable){
        Picasso with = Picasso.with(iv.getContext());
        with.setLoggingEnabled(BuildConfig.LOG_DEBUG);
        RequestCreator load = with.load(url);
        if(placeHolderDrawable!=-1)load.placeholder(placeHolderDrawable);
        if(errorDrawable!=-1)load.error(errorDrawable);
        load.into(new ImageTarget(iv));
    }
    private static class ImageTarget implements Target {
        private final ImageView mImageView;

        public ImageTarget(ImageView iv) {
            this.mImageView=iv;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            LogUtil.d("ImageTarget", "onBitmapFailed");
            mImageView.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mImageView.setImageDrawable(placeHolderDrawable);
        }
    }
}
