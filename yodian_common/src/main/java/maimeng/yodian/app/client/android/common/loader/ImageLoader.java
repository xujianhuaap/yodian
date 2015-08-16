package maimeng.yodian.app.client.android.common.loader;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import maimeng.yodian.app.client.android.common.BuildConfig;
import maimeng.yodian.app.client.android.common.R;

/**
 * Created by android on 2015/7/31.
 */
public class ImageLoader {
    public static final boolean DEBUG = true;
    private static ImageLoader network;
    private final int screenWidth;
    private final int screenHeight;
    private final Picasso loader;

    synchronized static ImageLoader getOne(Context app) {
        synchronized (ImageLoader.class) {
            if (network == null) {
                network = new ImageLoader(app);
                return network;
            } else {
                return network;
            }
        }
    }

    private ImageLoader(Context app) {
        screenWidth = app.getResources().getDisplayMetrics().widthPixels;
        screenHeight = app.getResources().getDisplayMetrics().heightPixels;
        loader = Picasso.with(app);
        loader.setLoggingEnabled(BuildConfig.DEBUG || DEBUG);
        loader.setIndicatorsEnabled(BuildConfig.DEBUG || DEBUG);
    }

    public static Bitmap image(final Context context, final String uri) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Bitmap[] bitmaps = {null};
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Picasso loader = getOne(context).loader;
                    bitmaps[0] = loader.load(uri).get();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
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

    public static void image(Context context, String uri, Target target) {
        image(context, uri, -1, target);

    }

    public static void image(Context context, String uri, int placeHolderDrawable, Target target) {
        image(context, uri, placeHolderDrawable, -1, target);
    }

    public static void image(Context context, String uri, Target target, int width, int height) {
        image(context, uri, -1, -1, target, width, height);

    }

    public static void image(Context context, String uri, int placeHolderDrawable, Target target, int width, int height) {
        image(context, uri, placeHolderDrawable, -1, target, width, height);

    }

    public static void image(Context context, String uri, int placeHolderDrawable, int errorDrawable, Target target, int width, int height) {
        ImageLoader one = getOne(context);
        RequestCreator load = one.loader.load(uri);
        load.resize(width, height);
        if (placeHolderDrawable <= 0) placeHolderDrawable = R.drawable.default_place_holder;
        if (errorDrawable != -1) load.error(errorDrawable);
        load.placeholder(placeHolderDrawable);
        load.into(target);
    }

    public static void image(Context context, String uri, int placeHolderDrawable, int errorDrawable, Target target) {
        image(context, uri, placeHolderDrawable, errorDrawable, target, getOne(context).screenWidth, getOne(context).screenHeight);
    }

    @BindingAdapter("bind:imgUrl")
    public static void image(ImageView iv, String url) {
        image(iv, url, -1, -1);
    }

    @BindingAdapter({"bind:imgUrl", "bind:placeHolder"})
    public static void image(ImageView iv, String url, Drawable placeHolderDrawable) {
        image(iv, url, placeHolderDrawable, null);
    }

    @BindingAdapter({"bind:imgUrl", "bind:placeHolder", "bind:errorImage"})
    public static void image(ImageView iv, String url, Drawable placeHolderDrawable, Drawable errorDrawable) {
        RequestCreator load = getOne(iv.getContext()).loader.load(url);
        load.tag(iv.getContext());
        if (placeHolderDrawable == null)
            placeHolderDrawable = iv.getResources().getDrawable(R.drawable.default_place_holder);
        if (errorDrawable != null) load.error(errorDrawable);
        load.placeholder(placeHolderDrawable);
        load.fit();
        load.into(iv);
    }

    public static void image(ImageView iv, String url, int placeHolderDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image(iv, url, iv.getResources().getDrawable(placeHolderDrawable, iv.getContext().getTheme()), null);
        } else {
            image(iv, url, iv.getResources().getDrawable(placeHolderDrawable), null);
        }
    }

    public static void image(ImageView iv, String url, int placeHolderDrawable, int errorDrawable) {
        Drawable place = placeHolderDrawable == -1 ? null : iv.getResources().getDrawable(placeHolderDrawable);
        Drawable error = errorDrawable == -1 ? null : iv.getResources().getDrawable(errorDrawable);
        image(iv, url, place, error);
    }

    private static class ImageTarget implements Target {
        private final ImageView mImageView;

        public ImageTarget(ImageView iv) {
            this.mImageView = iv;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mImageView.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mImageView.setImageDrawable(placeHolderDrawable);
        }
    }
}
