package maimeng.yodian.app.client.android.model.remainder;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import maimeng.yodian.app.client.android.view.deal.BindStatus;

/**
 * Created by android on 15-9-23.
 */
public class BindBank {


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

    @SerializedName("id")
    private long bindId;
    private long uid;
    private int cerif_type;//证件类型:1身份证
    private int why_id;//拒绝原因id
    private String backway;//拒绝原因
    private BindStatus status;
    private Date createtime;
    @SerializedName("bank_id")
    private long bankId;
    @SerializedName("bankname")
    private String bankName;
    //    private String number = "6214830296846172";
    @SerializedName("card_no")
    private String number = "";
    @SerializedName("sub_branch_name")
    private String branch;
    private String number2;//添加银行卡绑定的时候第二遍输入银行卡号
    @SerializedName("real_name")
    private String username;
    @SerializedName("certif_no")
    private String idcard;
    @SerializedName("phone_no")
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

    public String getNumberX() {
        if (number == null) {
            return null;
        }
        if (number.length() > 8) {
            StringBuffer sb = new StringBuffer();
            sb.append(number.substring(0, 4));
            sb.append("*******");
            sb.append(number.substring(11));
            return sb.toString();
        }
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

    public String getPhoneX() {
        if (phone == null) {
            return null;
        }
        if (phone.length() > 7) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
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

    public String getStatusText() {
        if (status == BindStatus.CANCEL) {
            return "申请已取消";
        } else if (status == BindStatus.PASS) {
            return "已绑定银行卡";
        } else if (status == BindStatus.DENY) {
            return backway;
        } else if (status == BindStatus.NO_CARD) {
            return "未绑定银行卡";
        } else if (status == BindStatus.WAITCONFIRM) {
            return "绑定审核中...";
        }
        return "状态未知";
    }

}
