package maimeng.yodian.app.client.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.Date;

import maimeng.yodian.app.client.android.BR;

/**
 * Created by android on 15-7-14.
 */
public class Skill extends BaseObservable{
    private long sid;//技能id
    private String name;//技能名称
    private String pic;//技能图片
    private String content;//技能描述
    private String price;//技能价格
    private String unit;//技能价格单位
    private int status;//2下架状态，0上架状态
    private Date createtime;//技能添加时间戳
    private String nickname;//用户昵称
    private String avatar;//用户头像数据

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }
    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    @Bindable
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
}
