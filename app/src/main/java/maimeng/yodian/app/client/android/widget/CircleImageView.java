package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;

/**
 * Created by android on 2015/9/7.
 */
public class CircleImageView extends HoverImageView {
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CircleImageView(Context context) {
        super(context);
        setup();
    }

    protected void setup() {

    }

    @Override
    public void buildBoundPath(Path borderPath) {
        borderPath.reset();
        final int width = getWidth();
        final int height = getHeight();
        final float cx = width * 0.5f;
        final float cy = height * 0.5f;
        final float radius = Math.min(width, height) * 0.5f;

        borderPath.addCircle(cx, cy, radius, Direction.CW);
    }

    @Override
    public void buildBorderPath(Path borderPath) {
        borderPath.reset();

        final float halfBorderWidth = getBorderWidth() * 0.5f;

        final int width = getWidth();
        final int height = getHeight();
        final float cx = width * 0.5f;
        final float cy = height * 0.5f;
        final float radius = Math.min(width, height) * 0.5f;

        borderPath.addCircle(cx, cy, radius - halfBorderWidth, Direction.CW);
    }
}
