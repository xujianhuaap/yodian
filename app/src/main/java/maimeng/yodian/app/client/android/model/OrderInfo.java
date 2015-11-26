package maimeng.yodian.app.client.android.model;

import org.parceler.Parcel;

import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.Buyer;

/**
 * Created by xujianhua on 9/28/15.
 */
@org.parceler.Parcel(value = Parcel.Serialization.BEAN)
public class OrderInfo {

    /**
     * id : 174
     * number : 20150929103630791971
     * seller_id : 18
     * uid : 74
     * sid : 72
     * scid : 6
     * pay_time : 1443494235
     * accept_time : 0
     * send_time : 0
     * confirm_time : 0
     * total_fee : 0.01
     * real_fee : lAEJfUFkEyuC5e2k1VxO9IIVZobAKOuUL6ezV8jZDrE=
     * skill_name : 订制叫醒
     * paytype : 1
     * status : 2
     * createtime : 1443494190
     * oid : 174
     */

    private String id;
    private String number;
    private String seller_id;
    private String uid;
    private String sid;
    private String scid;
    private String pay_time;
    private String accept_time;
    private String send_time;
    private String confirm_time;
    private String total_fee;
    private String real_fee;
    private String skill_name;
    private String paytype;
    private String status;
    private String createtime;
    private long oid;

    private Skill skill;
    private Buyer buyer;
    private float balance;

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getScid() {
        return scid;
    }

    public void setScid(String scid) {
        this.scid = scid;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(String accept_time) {
        this.accept_time = accept_time;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(String confirm_time) {
        this.confirm_time = confirm_time;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getReal_fee() {
        return real_fee;
    }

    public void setReal_fee(String real_fee) {
        this.real_fee = real_fee;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }


}
