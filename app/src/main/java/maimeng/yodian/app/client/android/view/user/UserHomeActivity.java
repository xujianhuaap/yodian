package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
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
    private User user;

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
        AlertDialog alert = AlertDialog.newInstance("提示", getString(R.string.lable_alert_report_user));
        alert.setNegativeListener(new AlertDialog.NegativeListener() {
            @Override
            public void onNegativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public String negativeText() {
                return getString(android.R.string.cancel);
            }
        });
        alert.setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(DialogInterface dialog) {
                Network.getService(CommonService.class).report(3, 0, 0, user.getUid(), new ToastCallback(UserHomeActivity.this));
            }

            @Override
            public String positiveText() {
                return getString(R.string.lable_report);
            }
        });
        alert.show(getFragmentManager(), "alert");
    }

    @Override
    public void start() {
        dialog = WaitDialog.show(this);
    }

    @Override
    public void success(SkillUserResponse res, Response response) {
        if (res.isSuccess()) {
            final SkillUserResponse.DataNode data = res.getData();
            this.user = data.getUser();
            proxy.init(user);
            proxy.show((FloatingActionButton) findViewById(R.id.btn_back));
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
