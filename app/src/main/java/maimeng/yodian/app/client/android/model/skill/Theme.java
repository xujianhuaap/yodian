package maimeng.yodian.app.client.android.model.skill;

import android.os.Parcel;
import android.os.Parcelable;

import maimeng.yodian.app.client.android.network.response.TypeData;

/**
 * Created by android on 15-8-10.
 */
public class Theme implements Parcelable, TypeData {
    private int scid;
    private String name;

    protected Theme(Parcel in) {
        scid = in.readInt();
        name = in.readString();
    }

    public static final Creator<Theme> CREATOR = new Creator<Theme>() {
        @Override
        public Theme createFromParcel(Parcel in) {
            return new Theme(in);
        }

        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };

    public int getScid() {
        return scid;
    }

    public String getName() {
        return name;
    }

    public void setScid(int scid) {
        this.scid = scid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Theme() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(scid);
        dest.writeString(name);
    }
}
