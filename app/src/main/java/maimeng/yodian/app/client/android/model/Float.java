package maimeng.yodian.app.client.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 2015/10/23.
 */
@org.parceler.Parcel
public class Float {
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

    public Float() {
    }

}
