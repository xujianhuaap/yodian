package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.view.skill.proxy.MainHomeProxy;

/**
 * Created by android on 2015/8/13.
 */
public class UserHomeActivity extends AbstractActivity implements Callback<SkillUserResponse> {
    private SkillService service;
    private MainHomeProxy proxy;
    private int page = 1;
    private View mBtnSettings;
    private WaitDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Network.getService(SkillService.class);
        setContentView(R.layout.activity_user_home, false);
        final View btnBack = findViewById(R.id.btn_back);
        ViewCompat.setTransitionName(btnBack, "back");
        final View root = findViewById(R.id.home_root);
        proxy = new MainHomeProxy(this, root);
        ((View) findViewById(R.id.btn_createskill).getParent()).setVisibility(View.GONE);
        findViewById(R.id.btn_chat).setVisibility(View.GONE);
        findViewById(R.id.btn_settings).setVisibility(View.GONE);
        findViewById(R.id.user_avatar).setOnClickListener(null);
        service.list(getIntent().getLongExtra("uid", 0), page, this);
        root.setVisibility(View.VISIBLE);
        final View btnReport = findViewById(R.id.btn_report);
        btnReport.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserHomeActivity.this);
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });
    }

    private void report() {

    }

    @Override
    public void start() {
        dialog = WaitDialog.show(this);
    }

    @Override
    public void success(SkillUserResponse res, Response response) {
        if (res.isSuccess()) {
            final SkillUserResponse.DataNode data = res.getData();
            proxy.init(data.getUser());
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
        dialog.dismiss();
    }
}
