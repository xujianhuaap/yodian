package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityWdlistHistoryBinding;
import maimeng.yodian.app.client.android.model.remainder.WDModel;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * 提现申请记录
 */
public class WDListHistoryActivity extends AbstractActivity {
    private ActivityWdlistHistoryBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = bindView(R.layout.activity_wdlist_history);
        bind.setWd(new WDModel());
    }
}
