package maimeng.yodian.app.client.android.network.common;

import android.content.Context;
import android.text.TextUtils;

import org.henjue.library.hnet.RequestFacade;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.utils.MD5Util;

/**
 * Created by android on 15-6-25.
 */
public class RequestIntercept implements org.henjue.library.hnet.RequestIntercept {
    private final Context mContext;

    public RequestIntercept(Context context) {
        this.mContext = context;

    }

    private ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();

    @Override
    public void onComplite(RequestFacade request) {

        /**
         * 添加一些任何任何接口都必须有的参数
         */
        if (!params.containsKey("versionName")) {
            request.add("versionName", String.valueOf(YApplication.versionName));
        }
        if (!params.containsKey("versionCode")) {
            request.add("versionCode", String.valueOf(YApplication.versionCode));
        }
        if (!params.containsKey("channelType")) {
            request.add("channelType", YApplication.channelId);
        }
        request.add("clientType", 2);
        if (mContext != null) {
            User auth = User.read(mContext);
            String token = auth.getToken();
            if (!TextUtils.isEmpty(token)) {
                if (!params.containsKey("SN_KEY_API")) {
                    request.add("SN_KEY_API", token);
                } else {
                    LogUtil.i("RequestIntercept", "SN_KEY_API exists for params,%s", params.get("SN_KEY_API"));
                }
            } else {
                LogUtil.i("RequestIntercept", "local token is null");
            }
        }


//        String key = getKey(params);
//        if (key != null) {
//            params.put("key", key);
//        }
//        request.add("key", key);
        LogUtil.i("RequestIntercept", "Print %s Params Start:", request.getPath());
        for (String key : params.keySet()) {
            LogUtil.i("RequestIntercept", "%s:%s", key, params.get(key));
        }
        LogUtil.i("RequestIntercept", "Print Params End.\n");
    }

    private String getKey(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
        sortMap.putAll(params);
        StringBuffer sb = new StringBuffer();

        for (String key : sortMap.keySet()) {
            sb.append(sortMap.get(key)).append("&");
        }
        String plaintext = sb.toString();
        plaintext = plaintext.endsWith("&") ? plaintext.substring(0, plaintext.length() - 1) : plaintext;
        return MD5Util.MD5(plaintext);
    }

    @Override
    public void onStart(RequestFacade request) {
        params.clear();


    }

    @Override
    public void onAdd(String name, Object value) {
        if (value instanceof String || value instanceof Integer || value instanceof Boolean || value instanceof Long || value instanceof Float) {
            params.put(name, String.valueOf(value));
        }
    }

    public class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
