package maimeng.yodian.app.client.android.model.skill;

@org.parceler.Parcel
public class Head {
    private long value;
    private String pic;

    public long getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
