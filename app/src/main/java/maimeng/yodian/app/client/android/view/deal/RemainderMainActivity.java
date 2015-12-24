package maimeng.yodian.app.client.android.view.deal;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.activity.AlertDialog;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.databinding.AcitivityRemainderMainBinding;
import maimeng.yodian.app.client.android.model.remainder.Remainder;
import maimeng.yodian.app.client.android.model.user.CertifyInfo;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.CertifyInfoResponse;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;

/**
 * 我的余额主页
 */
public class RemainderMainActivity extends AbstractActivity implements Callback<RemainderResponse>, View.OnClickListener, PtrHandler {
    private MoneyService service;
    private AcitivityRemainderMainBinding binding;
    private Remainder defaultValue;
    private static final int REQUEST_SHOW_DURING = 0x1001;
    private static final int REQUEST_INFO_CERTIFY = 0x1002;
    private static final int REQUEST_VOUCH_APPLY = 0x1003;
    private boolean isObtainRemainder = false;
    private CertifyInfo certifyInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.acitivity_remainder_main, null, false);
        setContentView(binding.getRoot());
        defaultValue = new Remainder();
        service = Network.getService(MoneyService.class);
        service.remanider(this);
        binding.btnRemainder.setOnClickListener(this);
        binding.btnRemaindered.setOnClickListener(this);
        binding.btnConfirmInfo.setOnClickListener(this);
        binding.btnDrawMoneyInfo.setOnClickListener(this);

        binding.refreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this).setTextColor(0x0);
        binding.refreshLayout.addPtrUIHandler(header);
        binding.refreshLayout.setHeaderView(header);
        binding.refreshLayout.autoRefresh();


        Network.getService(AuthService.class).getCertifyInfo(new Callback<CertifyInfoResponse>() {
            @Override
            public void start() {

            }

            @Override
            public void success(CertifyInfoResponse certifyInfoResponse, Response response) {
                if (certifyInfoResponse.getCode() == 20000) {
                    certifyInfo= certifyInfoResponse.getData().getCertifi();
                    if (certifyInfo != null) {
                        binding.basicName.setText(certifyInfo.getCname());
                    }

                }else {
//                    Toast.makeText(RemainderMainActivity.this,certifyInfoResponse.getMsg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(HNetError hNetError) {
                ErrorUtils.checkError(RemainderMainActivity.this, hNetError);
            }

            @Override
            public void end() {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
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
    public void success(RemainderResponse res, Response response) {
        if (res.isSuccess()) {
            binding.setRemainder(res.getData());
            isObtainRemainder = true;
        } else {
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void end() {
        binding.refreshLayout.refreshComplete();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnRemainder) {
            RemainderInfoActivity.show(this, binding.getRemainder());
        } else if (v == binding.btnRemaindered) {
            startActivity(new Intent(this, WDListHistoryActivity.class).putExtra("mony", binding.getRemainder().getWithdraw()));
        } else if (v == binding.btnConfirmInfo) {
            BasicalInfoConfirmActivity.show(RemainderMainActivity.this,certifyInfo,REQUEST_INFO_CERTIFY);
        } else if (binding.btnDrawMoneyInfo == v) {
            //必须获得Remainder之后binding.getRemainder()才有效
            DrawMoneyInfoConfirmActivity.show(this, binding.getRemainder().getDraw_account());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SHOW_DURING) {
                Remainder remainder = data.getParcelableExtra(RemainderInfoActivity.KEY_REMAINDER);
                if (remainder != null) {
                    binding.setRemainder(remainder);
                }
            } else if (requestCode == REQUEST_INFO_CERTIFY) {
                certifyInfo= Parcels.unwrap(data.getParcelableExtra("certifyinfo"));
                if(certifyInfo!=null){
                    binding.basicName.setText(certifyInfo.getCname());
                }

            } else if (requestCode == REQUEST_VOUCH_APPLY) {
                //申请完成
                int applyStatus = data.getIntExtra("apply", 0x12);
                if (applyStatus == BindStatus.WAITCONFIRM.getValue()) {
                    AlertDialog.show(this, "", getString(R.string.apply_alertdialog_tip), getString(R.string.btn_name));
                }

            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        service.remanider(this);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        service.remanider(this);
    }
}
