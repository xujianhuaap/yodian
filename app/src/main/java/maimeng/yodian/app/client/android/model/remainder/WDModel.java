package maimeng.yodian.app.client.android.model.remainder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by android on 2015/10/21.
 */
public class WDModel {


    /**
     * id : 99
     * withdraw_code : 20151109172239817788
     * money : 100
     * money_read : 100.00
     * uid : 82
     * alipay_id : 4
     * card_id : 0
     * why_id :
     * backwhy :
     * audit_time : 0
     * status : 0
     * createtime : 1447060959
     * alipay : l2Zs
     */

    private String id;
    private String withdraw_code;
    private String money;
    private String money_read;
    private String uid;
    private String alipay_id;
    private String card_id;
    private String why_id;
    private String backwhy;
    private String audit_time;
    private int status;
    private Date createtime;
    private String alipay;

    public void setId(String id) {
        this.id = id;
    }

    public void setWithdraw_code(String withdraw_code) {
        this.withdraw_code = withdraw_code;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setMoney_read(String money_read) {
        this.money_read = money_read;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAlipay_id(String alipay_id) {
        this.alipay_id = alipay_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public void setWhy_id(String why_id) {
        this.why_id = why_id;
    }

    public void setBackwhy(String backwhy) {
        this.backwhy = backwhy;
    }

    public void setAudit_time(String audit_time) {
        this.audit_time = audit_time;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getId() {
        return id;
    }

    public String getWithdraw_code() {
        return withdraw_code;
    }

    public String getMoney() {
        return money;
    }

    public String getMoney_read() {
        return money_read;
    }

    public String getUid() {
        return uid;
    }

    public String getAlipay_id() {
        return alipay_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public String getWhy_id() {
        return why_id;
    }

    public String getBackwhy() {
        return backwhy;
    }

    public String getAudit_time() {
        return audit_time;
    }

    public int getStatus() {
        return status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public String getAlipay() {
        return alipay;
    }


    public WDModel() {
    }

}
