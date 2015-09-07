package maimeng.yodian.app.client.android;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.TestResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;

/**
 * Created by android on 9/2/15.
 */
public class ImageLoaderTest extends ApplicationTest {
    private Handler handler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testSimpleCreate();
        handler = new Handler(getContext().getMainLooper());
    }

    public void testLoaderImage() {
        User user = User.read(getContext());
        assertNotNull(user);
        assertNotNull(user.getAvatar());
        new ImageLoaderManager.Loader(getContext(), Uri.parse(user.getAvatar())).start();
    }

    public void testLoaderImageCallback() {
        final CountDownLatch signal = new CountDownLatch(1);
        final Bitmap[] bitmaps = new Bitmap[1];
        handler.post(new Runnable() {
            @Override
            public void run() {
                User user = User.read(getContext());
                assertNotNull(user);
                assertNotNull(user.getAvatar());
                new ImageLoaderManager.Loader(getContext(), Uri.parse(user.getAvatar())).callback(new ImageLoaderManager.Callback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        bitmaps[0] = bitmap;
                        signal.countDown();
                    }

                    @Override
                    public void onLoadEnd() {
                        assertTrue(true);
                        signal.countDown();
                    }

                    @Override
                    public void onLoadFaild() {
                        assertTrue(true);
                        signal.countDown();
                    }
                }).start();
            }
        });
        try {
            signal.await();
            assertNotNull(bitmaps[0]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
