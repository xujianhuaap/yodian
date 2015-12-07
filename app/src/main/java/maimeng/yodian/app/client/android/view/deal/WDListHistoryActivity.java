package maimeng.yodian.app.client.android.view.deal;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.ActivityWdlistHistoryBinding;
import maimeng.yodian.app.client.android.databinding.ItemWdlistHistoryBinding;
import maimeng.yodian.app.client.android.model.remainder.WDList;
import maimeng.yodian.app.client.android.model.remainder.WDModel;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.WDListHistoryResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
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
        bind.money.setText(String.format("%.2f", getIntent().getDoubleExtra("mony", 0)));
        bind.historyList.setLayoutManager(new ListLayoutManager(this));
        adapter = new HistoryAdaper(this, this);
        bind.historyList.setAdapter(adapter);
        service = Network.getService(MoneyService.class);
        service.wdlist(page, this);
        MobclickAgent.onEvent(this, UEvent.REMAINDER_DRAWED);
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
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
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

    protected final class HistoryAdaper extends AbstractAdapter<WDModel, HistoryHolder> {

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

    protected final class HistoryHolder extends AbstractAdapter.BindViewHolder<WDModel, ItemWdlistHistoryBinding> {

        private String alipayStr;

        public HistoryHolder(ItemWdlistHistoryBinding t) {
            super(t);

        }

        @Override
        public void bind(WDModel item) {
            binding.backwhy.setVisibility(View.GONE);
            binding.drawAccount.setVisibility(View.GONE);
            binding.btnPull.setVisibility(View.VISIBLE);
            alipayStr = null;
            if (!TextUtils.isEmpty(item.getCard_id()) & Integer.parseInt(item.getCard_id()) != 0) {
                alipayStr = "提现账号：银行卡";
                binding.drawAccount.setText(alipayStr);
            } else {

                alipayStr = item.getAlipay();
                Spanned span = Html.fromHtml(WDListHistoryActivity.this.getResources().getString(R.string.zhifubao_account, alipayStr));
                binding.drawAccount.setText(span);

            }
            String backWhyStr = item.getBackwhy();
            if (TextUtils.isEmpty(backWhyStr) && TextUtils.isEmpty(alipayStr)) {
                binding.btnPull.setVisibility(View.INVISIBLE);
            }
            binding.backwhy.setText(backWhyStr);

            binding.btnPull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.btnArrow.setChecked(!binding.btnArrow.isChecked());
                }
            });
            binding.btnArrow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.btnArrow, "rotation", 0, 180);
                        animator.setDuration(300);
                        animator.setRepeatCount(0);
                        animator.start();

                    } else {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.btnArrow, "rotation", 180, 360);
                        animator.setDuration(300);
                        animator.setRepeatCount(0);
                        animator.start();
                    }
                    if (!TextUtils.isEmpty(binding.getModel().getBackwhy())) {
                        if (!isChecked) {
                            binding.backwhy.setVisibility(View.GONE);
                        } else {
                            binding.backwhy.setVisibility(View.VISIBLE);
                        }
                    }

                    if (!TextUtils.isEmpty(alipayStr)) {
                        if (!isChecked) {
                            binding.drawAccount.setVisibility(View.GONE);
                        } else {
                            binding.drawAccount.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
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
