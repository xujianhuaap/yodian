package maimeng.yodian.app.client.android2.network;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.RealmObject;
import maimeng.yodian.app.client.android2.BuildConfig;
import maimeng.yodian.app.client.android2.YApplication;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by android on 2015/11/12.
 */
public class Network {
    private static Network network;

    private Retrofit retrofit;
    private ConcurrentHashMap<String, Object> services = new ConcurrentHashMap<>();
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
    public static Retrofit getNet() {
        return getOne().retrofit;
    }

    public static <T> T getService(Class<T> clazz) {
        T service = (T) getOne().services.get(clazz.getName());
        if (service != null) {
            return service;
        } else {
            service = getOne().retrofit.create(clazz);
            getOne().services.put(clazz.getName(), service);
            return service;
        }
    }
    public void init(Application app) {
        OkHttpClient client=new OkHttpClient();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("OkHttp",convert(message));
//                Log.d("OkHttp",message);
            }
            public String convert(String utfString){
                StringBuilder sb = new StringBuilder();
                int i = -1;
                int pos = 0;
                boolean handle=false;
                while((i=utfString.indexOf("\\u", pos)) != -1){
                    handle=true;
                    sb.append(utfString.substring(pos, i));
                    if(i+5 < utfString.length()){
                        pos = i+6;
                        sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
                    }
                }
                if(!handle){
                    return utfString;
                }
                return sb.toString();
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                String url = original.urlString();
                if (url.indexOf("?") > 0) {

                } else {
                    url += "?";
                }
                Request.Builder builder = original.newBuilder()
                        .method(original.method(), original.body());
                if (YApplication.user != null) {
                    builder.url(url + "SN_KEY_API=" + YApplication.user.getToken());
                }
                Request request = builder.build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        });
        client.interceptors().add(logging);
        gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(client)
                .baseUrl(BuildConfig.API_HOST)
                .addConverterFactory(GsonConverterFactory.create(gson));
        retrofit=builder.build();
    }
    public Gson getGson(){
        return this.gson;
    }
}
