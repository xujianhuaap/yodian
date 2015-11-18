package maimeng.yodian.app.client.android.network;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.converter.StringConverter;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.databings.ImageBindable;
import maimeng.yodian.app.client.android.model.user.Sex;
import maimeng.yodian.app.client.android.network.common.GsonConverter;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.view.deal.BindStatus;
import maimeng.yodian.app.client.android.view.deal.pay.CertifyStatus;


/**
 * Created by android on 15-6-25.
 */
public class Network {
    private static Network network;
    private ConcurrentHashMap<String, Object> services = new ConcurrentHashMap<>();

    public Gson getGson() {
        return gson;
    }

    private Gson gson;

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
        gsonBuilder=gsonBuilder.registerTypeAdapter(CertifyStatus.class,new GsonConverter.CertifiStatusAdapter());
        gsonBuilder = gsonBuilder.registerTypeAdapter(Date.class, new GsonConverter.DateAdapter());
        gsonBuilder = gsonBuilder.registerTypeAdapter(String.class, new GsonConverter.StringAdapter());
        gsonBuilder = gsonBuilder.registerTypeAdapter(ImageBindable.class, new GsonConverter.ImageBindableAdapter());
        gsonBuilder = gsonBuilder.registerTypeAdapter(BindStatus.class, new GsonConverter.BindStatusAdapter());
        gsonBuilder = gsonBuilder.registerTypeAdapter(Sex.class, new GsonConverter.SexAdapter());
        gson = gsonBuilder.create();
        net = new HNet.Builder()
                .setEndpoint(BuildConfig.API_HOST)
                .setIntercept(new RequestIntercept(app.getApplicationContext()))
                .addConverter(new StringConverter())
                .addConverter(new GsonConverter(gson))
                .addConverter(new StringConverter())
                .build();
        if (BuildConfig.DEBUG) {
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
}
