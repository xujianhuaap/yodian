package maimeng.yodian.app.client.android.model.skill;

import org.parceler.Parcel;

@org.parceler.Parcel(value = Parcel.Serialization.BEAN)
public class Theme {
    private int scid;
    private String name;

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

}
