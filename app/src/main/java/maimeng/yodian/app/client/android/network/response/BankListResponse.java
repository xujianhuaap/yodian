package maimeng.yodian.app.client.android.network.response;

import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.model.remainder.Bank;

/**
 * Created by android on 15-9-23.
 */
public class BankListResponse extends Response {
    private ArrayList<Bank> data = new ArrayList<>();

    public ArrayList<Bank> getData() {
        return data;
    }

    public void setData(ArrayList<Bank> data) {
        this.data = data;
    }
}
