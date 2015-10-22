package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.databinding.ActivityWdlistHistoryBinding;
import maimeng.yodian.app.client.android.databinding.ItemWdlistHistoryBinding;
import maimeng.yodian.app.client.android.model.remainder.WDList;
import maimeng.yodian.app.client.android.model.remainder.WDModel;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.WDListHistoryResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * 提现申请记录
 */
public class WDListHistoryActivity extends AbstractActivity implements Callback<WDListHistoryResponse>, AbstractAdapter.ViewHolderClickListener<WDListHistoryActivity.HistoryHolder> {
    private ActivityWdlistHistoryBinding bind;
    private MoneyService service;
    private int page = 1;
    private HistoryAdaper adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = bindView(R.layout.activity_wdlist_history);
        bind.money.setText(String.valueOf(getIntent().getDoubleExtra("mony", 0.0)));
        bind.historyList.setLayoutManager(new ListLayoutManager(this));
        adapter = new HistoryAdaper(this, this);
        bind.historyList.setAdapter(adapter);
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
            adapter.reload(wdlist.getList(), false);
            adapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(HistoryHolder holder, int postion) {

    }

    @Override
    public void onClick(HistoryHolder holder, View clickItem, int postion) {

    }

    protected static final class HistoryAdaper extends AbstractAdapter<WDModel, HistoryHolder> {

        public HistoryAdaper(Context context, ViewHolderClickListener<HistoryHolder> viewHolderClickListener) {
            super(context, viewHolderClickListener);
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemWdlistHistoryBinding inflate = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_wdlist_history, parent, false);
            return new HistoryHolder(inflate);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            holder.bind(getItem(position));
        }
    }

    protected static final class HistoryHolder extends AbstractAdapter.BindViewHolder<WDModel, ItemWdlistHistoryBinding> {
        public HistoryHolder(ItemWdlistHistoryBinding t) {
            super(t);

        }

        @Override
        public void bind(WDModel item) {
            binding.backwhy.setVisibility(View.GONE);
            if (TextUtils.isEmpty(item.getBackwhy())) {
                binding.btnPull.setOnClickListener(null);
                binding.btnPull.setVisibility(View.INVISIBLE);
            } else {
                binding.btnPull.setVisibility(View.VISIBLE);
                binding.btnPull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.backwhy.getVisibility() == View.VISIBLE) {
                            binding.backwhy.setVisibility(View.GONE);
                        } else {
                            binding.backwhy.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            binding.setModel(item);
            final String status;
            switch (item.getStatus()) {
                case 0:
                    status = "提现中";
                    break;
                case 1:
                    status = "已删除";
                    break;
                case 2:
                    status = "提现成功";
                    break;
                case 3:
                    status = "提现失败";
                    break;
                default:
                    status = "";
            }
            binding.status.setText(status);
        }
    }
}
