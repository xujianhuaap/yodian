package maimeng.yodian.app.client.android2.network.response;

import android.content.Context;
import android.widget.Toast;

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
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
