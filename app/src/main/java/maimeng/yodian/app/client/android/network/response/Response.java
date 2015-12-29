package maimeng.yodian.app.client.android.network.response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.auth.AuthRedirect;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;

public class Response {
    private int code;
    private String msg;

    public boolean isSuccess() {
        return code == 20000;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void showMessage(Context context) {
        if (context!=null && !TextUtils.isEmpty(msg)) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean isValidateAuth(Activity context) {
        return isValidateAuth(context, -1);
    }
    public boolean isValidateAuth(Activity context, int requestCode) {
        if(context==null){
            LogUtil.w(this.getClass().getSimpleName(),"isValidateAuth(),context is null");
            return true;
        }
        if (code == 10011) {
            User.clear(context);
            AuthRedirect.toAuth(context,requestCode);
            return false;
        } else if(code==10036) {
            showMessage(context);
            AuthRedirect.toAuth(context);
            return false;
        }else{
            return true;
        }
    }

}
