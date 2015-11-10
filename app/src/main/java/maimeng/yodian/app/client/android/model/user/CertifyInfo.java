package maimeng.yodian.app.client.android.model.user;

@org.parceler.Parcel
public class CertifyInfo {

    /**
     * cname : 优点
     * idcard : 412823199101187220
     * mobile : 18516668150
     */

    private String cname;
    private String idcard;
    private String mobile;

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCname() {
        return cname;
    }

    public String getIdcard() {
        return idcard;
    }

    public String getMobile() {
        return mobile;
    }


    public CertifyInfo() {
    }


}
