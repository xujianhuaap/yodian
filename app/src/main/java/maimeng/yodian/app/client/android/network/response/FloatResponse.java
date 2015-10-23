package maimeng.yodian.app.client.android.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import maimeng.yodian.app.client.android.model.Float;

/**
 * Created by android on 2015/10/23.
 */
public class FloatResponse extends Response {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("floatad")
        private ArrayList<maimeng.yodian.app.client.android.model.Float> floatPic = new ArrayList<maimeng.yodian.app.client.android.model.Float>();

        public ArrayList<maimeng.yodian.app.client.android.model.Float> getFloatPic() {
            return floatPic;
        }

        public void setFloatPic(ArrayList<Float> floatPic) {
            this.floatPic = floatPic;
        }
    }

}
