package maimeng.yodian.app.client.android.model.skill;

/**
 * Created by android on 8/20/15.
 */
public class Banner {
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
}