package maimeng.yodian.app.client.android.model.skill;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.databings.ImageBindable;
import maimeng.yodian.app.client.android.network.loader.Circle;

/**
 * Created by xujianhua on 9/29/15.
 */
public class Skill  implements Parcelable{

    /**
     * id : 72
     * uid : 18
     * type : 1
     * scid : 6
     * name : 订制叫醒
     * pic : http://skilltest.oss-cn-hangzhou.aliyuncs.com/files/image/20150710/559f35c066607.jpg
     * content : 百分百人工叫醒服务/世界早上好/起床气再见/世界早安，凌晨的飞机、早上的会议、下午的纪念日，最暖心的提醒，让我们一睁眼就是美好世界
     * price : 0.01
     * unit : 次
     * contentcount : 2
     * daycontentcount : 0
     * daycontentstatus : 0
     * sharecount : 4
     * report_count : 0
     * daysharecount : 1
     * daysharestatus : 0
     * qrcode : http://skilltest.oss-cn-hangzhou.aliyuncs.com/qrcode/2015/07/10/8d2fe946bf3d8390141fdc7bab7c7a22.png
     * qrcodeUrl : http://share.yodian.me/skill/share/72/18
     * elitetime : 1437386519
     * ord : 102
     * smartsort : 0.66
     * allow_sell : 1
     * status : 0
     * createtime : 1436497344
     * nickname : 清风神仙画中飞
     * avatar : http://skilltest.oss-cn-hangzhou.aliyuncs.com/files/image/20150709/559e363738d9c.jpg
     * weichat : guongguangming
     * hxname : hx_18
     * sid : 72
     * qq : 222
     * contact : 18621761048
     * sex : 1
     * signature : 人人都是生活家，好好爱国哦
     * province : 上海
     * city : 上海市
     * district : 静安区
     * address :
     * saddress : 上海上海市静安区
     */

    private long id;
    private long uid;
    private String type;
    private long scid;
    private String name;
    private String pic;
    private String content;
    private String price;
    private String unit;
    private String contentcount;
    private String daycontentcount;
    private String daycontentstatus;
    private String sharecount;
    private String report_count;
    private String daysharecount;
    private String daysharestatus;
    private String qrcode;
    private String qrcodeUrl;
    private String elitetime;
    private String ord;
    private String smartsort;
    private String allow_sell;
    private int status;
    private Date createtime;
    private String nickname;
    private String avatar;
    private String weichat;
    private String sid;
    private String qq;
    private String contact;
    private String sex;
    private String signature;
    private String province;
    private String city;
    private String district;
    private String address;
    private String saddress;
    @SerializedName("hxname")
    private String chatLoginName = "";

    public String getChatLoginName() {
        return chatLoginName;
    }

