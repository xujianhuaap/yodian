package maimeng.yodian.app.client.android.view.deal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityBankBindCompliteBinding;
import maimeng.yodian.app.client.android.model.remainder.BindBank;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.BankBindInfoResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.BankService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by android on 15-9-23.
 */
public class BindBankCompliteActivity extends AbstractActivity implements Callback<BankBindInfoResponse>, View.OnClickListener {
    private static final int REQUEST_BIND = 0x1001;
    private ActivityBankBindCompliteBinding binding;
    private BankService service;
    private WaitDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = bindView(R.layout.activity_bank_bind_complite);
        binding.btnRebind.setOnClickListener(this);
        binding.btnUnbind.setOnClickListener(this);
        service = Network.getService(BankService.class);
        service.bindInfo(this);
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
        dialog = WaitDialog.show(this);
    }

    @Override
    public void success(BankBindInfoResponse res, Response response) {
        final BindBank data = res.getData();
        binding.setBind(data);
        if (data.getStatus() == BindStatus.PASS) {
            binding.status.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.ic_bind_status_pass), null);
            binding.status.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.normal_padding));
        } else {
            binding.status.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            binding.status.setCompoundDrawablePadding(0);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void end() {
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_BIND == requestCode) {
            if (resultCode == RESULT_OK) {
                service.bindInfo(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnRebind) {
            startActivityForResult(new Intent(this, BasicalInfoConfirmActivity.class).putExtra("result", true), REQUEST_BIND);
        } else if (v == binding.btnUnbind) {
            service.unbind(new ToastCallback(this) {
                @Override
                public void success(ToastResponse toastResponse, Response response) {
                    super.success(toastResponse, response);
                    if (toastResponse.isSuccess()) {
                        finish();
                    }
                }
            });
        }
    }
}
