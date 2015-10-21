package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityWdlistHistoryBinding;
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
        service = Network.getService(MoneyService.class);
        service.wdlist(page, this);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(WDListHistoryResponse wdListHistoryResponse, Response response) {

    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {

    }
}
