package maimeng.yodian.app.client.android.databings;

import android.net.Uri;

import maimeng.yodian.app.client.android.network.loader.Circle;

/**
 * Created by android on 9/1/15.
 */
public class ImageBindable implements CharSequence {
    private Uri uri;
    private Circle circle;

    public Uri getUri() {
        return uri;
    }

    public ImageBindable setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public Circle getCircle() {
        return circle;
    }

    public ImageBindable setCircle(Circle circle) {
        this.circle = circle;
        return this;
    }

    @Override
    public int length() {
        if (uri == null) return 0;
        return uri.toString().length();
    }

    @Override
    public char charAt(int index) {
        if (uri == null) return 0;
        return uri.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (uri == null) return null;
        return uri.toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        if (uri == null) return null;

        return uri.toString();
    }
}
