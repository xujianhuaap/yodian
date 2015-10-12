package maimeng.yodian.app.client.android.network;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import org.henjue.library.hnet.typed.TypedInput;
import org.henjue.library.hnet.typed.TypedOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import maimeng.yodian.app.client.android.utils.LogUtil;

public class TypedBitmap implements TypedInput, TypedOutput {
    /**
     * 图片最大大小
     */
    private int maxSize = -1;
    /**
     * 图片宽
     */
    private int maxWidth = -1;
    /**
     * 图片高
     */
    private int maxHeight = -1;
    private Bitmap mBitmap;

    public static class Builder {

        private final String name;
        private Bitmap.CompressFormat format;
        private final Bitmap bitmap;

        public Builder setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder setFormat(Bitmap.CompressFormat format) {
            this.format = format;
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public Builder setAutoMatch(Resources res) {
            this.maxHeight = res.getDisplayMetrics().heightPixels;
            this.maxWidth = res.getDisplayMetrics().widthPixels;
            return this;
        }

        /**
         * 图片最大大小
         */
        private int maxSize = -1;
        /**
         * 图片宽
         */
        private int maxWidth = -1;
        /**
         * 图片高
         */
        private int maxHeight = -1;

        public Builder(Bitmap bitmap) {
            this(bitmap, String.valueOf(System.currentTimeMillis()), -1, -1);
        }

        public Builder(Bitmap bitmap, int maxWidth, int maxHeight) {
            this(bitmap, String.valueOf(System.currentTimeMillis()), maxWidth, maxHeight);
        }

        public Builder(Bitmap bitmap, String name, int maxWidth, int maxHeight) {
            this.name = name;
            this.bitmap = bitmap;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.format = Bitmap.CompressFormat.PNG;
        }

        public TypedBitmap build() {
            return new TypedBitmap(maxWidth, maxHeight, maxSize, bitmap, name, format);
        }

    }

    private final String name;
    private final Bitmap.CompressFormat format;
    private byte[] bytes;


    private TypedBitmap(int maxW, int maxH, int maxSize, Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        this.name = name;
        this.format = format;
        this.mBitmap = bitmap;
        this.maxWidth = maxW;
        this.maxHeight = maxW;
        this.maxSize = maxSize;
    }

    @Override
    public String fileName() {
        if (format == Bitmap.CompressFormat.JPEG) {
            return name + ".jpg";
        } else if (format == Bitmap.CompressFormat.PNG) {
            return name + ".png";
        } else if (format == Bitmap.CompressFormat.WEBP) {
            return name + ".webp";
        } else {
            return name;
        }
    }

    @Override
    public String mimeType() {
        if (format == Bitmap.CompressFormat.JPEG) {
            return "image/jpeg";
        } else if (format == Bitmap.CompressFormat.PNG) {
            return "image/png";
        } else if (format == Bitmap.CompressFormat.WEBP) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }

    @Override
    public long length() {
        if (bytes == null) {
            bytes = getBytes(this.mBitmap);
            if (maxWidth > 0 || maxHeight > 0) {
                this.mBitmap = ThumbnailUtils.extractThumbnail(this.mBitmap, this.maxWidth, this.maxHeight);
                if(mBitmap!=null) {
                    bytes = getBytes(this.mBitmap);
                }
            } else {
                //throw new IllegalArgumentException("maxWidth > 0 and maxHeight > 0");
            }
        }
        if (bytes != null) {
            return bytes.length;
        }
        return 0;

    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        if (bytes != null) {
            outputStream.write(bytes);
        }
    }

    @Override
    public InputStream in() throws IOException {
        LogUtil.d(TypedBitmap.class.getSimpleName(), "in");
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        } else {
            return new ByteArrayInputStream(new byte[]{0});
        }
    }

    private byte[] getBytes(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(format, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
