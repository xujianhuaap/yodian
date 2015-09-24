package maimeng.yodian.app.client.android.model.remainder;

import java.util.Date;

import maimeng.yodian.app.client.android.view.deal.BindStatus;

/**
 * Created by android on 15-9-23.
 */
public class BindBank {
    private long bindId;
    private long uid;
    private int cerif_type;//证件类型:1身份证
    private int why_id;//拒绝原因id
    private String backway;//拒绝原因
    private BindStatus status;
    private Date createtime;
    private long bankId;
    private String bankName;

    public long getBindId() {
        return bindId;
    }

    public void setBindId(long bindId) {
        this.bindId = bindId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getCerif_type() {
        return cerif_type;
    }

    public void setCerif_type(int cerif_type) {
        this.cerif_type = cerif_type;
    }

    public int getWhy_id() {
        return why_id;
    }

    public void setWhy_id(int why_id) {
        this.why_id = why_id;
    }

    public String getBackway() {
        return backway;
    }

    public void setBackway(String backway) {
        this.backway = backway;
    }

    public BindStatus getStatus() {
        return status;
    }

    public void setStatus(BindStatus status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public long getId() {
        return bankId;
    }

    public void setId(long id) {
        this.bankId = id;
    }


    private String number = "6214830296846172";
    private String branch;
    private String number2;
    private String username;
    private String idcard;
    private String phone;
    private String valicode;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getValicode() {
        return valicode;
    }

    public void setValicode(String valicode) {
        this.valicode = valicode;
    }

}
