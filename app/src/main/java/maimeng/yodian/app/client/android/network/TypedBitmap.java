package maimeng.yodian.app.client.android.network;

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
     * 图片宽
     */
    private int maxWidth = 960;
    /**
     * 图片高
     */
    private int maxHeight = 1280;
    private Bitmap mBitmap;

    public static class Builder {

        private final String name;
        private Bitmap.CompressFormat format;
        private final Bitmap bitmap;

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setFormat(Bitmap.CompressFormat format) {
            this.format = format;
            return this;
        }


        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        /**
         * 图片宽
         */
        private int width = 960;
        /**
         * 图片高
         */
        private int height = 1280;

        public Builder(Bitmap bitmap) {
            this(bitmap, String.valueOf(System.currentTimeMillis()), -1, -1);
        }

        public Builder setSize(int size) {
            this.height = size;
            this.width = size;
            return this;
        }

        public Builder(Bitmap bitmap, int width, int height) {
            this(bitmap, String.valueOf(System.currentTimeMillis()), width, height);
        }

        public Builder(Bitmap bitmap, String name, int width, int height) {
            this.name = name;
            this.bitmap = bitmap;
            this.width = width;
            this.height = height;
            this.format = Bitmap.CompressFormat.PNG;
        }

        public TypedBitmap build() {
            return new TypedBitmap(width, height, bitmap, name, format);
        }

    }

    private final String name;
    private final Bitmap.CompressFormat format;
    private byte[] bytes;


    private TypedBitmap(int maxW, int maxH, Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        this.name = name;
        this.format = format;
        this.mBitmap = bitmap;
        this.maxWidth = maxW;
        this.maxHeight = maxH;
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
                if (mBitmap != null) {
                    bytes = getBytes(this.mBitmap);
                }
            } else {
                //throw new IllegalArgumentException("width > 0 and height > 0");
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
