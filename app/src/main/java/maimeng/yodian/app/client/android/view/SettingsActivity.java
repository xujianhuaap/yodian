package maimeng.yodian.app.client.android.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.umeng.fb.FeedbackAgent;

import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 2015/7/21.
 */
public class SettingsActivity extends AbstractActivity {
    private View mBtnBack;
    private View mBtnYijian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.action_settings);
        mBtnBack=findViewById(R.id.btn_back);
        mBtnYijian=findViewById(R.id.btn_yijian);
        ViewCompat.setTransitionName(mBtnBack,"back");
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SettingsActivity.this);
            }
        });
        mBtnYijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View,String> back=Pair.create((View)mBtnBack,"back");
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(SettingsActivity.this, back);
                ActivityCompat.startActivity(SettingsActivity.this, new Intent(SettingsActivity.this, FeedBackActivity.class), options.toBundle());
            }
        });
    }
}
