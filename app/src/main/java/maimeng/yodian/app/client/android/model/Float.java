package maimeng.yodian.app.client.android.model;

import org.parceler.Parcel;

/**
 * Created by android on 2015/10/23.
 */
@Parcel(value = Parcel.Serialization.BEAN)
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
