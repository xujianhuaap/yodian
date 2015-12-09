package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * app:roundAsCircle="true"
 * app:roundedCornerRadius="5dp"
 * app:roundBottomLeft="false"
 * app:roundBottomRight="false"
 * app:roundWithOverlayColor="@color/blue"
 * app:roundingBorderWidth="1dp"
 * app:roundingBorderColor="@color/red"
 */
public class YDView extends SimpleDraweeView {


    public YDView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public YDView(Context context) {
        super(context);
    }

    public YDView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YDView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public YDView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //    @Override
//    public void setImageURI(Uri uri) {
//        setImageURI(uri, null);
//    }

    protected ImageRequest getRequest(Uri uri) {
        return null;
    }

    @Override
    public void setImageURI(Uri uri, Object callerContext) {
        if (uri == null) {
            //uri = Uri.parse("http://");
            return;
        }
        PipelineDraweeControllerBuilder controllerBuilder = (PipelineDraweeControllerBuilder) getControllerBuilder();
        if (BuildConfig.DEBUG) {
            controllerBuilder.setControllerListener(new ControllerListener(uri.toString()));
        }
        ImageRequest request = getRequest(uri);
        if (request != null) {
            controllerBuilder.setImageRequest(request);
        }
        DraweeController controller = controllerBuilder
                .setCallerContext(callerContext)
                .setUri(uri)
                .setOldController(getController())
                .build();
        setController(controller);
    }


    public static class ControllerListener extends BaseControllerListener<ImageInfo> {
        private final String url;

        public ControllerListener(String url) {
            this.url = url;
        }

        public void onFinalImageSet(
                String id,
                @Nullable ImageInfo imageInfo,
                @Nullable Animatable anim) {
            if (imageInfo == null) {
                return;
            }
            QualityInfo qualityInfo = imageInfo.getQualityInfo();
            LogUtil.d(YDView.class.getSimpleName(), "Load Image %s\n  received! Size %d x %d\n Quality level %d, good enough: %s, full quality: %s",
                    url,
                    imageInfo.getWidth(),
                    imageInfo.getHeight(),
                    qualityInfo.getQuality(),
                    qualityInfo.isOfGoodEnoughQuality(),
                    qualityInfo.isOfFullQuality());
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            LogUtil.d(YDView.class.getSimpleName(), "Intermediate image received");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            LogUtil.e(YDView.class.getSimpleName(), throwable, "Error loading %s", id);
        }
    }
}
