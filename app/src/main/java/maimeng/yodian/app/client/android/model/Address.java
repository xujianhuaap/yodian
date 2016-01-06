package maimeng.yodian.app.client.android.model;

import org.parceler.Parcel;

/**
 * Created by xujianhua on 06/01/16.
 */
@Parcel(value = Parcel.Serialization.BEAN)
public class Address {

    /**
     * id : 1
     * uid : 17
     * name : 简
     * mobile : 18516668150
     * province : 辽宁省
     * city : 大连市
     * district : 甘井子区
     * address : 啦啦啦
     * status : 0
     * createtime : 1452051161
     * updatetime : 1452051161
     */

    private long id;
    private long uid;
    private String name;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String address;
    private int status;
    private long createtime;
    private long updatetime;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }


}
