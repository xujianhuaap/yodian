package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityWdlistHistoryBinding;
import maimeng.yodian.app.client.android.model.remainder.WDList;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.WDListHistoryResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * 提现申请记录
 */
public class WDListHistoryActivity extends AbstractActivity implements Callback<WDListHistoryResponse> {
    private ActivityWdlistHistoryBinding bind;
    private MoneyService service;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = bindView(R.layout.activity_wdlist_history);
        bind.money.setText(String.valueOf(getIntent().getDoubleExtra("mony", 0.0)));
        service = Network.getService(MoneyService.class);
        service.wdlist(page, this);
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
    public void start() {

    }

    @Override
    public void success(WDListHistoryResponse res, Response response) {
        if (res.isSuccess()) {
            WDList wdlist = res.getData();
            System.out.println(wdlist);
        } else {
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        checkError(hNetError);
    }

    @Override
    public void end() {

    }
}
