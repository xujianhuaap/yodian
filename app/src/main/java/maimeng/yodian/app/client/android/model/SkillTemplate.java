package maimeng.yodian.app.client.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import maimeng.yodian.app.client.android.BR;
@DatabaseTable(tableName = "_skillTemplate")
public class SkillTemplate extends BaseObservable implements Parcelable {

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
    private String price;
    @DatabaseField(columnName = "_unit")
    private String unit;
    @DatabaseField(columnName = "_status")
    private int status;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.pic);
        dest.writeString(this.content);
        dest.writeString(this.price);
        dest.writeString(this.unit);
        dest.writeInt(this.status);
        dest.writeLong(createtime != null ? createtime.getTime() : -1);
    }

    public SkillTemplate() {
    }

    protected SkillTemplate(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.pic = in.readString();
        this.content = in.readString();
        this.price = in.readString();
        this.unit = in.readString();
        this.status = in.readInt();
        long tmpCreatetime = in.readLong();
        this.createtime = tmpCreatetime == -1 ? null : new Date(tmpCreatetime);
    }

    public static final Parcelable.Creator<SkillTemplate> CREATOR = new Parcelable.Creator<SkillTemplate>() {
        public SkillTemplate createFromParcel(Parcel source) {
            return new SkillTemplate(source);
        }

        public SkillTemplate[] newArray(int size) {
            return new SkillTemplate[size];
        }
    };
}
