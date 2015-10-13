package maimeng.yodian.app.client.android.model.skill;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 8/20/15.
 */
public class Banner implements Parcelable {

    /**
     * 1:网址;2用户;3技能
     */
    private int type;
    /**
     * 1的时候是网址，2或3的时候是id(long)
     */

    private String value;
    private String pic;

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

    public Banner() {
    }

    protected Banner(Parcel in) {
        this.type = in.readInt();
        this.value = in.readString();
        this.pic = in.readString();
    }

    public static final Parcelable.Creator<Banner> CREATOR = new Parcelable.Creator<Banner>() {
        public Banner createFromParcel(Parcel source) {
            return new Banner(source);
        }

        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };
}
