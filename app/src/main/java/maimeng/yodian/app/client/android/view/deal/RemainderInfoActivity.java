package maimeng.yodian.app.client.android.view.deal;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityRemainderInfoBinding;
import maimeng.yodian.app.client.android.model.Remainder;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by android on 2015/9/22.
 * 我的余额详细信息
 */
public class RemainderInfoActivity extends AbstractActivity {
    public static final String KEY_REMAINDER = "_remainder";
    private ActivityRemainderInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Remainder remainder = getIntent().getParcelableExtra(KEY_REMAINDER);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_remainder_info, null, false);
        setContentView(binding.getRoot());
        binding.htmlComment.setText(Html.fromHtml(getResources().getString(R.string.during_comment3)));
        binding.setRemainder(remainder);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
