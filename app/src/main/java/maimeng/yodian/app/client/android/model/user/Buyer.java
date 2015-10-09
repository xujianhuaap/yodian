package maimeng.yodian.app.client.android.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xujianhua on 9/29/15.
 */
public class Buyer implements Parcelable{

    /**
     * id : 74
     * mobile :
     * nickname : 阿乐控_726
     * avatar : http://skilltest.oss-cn-hangzhou.aliyuncs.com/files/image/20150925/5604bc47aaf66.jpg
     * weichat : 1111233
     * eclose : 0
     * etoken :
     * etype : 2
     * type : 1
     * token : 2.002C9quCVN_xAC1d57c4ae49OfScUE
     * usid : 2672241313
     * shielding : 0
     * skillcount : 0
     * report_count : 0
     * hxname : hx_74
     * qq :
     * contact :
     * sex : 0
     * signature :
     * job :
     * province :
     * city : null
     * district :
     * address :
     * logincount : 1
     * logincountstatus : 1
     * status : 0
     * createtime : 1443150870
     * saddress :
     */

    private String id;
    private String mobile;
    private String nickname;
    private String avatar;
    private String weichat;
    private String eclose;
    private String etoken;
    private String etype;
    private String type;
    private String token;
    private String usid;
    private String shielding;
    private String skillcount;
    private String report_count;
    private String hxname;
    private String qq;
    private String contact;
    private String sex;
    private String signature;
    private String job;
    private String province;
    private String city;
    private String district;
    private String address;
    private String logincount;
    private String logincountstatus;
    private String status;
    private String createtime;
    private String saddress;


    public String getSaddress() {
        return saddress;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWeichat() {
        return weichat;
    }

    public void setWeichat(String weichat) {
        this.weichat = weichat;
    }

    public String getEclose() {
        return eclose;
    }

    public void setEclose(String eclose) {
        this.eclose = eclose;
    }

    public String getEtoken() {
        return etoken;
    }

    public void setEtoken(String etoken) {
        this.etoken = etoken;
    }

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsid() {
        return usid;
    }

    public void setUsid(String usid) {
        this.usid = usid;
    }

    public String getShielding() {
        return shielding;
    }

    public void setShielding(String shielding) {
        this.shielding = shielding;
    }

    public String getSkillcount() {
        return skillcount;
    }

    public void setSkillcount(String skillcount) {
        this.skillcount = skillcount;
    }

    public String getReport_count() {
        return report_count;
    }

    public void setReport_count(String report_count) {
        this.report_count = report_count;
    }

    public String getHxname() {
        return hxname;
    }

    public void setHxname(String hxname) {
        this.hxname = hxname;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogincount() {
        return logincount;
    }

    public void setLogincount(String logincount) {
        this.logincount = logincount;
    }

    public String getLogincountstatus() {
        return logincountstatus;
    }

    public void setLogincountstatus(String logincountstatus) {
        this.logincountstatus = logincountstatus;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.mobile);
        dest.writeString(this.nickname);
        dest.writeString(this.avatar);
        dest.writeString(this.weichat);
        dest.writeString(this.eclose);
        dest.writeString(this.etoken);
        dest.writeString(this.etype);
        dest.writeString(this.type);
        dest.writeString(this.token);
        dest.writeString(this.usid);
        dest.writeString(this.shielding);
        dest.writeString(this.skillcount);
        dest.writeString(this.report_count);
        dest.writeString(this.hxname);
        dest.writeString(this.qq);
        dest.writeString(this.contact);
        dest.writeString(this.sex);
        dest.writeString(this.signature);
        dest.writeString(this.job);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.address);
        dest.writeString(this.logincount);
        dest.writeString(this.logincountstatus);
        dest.writeString(this.status);
        dest.writeString(this.createtime);
        dest.writeString(this.saddress);
    }

    public Buyer() {
    }

    protected Buyer(Parcel in) {
        this.id = in.readString();
        this.mobile = in.readString();
        this.nickname = in.readString();
        this.avatar = in.readString();
        this.weichat = in.readString();
        this.eclose = in.readString();
        this.etoken = in.readString();
        this.etype = in.readString();
        this.type = in.readString();
        this.token = in.readString();
        this.usid = in.readString();
        this.shielding = in.readString();
        this.skillcount = in.readString();
        this.report_count = in.readString();
        this.hxname = in.readString();
        this.qq = in.readString();
        this.contact = in.readString();
        this.sex = in.readString();
        this.signature = in.readString();
        this.job = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.address = in.readString();
        this.logincount = in.readString();
        this.logincountstatus = in.readString();
        this.status = in.readString();
        this.createtime = in.readString();
        this.saddress = in.readString();
    }

    public static final Creator<Buyer> CREATOR = new Creator<Buyer>() {
        public Buyer createFromParcel(Parcel source) {
            return new Buyer(source);
        }

        public Buyer[] newArray(int size) {
            return new Buyer[size];
        }
    };
}