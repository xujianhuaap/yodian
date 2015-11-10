package maimeng.yodian.app.client.android.view.deal.pay;

/**
 * Created by xujianhua on 11/10/15.
 */
public enum CertifyStatus {
    PASS(1,"审核通过"),
    Failure(0,"未通过");


    private int value;
    private String name;

    CertifyStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static CertifyStatus create(int value){
        switch (value){
            case 1:
                return PASS;
            case 0:
                return Failure;
            default:
                return null;
        }
    }
}
