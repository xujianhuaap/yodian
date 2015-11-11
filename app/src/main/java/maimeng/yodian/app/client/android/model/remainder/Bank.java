package maimeng.yodian.app.client.android.model.remainder;

import com.google.gson.annotations.SerializedName;

@org.parceler.Parcel
public class Bank {
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

    public Bank() {
    }

}
