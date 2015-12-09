package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by android on 2015/12/9.
 */
public class YDViewBitmap extends YDView {
    private Bitmap mBitmap;
    private ImageRequest request;

    public YDViewBitmap(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    private void init() {

    }

    public YDViewBitmap(Context context) {
        super(context);
        init();
    }

    public YDViewBitmap(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YDViewBitmap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public YDViewBitmap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected ImageRequest getRequest(Uri uri) {
        request = ImageRequestBuilder.newBuilderWithSource(uri).build();
        return request;
    }

    /**
     * @return bitmap
     * @Deprecated
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void setImageURI(Uri uri, Object callerContext) {
        super.setImageURI(uri, callerContext);
        final DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(request, this);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {

            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                mBitmap = bitmap;
                dataSource.close();
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
