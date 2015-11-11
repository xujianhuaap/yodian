package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityRemainderInfoBinding;
import maimeng.yodian.app.client.android.model.remainder.Remainder;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.deal.pay.CertifyStatus;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;

/**
 * Created by android on 2015/9/22.
 * 我的余额详细信息
 */
public class RemainderInfoActivity extends AbstractActivity implements View.OnClickListener {
    public static final String KEY_REMAINDER = "_remainder";
    private ActivityRemainderInfoBinding binding;
    private MoneyService service;
    private Remainder mAlipay;


    public static void show(Context context, Remainder remainder) {
        Intent intent = new Intent(context, RemainderInfoActivity.class);
        intent.putExtra(RemainderInfoActivity.KEY_REMAINDER, Parcels.wrap(remainder));
        context.startActivity(intent);
    }

    @Override
    public void postponeEnterTransition() {
        super.postponeEnterTransition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlipay = Parcels.unwrap(getIntent().getParcelableExtra(KEY_REMAINDER));
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_remainder_info, null, false);
        setContentView(binding.getRoot());
        binding.htmlComment.setText(Html.fromHtml(getResources().getString(R.string.during_comment3)));
        binding.setRemainder(mAlipay);
        binding.btnDuring.setOnClickListener(this);
        binding.mySaleOrder.setOnClickListener(this);
        service = Network.getService(MoneyService.class);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayUseLogoEnabled(true);
//            actionBar.setLogo(R.drawable.ic_go_back);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
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
    public void onClick(View v) {

        if (v == binding.btnDuring) {
            if (!TextUtils.isEmpty(mAlipay.getDraw_account())) {
                showDuringDialog();
            } else {
                Toast.makeText(this, R.string.toast_bind_bank_card, Toast.LENGTH_SHORT).show();
            }
        } else if (v == binding.mySaleOrder) {
            OrderListActivity.show(this, true);
        }
    }

    /**
     * 显示提现对话框
     */
    private void showDuringDialog() {
        if (binding.getRemainder().getMoney() < 50) {
            AlertDialog.newInstance("提示", "余额少于50元不能提现").setNegativeListener(new AlertDialog.NegativeListener() {
                @Override
                public void onNegativeClick(DialogInterface dialog) {
                    dialog.dismiss();
                }

                @Override
                public String negativeText() {
                    return getString(android.R.string.ok);
                }
            }).show(getFragmentManager(), "_dialog");
        } else {
            DuringDialog.show(this, binding.getRemainder().getMoney());
        }
    }

    /**
     * 是否有成功操作提现功能
     */
    private boolean moneyChanged = false;
    private double during = 0;

    @Override
    public void finish() {
        if (moneyChanged) {
            setResult(RESULT_OK, new Intent().putExtra(KEY_REMAINDER, Parcels.wrap(binding.getRemainder())).putExtra("during", during));
        }
        super.finish();
    }

    public void onInputDuring(final double during) {
        service.withdraw(during, new ToastCallback(this) {
            @Override
            public void success(ToastResponse res, Response response) {
                super.success(res, response);
                if (res.isSuccess()) {
                    moneyChanged = true;
                    final Remainder remainder = binding.getRemainder();
                    remainder.setMoney(remainder.getMoney() - during);
                    remainder.setDuring(remainder.getDuring() + during);
                    binding.setRemainder(remainder);
                    RemainderInfoActivity.this.during = during;
                }
            }

            @Override
            public void failure(HNetError hNetError) {
                super.failure(hNetError);
            }
        });
    }
}
