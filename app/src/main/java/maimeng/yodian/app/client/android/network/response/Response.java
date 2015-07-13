package maimeng.yodian.app.client.android.network.response;

public class Response {
    private int code;
    private String msg;
    public boolean isSuccess(){
        return code==20000;
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
}
