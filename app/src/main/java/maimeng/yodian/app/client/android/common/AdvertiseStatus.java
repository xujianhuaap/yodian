package maimeng.yodian.app.client.android.common;

import io.realm.RealmObject;

/**
 * Created by android on 2015/11/18.
 */
public class AdvertiseStatus extends RealmObject {
    private String pic;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    private boolean show;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
