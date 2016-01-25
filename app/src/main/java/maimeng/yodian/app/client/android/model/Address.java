package maimeng.yodian.app.client.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.text.TextUtils;

import org.parceler.Parcel;

import maimeng.yodian.app.client.android.chat.DemoApplication;

/**
 * Created by xujianhua on 06/01/16.
 */
@Parcel(value = Parcel.Serialization.BEAN)
public class Address extends BaseObservable{

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
    public static final String ACCEPT_ADDRESS_SHARE_PREFERENCE_NAME="_accept_address_info";

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

    public static final String KEY_ID="_accept_address_id";
    public static final String KEY_UID="_accept_address_uid";
    public static final String KEY_NAME="_accept_address_name";
    public static final String KEY_MOBILE="_accept_address_mobile";
    public static final String KEY_PROVINCE="_accept_address_province";
    public static final String KEY_CITY="_accept_address_city";
    public static final String KEY_DISRTICT="_accept_address_district";
    public static final String KEY_ADDRESS="_accept_address_address";//详情地址
    public static final String KEY_STATUS="_accept_address_status";
    public static final String KEY_CREATE_TIME="_accept_address_createtime";
    public static final String KEY_UPODATE_TIME="_accept_address_updatetime";


    public void writeAcceptAddress(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(ACCEPT_ADDRESS_SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        write(sharedPreferences);
        DemoApplication.getInstance().setAcceptAddres(this);
    }
    private synchronized boolean write(SharedPreferences sharedPreferences){
        synchronized (Address.class){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putLong(KEY_ID,id);
            editor.putLong(KEY_UID,uid);
            editor.putString(KEY_NAME, name == null ? "" : name);
            editor.putString(KEY_MOBILE,mobile==null?"":mobile);
            editor.putString(KEY_PROVINCE,province==null?"":province);
            editor.putString(KEY_CITY,city==null?"":city);
            editor.putString(KEY_DISRTICT,district==null?"":district);
            editor.putString(KEY_ADDRESS,address==null?"":address);
            editor.putInt(KEY_STATUS, status);
            editor.putLong(KEY_CREATE_TIME, createtime);
            editor.putLong(KEY_UPODATE_TIME,updatetime);
            editor.apply();
            return true;
        }

    }


    public static Address readAcceptAddress(Context context){
        Address acceptAddres=DemoApplication.getInstance().getAcceptAddres();
        if(acceptAddres!=null&& !TextUtils.isEmpty(acceptAddres.getProvince())){
            return acceptAddres;
        }
        SharedPreferences sharedPreferences=context.getSharedPreferences(ACCEPT_ADDRESS_SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Address acceptAddressInfo=read(sharedPreferences);
        DemoApplication.getInstance().setAcceptAddres(acceptAddressInfo);
        return acceptAddressInfo;
    }

    private static synchronized Address read(SharedPreferences sharedPreferences){
        synchronized (Address.class){
            long id=sharedPreferences.getLong(KEY_ID,0);
            long uid=sharedPreferences.getLong(KEY_UID,0);
            String name=sharedPreferences.getString(KEY_NAME, "");
            String mobile=sharedPreferences.getString(KEY_MOBILE,"");
            String province=sharedPreferences.getString(KEY_PROVINCE, "");
            String city=sharedPreferences.getString(KEY_CITY, "");
            String distirct=sharedPreferences.getString(KEY_DISRTICT, "");
            String address=sharedPreferences.getString(KEY_ADDRESS, "");
            int status=sharedPreferences.getInt(KEY_STATUS, 0);
            long create_time=sharedPreferences.getLong(KEY_CREATE_TIME, 0);
            long update_time=sharedPreferences.getLong(KEY_UPODATE_TIME,0);
            Address acceptAddressInfo=new Address(id,uid,name,mobile,province,city,distirct,address,status,create_time,update_time);
            return acceptAddressInfo;
        }
    }

    public static boolean clear(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(ACCEPT_ADDRESS_SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        return true;
    }


    public Address(long id, long uid, String name, String mobile, String province, String city, String district, String address, int status, long createtime, long updatetime) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.province = province;
        this.city = city;
        this.district = district;
        this.address = address;
        this.status = status;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }

    public Address() {
    }
}
