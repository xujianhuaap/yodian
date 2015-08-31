package maimeng.yodian.app.client.android.network.loader;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.concurrent.CountDownLatch;

import maimeng.yodian.app.client.android.widget.RoundImageView;

/**
 * Created by android on 2015/8/31.
 */
public final class ImageLoaderManager {
    private static ImageLoaderManager instance;

    public RequestManager getRequest() {
        return mRequest;
    }

    private final RequestManager mRequest;

    private ImageLoaderManager(Context app) {
        mRequest = Glide.with(app);
    }

    private static synchronized ImageLoaderManager getInstance(Context app) {
        synchronized (ImageLoaderManager.class) {
            if (instance == null) {
                instance = new ImageLoaderManager(app);
            }
        }
        return instance;
    }

    @Deprecated
    @BindingAdapter("bind:imgUrl")
    public static void image(ImageView iv, String url) {
        if (TextUtils.isEmpty(url)) return;
        new Loader(iv, url).start();
    }

    @Deprecated
    @BindingAdapter({"bind:imgUrl", "bind:placeHolder"})
    public static void image(ImageView iv, String url, Drawable placeHolderDrawable) {
        new Loader(iv, url).placeHolder(placeHolderDrawable).start();
    }

    @Deprecated
    @BindingAdapter({"bind:imgUrl", "bind:placeHolder", "bind:errorImage"})
    public static void image(ImageView iv, String url, Drawable placeHolderDrawable, Drawable errorDrawable) {
        new Loader(iv, url).placeHolder(placeHolderDrawable).error(errorDrawable).start();
    }

    private static Bitmap image(final Context context, final Uri uri) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Bitmap[] bitmaps = new Bitmap[1];
        new Loader(context, uri).callback(new Callback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                bitmaps[0] = bitmap;
            }

            @Override
            public void onLoadEnd() {
                latch.countDown();
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmaps[0];
    }

    public interface Callback {
        void onImageLoaded(Bitmap bitmap);

        void onLoadEnd();
    }

    public static final class LoaderRequest {
        private Loader loader;

        public LoaderRequest(Loader loader) {
            this.loader = loader;
        }
    }

    public static final class Loader {
        private final Uri uri;
        private final Context mContext;
        private int width, height;
        private ImageView iv;
        private Drawable placeHolderDrawable;
        private Drawable errorDrawable;
        private boolean gif = false;
        private boolean circle = false;

        public Loader circle() {
            this.circle = true;
            return this;
        }

        public Loader callback(Callback callback) {
            this.callback = callback;
            return this;
        }

        private Callback callback;

        public Loader gif() {
            this.gif = true;
            return this;

        }

        public Loader(ImageView iv, Uri uri) {
            this(iv.getContext(), uri);
            this.iv = iv;
        }

        @Deprecated
        public Loader(ImageView iv, String uri) {
            this(iv.getContext(), uri);
            this.iv = iv;
        }

        public Loader(Context context, Uri uri) {
            this.uri = uri;
            this.mContext = context;
        }

        @Deprecated
        public Loader(Context context, String uri) {
            this.uri = Uri.parse(uri);
            this.mContext = context;
        }

        private boolean loaded = false;

        public void start() {
            ensureSaneDefaults();
            Log.w(ImageLoaderManager.class.getName(), String.format("start Reqeust Image:%s", this.uri.toString()));
            DrawableTypeRequest<Uri> loader = ImageLoaderManager.getInstance(this.mContext).getRequest().load(this.uri);
            if (width > 0 && height > 0) {
                loader.override(width, height);
            }
            if (this.placeHolderDrawable != null) {
                loader.placeholder(placeHolderDrawable);
            }
            if (this.errorDrawable != null) {
                loader.error(errorDrawable);
            }
            if (circle) {
                loader.transform(new GlideCircleTransform(this.mContext));
            }
            if (gif) {
                loadGif(loader);
            } else {
                loadBitmap(loader);
            }
            loaded = true;
        }

        private void loadBitmap(DrawableTypeRequest<Uri> loader) {
            BitmapTypeRequest<Uri> bitmapRequest = loader.asBitmap();
            if (this.iv != null) {
//                if (iv instanceof RoundImageView && !this.circle) {
//                    bitmapRequest.transform(new GlideCircleTransform(this.mContext));
//                }
                if (callback != null) {
                    bitmapRequest.into(new ImageViewTarget<Bitmap>(iv) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            callback.onImageLoaded(resource);
                            callback.onLoadEnd();
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            e.printStackTrace();
                            callback.onLoadEnd();
                        }
                    });
                } else {
                    bitmapRequest.into(iv);
                }
            } else {
                if (callback != null) {
                    bitmapRequest.into(new SimpleTarget<Bitmap>(720, 720) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            callback.onImageLoaded(resource);
                            callback.onLoadEnd();
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            e.printStackTrace();
                            callback.onLoadEnd();
                        }
                    });
                }
            }
        }

        private void loadGif(DrawableTypeRequest<Uri> request) {
        }

        private void ensureSaneDefaults() {
            if (loaded) {
                Log.w(ImageLoaderManager.class.getName(), String.format("Image Url: %s exist Request", this.uri.toString()));
            } else {

            }
        }

        public Loader width(int width) {
            this.width = width;
            return this;
        }

        public Loader height(int height) {
            this.height = height;
            return this;
        }

        public Loader error(Drawable drawable) {
            this.errorDrawable = drawable;
            return this;
        }

        public Loader placeHolder(Drawable drawable) {
            this.placeHolderDrawable = drawable;
            return this;
        }

        public Loader placeHolder(Bitmap bitmap) {
            this.placeHolderDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            return this;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private Loader placeHolderV21(int resId) {
            this.placeHolderDrawable = mContext.getResources().getDrawable(resId, mContext.getTheme());
            return this;
        }

        public Loader placeHolder(@DrawableRes int resId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return placeHolderV21(resId);
            } else {
                this.placeHolderDrawable = mContext.getResources().getDrawable(resId);
                return this;
            }
        }
    }
}
