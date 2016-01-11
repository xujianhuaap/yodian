package maimeng.yodian.app.client.android.model;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by xujianhua on 11/01/16.
 */
@Parcel(Parcel.Serialization.BEAN)
public class BalanceInfo {

    private long id;
    private long uid;
    private int type;
    private float money;
    private long srcid;
    private int status;
    private Date createtime;
    private Date order_createtime;
    private String name;
    private String ostatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public long getSrcid() {
        return srcid;
    }

    public void setSrcid(long srcid) {
        this.srcid = srcid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getOrder_createtime() {
        return order_createtime;
    }

    public void setOrder_createtime(Date order_createtime) {
        this.order_createtime = order_createtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOstatus() {
        return ostatus;
    }

    public void setOstatus(String ostatus) {
        this.ostatus = ostatus;
    }
}