    public void setChatLoginName(String chatLoginName) {
        this.chatLoginName = chatLoginName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setScid(long scid) {
        this.scid = scid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setContentcount(String contentcount) {
        this.contentcount = contentcount;
    }

    public void setDaycontentcount(String daycontentcount) {
        this.daycontentcount = daycontentcount;
    }

    public void setDaycontentstatus(String daycontentstatus) {
        this.daycontentstatus = daycontentstatus;
    }

    public void setSharecount(String sharecount) {
        this.sharecount = sharecount;
    }

    public void setReport_count(String report_count) {
        this.report_count = report_count;
    }

    public void setDaysharecount(String daysharecount) {
        this.daysharecount = daysharecount;
    }

    public void setDaysharestatus(String daysharestatus) {
        this.daysharestatus = daysharestatus;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public void setElitetime(String elitetime) {
        this.elitetime = elitetime;
    }

    public void setOrd(String ord) {
        this.ord = ord;
    }

    public void setSmartsort(String smartsort) {
        this.smartsort = smartsort;
    }

    public void setAllow_sell(String allow_sell) {
        this.allow_sell = allow_sell;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setWeichat(String weichat) {
        this.weichat = weichat;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public long getId() {
        return id;
    }

    public long getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public long getScid() {
        return scid;
    }

    public String getName() {
        return name;
    }

    public String getPic() {
        return pic;
    }

    public String getContent() {
        return content;
    }

    public String getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public String getContentcount() {
        return contentcount;
    }

    public String getDaycontentcount() {
        return daycontentcount;
    }

    public String getDaycontentstatus() {
        return daycontentstatus;
    }

    public String getSharecount() {
        return sharecount;
    }

    public String getReport_count() {
        return report_count;
    }

    public String getDaysharecount() {
        return daysharecount;
    }

    public String getDaysharestatus() {
        return daysharestatus;
    }

    public String getQrcode() {
        return qrcode;
    }

    public String getQrcodeUrl() {
        return BuildConfig.DEBUG ? qrcodeUrl == null ? "" : qrcodeUrl.replaceAll("^http://share.yodian.me/", "http://share.yodian.me/") : qrcodeUrl;

    }

    public String getElitetime() {
        return elitetime;
    }

    public String getOrd() {
        return ord;
    }

    public String getSmartsort() {
        return smartsort;
    }

    public String getAllow_sell() {
        return allow_sell;
    }

    public int getStatus() {
        return status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getWeichat() {
        return weichat;
    }


    public String getSid() {
        return sid;
    }

    public String getQq() {
        return qq;
    }

    public String getContact() {
        return contact;
    }

    public String getSex() {
        return sex;
    }

    public String getSignature() {
        return signature;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public String getSaddress() {
        return saddress;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.uid);
        dest.writeString(this.type);
        dest.writeLong(this.scid);
        dest.writeString(this.name);
        dest.writeString(this.pic);
        dest.writeString(this.content);
        dest.writeString(this.price);
        dest.writeString(this.unit);
        dest.writeString(this.contentcount);
        dest.writeString(this.daycontentcount);
        dest.writeString(this.daycontentstatus);
        dest.writeString(this.sharecount);
        dest.writeString(this.report_count);
        dest.writeString(this.daysharecount);
        dest.writeString(this.daysharestatus);
        dest.writeString(this.qrcode);
        dest.writeString(this.qrcodeUrl);
        dest.writeString(this.elitetime);
        dest.writeString(this.ord);
        dest.writeString(this.smartsort);
        dest.writeString(this.allow_sell);
        dest.writeInt(this.status);
        dest.writeLong(createtime != null ? createtime.getTime() : -1);
        dest.writeString(this.nickname);
        dest.writeString(this.avatar);
        dest.writeString(this.weichat);
        dest.writeString(this.sid);
        dest.writeString(this.qq);
        dest.writeString(this.contact);
        dest.writeString(this.sex);
        dest.writeString(this.signature);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.address);
        dest.writeString(this.saddress);
        dest.writeString(this.chatLoginName);
    }

    public Skill() {
    }

    protected Skill(Parcel in) {
        this.id = in.readLong();
        this.uid = in.readLong();
        this.type = in.readString();
        this.scid = in.readLong();
        this.name = in.readString();
        this.pic = in.readString();
        this.content = in.readString();
        this.price = in.readString();
        this.unit = in.readString();
        this.contentcount = in.readString();
        this.daycontentcount = in.readString();
        this.daycontentstatus = in.readString();
        this.sharecount = in.readString();
        this.report_count = in.readString();
        this.daysharecount = in.readString();
        this.daysharestatus = in.readString();
        this.qrcode = in.readString();
        this.qrcodeUrl = in.readString();
        this.elitetime = in.readString();
        this.ord = in.readString();
        this.smartsort = in.readString();
        this.allow_sell = in.readString();
        this.status = in.readInt();
        long tmpCreatetime = in.readLong();
        this.createtime = tmpCreatetime == -1 ? null : new Date(tmpCreatetime);
        this.nickname = in.readString();
        this.avatar = in.readString();
        this.weichat = in.readString();
        this.sid = in.readString();
        this.qq = in.readString();
        this.contact = in.readString();
        this.sex = in.readString();
        this.signature = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.address = in.readString();
        this.saddress = in.readString();
        this.chatLoginName = in.readString();
    }

    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        public Skill createFromParcel(Parcel source) {
            return new Skill(source);
        }

        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };

    public void update(Skill skill) {
        setUid(skill.uid);
        setName(skill.name);
        setPic(skill.pic);
        setContent(skill.content);
        setPrice(skill.price);
        setUnit(skill.unit);
        setStatus(skill.status);
        setCreatetime(skill.createtime);
        setNickname(skill.nickname);
        setAvatar(skill.avatar);

    }

    public ImageBindable getAvatar80() {
        ImageBindable img = new ImageBindable();
        if (avatar != null) {
            final int i = avatar.lastIndexOf(".");
            if (i != -1) {
                String url = avatar.substring(0, i);
                String suffix = avatar.substring(i, avatar.length());
                img = new ImageBindable().setUri(Uri.parse(url + "_80x80" + suffix)).setCircle(Circle.obtain().setBorderSize(3));
            }
        }
        return img;
    }
}
