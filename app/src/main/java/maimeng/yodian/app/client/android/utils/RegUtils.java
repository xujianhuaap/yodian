package maimeng.yodian.app.client.android.utils;

import android.content.Context;
import android.text.TextUtils;

import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.regex.Pattern;

/**
 * Created by android on 16-1-5.
 */
public class RegUtils {
    public static boolean isMobile(Context context, String txt){
        String reg_mobile = OnlineConfigAgent.getInstance().getConfigParams(context, "reg_mobile");
        if(TextUtils.isEmpty(reg_mobile)){
            OnlineConfigAgent.getInstance().updateOnlineConfig(context);
        }
        reg_mobile = OnlineConfigAgent.getInstance().getConfigParams(context, "reg_mobile");
        if(TextUtils.isEmpty(reg_mobile)){
            return false;
        }
        Pattern p = Pattern.compile(reg_mobile);
        return p.matcher(txt).matches();
    }
}
