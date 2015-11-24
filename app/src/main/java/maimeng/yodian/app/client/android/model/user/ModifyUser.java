package maimeng.yodian.app.client.android.model.user;

import org.parceler.Parcel;

@Parcel(value = Parcel.Serialization.BEAN)
public class ModifyUser {
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;
}
