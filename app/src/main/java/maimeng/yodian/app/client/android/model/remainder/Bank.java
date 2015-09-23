package maimeng.yodian.app.client.android.model.remainder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 15-9-23.
 */
public class Bank implements Parcelable {
    @SerializedName("bank_id")
    private long id;
    @SerializedName("bankname")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
    }

    public Bank() {
    }

    protected Bank(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Bank> CREATOR = new Parcelable.Creator<Bank>() {
        public Bank createFromParcel(Parcel source) {
            return new Bank(source);
        }

        public Bank[] newArray(int size) {
            return new Bank[size];
        }
    };
}
