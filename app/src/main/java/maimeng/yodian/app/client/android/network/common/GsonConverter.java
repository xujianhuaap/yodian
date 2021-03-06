package maimeng.yodian.app.client.android.network.common;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.MimeUtil;
import org.henjue.library.hnet.converter.Converter;
import org.henjue.library.hnet.exception.ConversionException;
import org.henjue.library.hnet.typed.TypedInput;
import org.henjue.library.hnet.typed.TypedOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;

import maimeng.yodian.app.client.android.model.user.Sex;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.deal.BindStatus;
import maimeng.yodian.app.client.android.view.deal.pay.CertifyStatus;


public class GsonConverter implements Converter {
    private final Gson gson;
    private String charset;

    public GsonConverter(Gson gson) {
        this(gson, "UTF-8");
    }

    public GsonConverter(Gson gson, String charset) {
        this.gson = gson;
        this.charset = charset;
    }

    @Override
    public boolean match(Type type) {
        return !String.class.equals(type);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String charset = this.charset;
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType(), charset);
        }
        InputStreamReader isr = null;
        StringBuffer sb = new StringBuffer();
        try {
            isr = new InputStreamReader(body.in(), charset);
            BufferedReader reader = new BufferedReader(isr);
            String str = "";
            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
            String json = sb.toString();
            if (Network.getNet().getLogLevel() != HNet.LogLevel.NONE) {
                LogUtil.i("GsonConverter", json);
            }
            return gson.fromJson(json, type);
        } catch (IOException | JsonParseException e) {
            throw new ConversionException(sb.toString(), e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            return new JsonTypedOutput(gson.toJson(object).getBytes(charset), charset);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;
        private final String mimeType;

        JsonTypedOutput(byte[] jsonBytes, String encode) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + encode;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return jsonBytes.length;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(jsonBytes);
        }
    }

    public static class BooleanAdapter implements JsonDeserializer<Boolean>, JsonSerializer<Boolean> {

        @Override
        public JsonElement serialize(Boolean arg0, Type arg1, JsonSerializationContext arg2) {
            return new JsonPrimitive(arg0 ? 1 : 0);
        }

        @Override
        public Boolean deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            return arg0.getAsInt() != 0;
        }
    }

    public static class StringAdapter implements JsonDeserializer<String>, JsonSerializer<String> {

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return json == null ? "" : json.getAsString();
        }

        @Override
        public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src == null ? "" : src);
        }
    }
    public static class BindStatusAdapter implements JsonDeserializer<BindStatus>, JsonSerializer<BindStatus> {
        @Override
        public BindStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int asInt = json.getAsInt();
            return BindStatus.create(asInt);
        }

        @Override
        public JsonElement serialize(BindStatus src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class CertifiStatusAdapter implements JsonDeserializer<CertifyStatus>, JsonSerializer<CertifyStatus> {
        @Override
        public CertifyStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return CertifyStatus.create(json.getAsInt());
        }

        @Override
        public JsonElement serialize(CertifyStatus src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class SexAdapter implements JsonDeserializer<Sex>, JsonSerializer<Sex> {
        @Override
        public Sex deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int asInt = json.getAsInt();
            return Sex.create(asInt);
        }

        @Override
        public JsonElement serialize(Sex src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getCode());
        }
    }

    public static class DateAdapter implements JsonDeserializer<java.util.Date>, JsonSerializer<java.util.Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new java.util.Date(json.getAsJsonPrimitive().getAsLong() * 1000);
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime() / 1000);
        }
    }


}
