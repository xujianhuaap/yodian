package maimeng.yodian.app.client.android.network;

import android.app.Application;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.squareup.picasso.Target;

import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.http.ClientStack;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.common.loader.ImageLoader;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.common.ApacheHttpStack;
import maimeng.yodian.app.client.android.network.common.GsonConverter;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.network.response.TypeData;
import maimeng.yodian.app.client.android.widget.RoundImageView;


/**
 * Created by android on 15-6-25.
 */
public class Network {
    private static Network network;
    private ConcurrentHashMap<String, Object> services = new ConcurrentHashMap<>();

    public synchronized static Network getOne() {
        synchronized (Network.class) {
            if (network == null) {
                network = new Network();
                return network;
            } else {
                return network;
            }
        }
    }

    private Network() {
    }

    private HNet net;

    public void init(Application app) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(TypeData.class, new GsonConverter.TypeDataAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(Date.class, new GsonConverter.DateAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(String.class, new GsonConverter.StringAdapter());
        net = new HNet.Builder()
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
        if (BuildConfig.LOG_DEBUG) {
            net.setLogLevel(HNet.LogLevel.FULL);
        }
    }

    public static HNet getNet() {
        return getOne().net;
    }

    public static <T> T getService(Class<T> clazz) {
        T service = (T) getOne().services.get(clazz.getName());
        if (service != null) {
            return service;
        } else {
            service = getOne().net.create(clazz);
            getOne().services.put(clazz.getName(), service);
            return service;
        }
    }


    public static void image(RoundImageView iv, String url) {
        ImageLoader.image(iv, url);
    }

    public static void image(Context context, String url, Target target) {

        image(context,url,target,0,0);


    }

    public static void image(Context context, String url, Target target,int width,int height) {

        String path = null;
        try {
            path = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            path = url;
        } finally {
            if(width>0&&height>0){
                ImageLoader.image(context, path, target,width,height);
            }else {
                ImageLoader.image(context,url,target);
            }

        }


    }
}
