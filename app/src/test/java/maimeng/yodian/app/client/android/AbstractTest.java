package maimeng.yodian.app.client.android;

import android.content.Context;
import android.os.Build;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN, application = YApplication.class)
public abstract class AbstractTest {
    protected Context context;

    @Before
    public void setup() {
        context = new RenamingDelegatingContext(RuntimeEnvironment.application, "test_");
        before();
    }

    protected abstract void before();

    protected abstract void after();

    @After
    public void end() {
        after();
    }
}
