package maimeng.yodian.app.client.android.model;

import java.util.Date;

/**
 * Created by android on 2015/8/24.
 */
@org.parceler.Parcel
public class Version {

    /**
     * datetime : 0
     * os : 2
     * force_version :
     * id : 2
     * type : 1
     * version : 1.0.1
     * content : 更拽更炫酷的新版本，                           更新进入精彩の优点世界
     * url :
     */
    private Date datetime;
    private int os;
    private String force_version;
    private long id;
    private int type;//1强制更新
    private String version;
    private String content;
    private String url;

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public String getForce_version() {
        return force_version;
    }

    public void setForce_version(String force_version) {
        this.force_version = force_version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
