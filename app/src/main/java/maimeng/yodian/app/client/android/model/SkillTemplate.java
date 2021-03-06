package maimeng.yodian.app.client.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.parceler.Parcel;

import java.util.Date;

import maimeng.yodian.app.client.android.BR;

@Parcel(value = Parcel.Serialization.BEAN)
@DatabaseTable(tableName = "_skillTemplate")
public class SkillTemplate extends BaseObservable {

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SerializedName("tid")
    @DatabaseField(columnName = "_id")
    private long id;
    @DatabaseField(columnName = "_name")
    private String name;
    @Bindable
    @DatabaseField(columnName = "_pic")
    private String pic;
    @DatabaseField(columnName = "_content")
    private String content;
    @DatabaseField(columnName = "_price")
    private float price;
    @DatabaseField(columnName = "_unit")
    private String unit;
    @DatabaseField(columnName = "_status")
    private boolean status;
    @DatabaseField(columnName = "_createtime")
    private Date createtime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
        notifyPropertyChanged(BR.pic);
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
