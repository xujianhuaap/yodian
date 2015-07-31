package maimeng.yodian.app.client.android.network;

import android.graphics.Bitmap;

import org.henjue.library.hnet.typed.TypedInput;
import org.henjue.library.hnet.typed.TypedOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TypeBitmap implements TypedInput, TypedOutput {
    private final String name;
    private final Bitmap.CompressFormat format;
    private byte[] bytes;

    /**
     * 默认是png格式
     *
     * @param bitmap
     */
    public TypeBitmap(Bitmap bitmap) {
        this(bitmap, Bitmap.CompressFormat.PNG);
    }

    /**
     * 默认是png格式
     *
     * @param bitmap
     */
    public TypeBitmap(Bitmap bitmap, String name) {
        this(bitmap, name, Bitmap.CompressFormat.PNG);
    }

    public TypeBitmap(Bitmap bitmap, Bitmap.CompressFormat format) {
        this(bitmap, String.valueOf(System.currentTimeMillis()), format);
    }

    public TypeBitmap(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        this.name = name;
        this.format = format;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            bitmap.compress(format, 100, bos);
            bos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        } else {
            return new ByteArrayInputStream(new byte[]{0});
        }
    }
}
