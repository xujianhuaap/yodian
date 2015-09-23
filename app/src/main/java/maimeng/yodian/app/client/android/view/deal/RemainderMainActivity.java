package maimeng.yodian.app.client.android.view.deal;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.databinding.AcitivityRemainderMainBinding;
import maimeng.yodian.app.client.android.model.Remainder;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * 我的余额主页
 */
public class RemainderMainActivity extends AbstractActivity implements Callback<RemainderResponse>, View.OnClickListener, PtrHandler {
    private MoneyService service;
    private AcitivityRemainderMainBinding binding;
    private Remainder defaultValue;
    private static final int REQUEST_SHOW_DURING = 0x1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.acitivity_remainder_main, null, false);
        setContentView(binding.getRoot());
        defaultValue = new Remainder();
        service = Network.getService(MoneyService.class);
        service.remanider(this);
        binding.btnRemainder.setOnClickListener(this);


        binding.refreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this).setTextColor(0x0);
        binding.refreshLayout.addPtrUIHandler(header);
        binding.refreshLayout.setHeaderView(header);
        binding.refreshLayout.autoRefresh();
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
//            actionBar.setDisplayUseLogoEnabled(true);
//            actionBar.setLogo(R.drawable.ic_go_back);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void success(RemainderResponse res, Response response) {
        if (res.isSuccess()) {
            binding.setRemainder(res.getData());
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
            startActivityForResult(new Intent(this, RemainderInfoActivity.class).putExtra(RemainderInfoActivity.KEY_REMAINDER, binding.getRemainder()), REQUEST_SHOW_DURING);

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
            }
        }
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