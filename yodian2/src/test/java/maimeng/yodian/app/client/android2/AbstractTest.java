package maimeng.yodian.app.client.android2;

import android.content.Context;
import android.os.Build;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.Executor;

import io.realm.RealmObject;
import maimeng.yodian.app.client.android2.network.Network;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN,application = YApplication.class)
public abstract class AbstractTest implements Executor {
    protected Context context;
    @Before
    public void setup(){
        context=RuntimeEnvironment.application;
        Network.getOne().init(RuntimeEnvironment.application);
        before();
    }
    protected abstract void before();
    @Override
    public void execute(Runnable command) {
    }
    protected void json(Object body){
        Gson gson = new GsonBuilder()
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
                .setPrettyPrinting()
                .create();
        System.out.println(gson.toJson(body));
    }
}
