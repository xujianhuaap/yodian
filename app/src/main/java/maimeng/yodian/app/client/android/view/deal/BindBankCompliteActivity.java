package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.BankBindInfoResponse;
import maimeng.yodian.app.client.android.network.service.BankService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by android on 15-9-23.
 */
public class BindBankCompliteActivity extends AbstractActivity implements Callback<BankBindInfoResponse> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_bind_complite);
        BankService service = Network.getService(BankService.class);
        service.bindInfo(this);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(BankBindInfoResponse bankBindInfoResponse, Response response) {

    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {

    }
}
