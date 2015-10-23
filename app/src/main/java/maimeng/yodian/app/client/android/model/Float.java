package maimeng.yodian.app.client.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 2015/10/23.
 */
public class Float implements Parcelable {
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    private String value;
    private String pic;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.value);
        dest.writeString(this.pic);
    }

    public Float() {
    }

    protected Float(Parcel in) {
        this.type = in.readInt();
        this.value = in.readString();
        this.pic = in.readString();
    }

    public static final Parcelable.Creator<Float> CREATOR = new Parcelable.Creator<Float>() {
        public Float createFromParcel(Parcel source) {
            return new Float(source);
        }

        public Float[] newArray(int size) {
            return new Float[size];
        }
    };
}
