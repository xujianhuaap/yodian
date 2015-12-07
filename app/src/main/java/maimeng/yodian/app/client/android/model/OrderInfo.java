package maimeng.yodian.app.client.android.model;

import org.parceler.Parcel;

import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.Buyer;

/**
 * Created by xujianhua on 9/28/15.
 */
@Parcel(value = Parcel.Serialization.BEAN)
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
    private long seller_id;
    private long uid;
    private long sid;
    private long scid;
    private long pay_time;
    private long accept_time;
    private long send_time;
    private long difftime; //
    private long endtime;
    private long systime;
    private long confirm_time;
    private float total_fee;
    private float real_fee;
    private String skill_name;
    private int paytype;
    private int status;
    private long createtime;
    private long oid;

    private Skill skill;
    private Buyer buyer;
    private float balance;

    public long getDifftime() {
        return difftime;
    }

    public void setDifftime(long difftime) {
        this.difftime = difftime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getSystime() {
        return systime;
    }

    public void setSystime(long systime) {
        this.systime = systime;
    }

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

    public long getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(long seller_id) {
        this.seller_id = seller_id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public long getScid() {
        return scid;
    }

    public void setScid(long scid) {
        this.scid = scid;
    }

    public long getPay_time() {
        return pay_time;
    }

    public void setPay_time(long pay_time) {
        this.pay_time = pay_time;
    }

    public long getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(long accept_time) {
        this.accept_time = accept_time;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public long getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(long confirm_time) {
        this.confirm_time = confirm_time;
    }

    public float getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(float total_fee) {
        this.total_fee = total_fee;
    }

    public float getReal_fee() {
        return real_fee;
    }

    public void setReal_fee(float real_fee) {
        this.real_fee = real_fee;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
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
