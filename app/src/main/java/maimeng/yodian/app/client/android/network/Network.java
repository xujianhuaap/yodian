package maimeng.yodian.app.client.android.network;

import android.app.Application;

import com.google.gson.GsonBuilder;

import org.henjue.library.hnet.HNet;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.databings.ImageBindable;
import maimeng.yodian.app.client.android.network.common.GsonConverter;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.view.deal.BindStatus;


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
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(Date.class, new GsonConverter.DateAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(String.class, new GsonConverter.StringAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(ImageBindable.class, new GsonConverter.ImageBindableAdapter());
        gsonBuilder = gsonBuilder.registerTypeHierarchyAdapter(BindStatus.class, new GsonConverter.BindStatusAdapter());

        net = new HNet.Builder()
                .setEndpoint(BuildConfig.API_HOST)
                .setIntercept(new RequestIntercept(app.getApplicationContext()))
                .setConverter(new GsonConverter(gsonBuilder.create()))
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
