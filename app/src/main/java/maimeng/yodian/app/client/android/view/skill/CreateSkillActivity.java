package maimeng.yodian.app.client.android.view.skill;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityCreateSkillBinding;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.network.Network;

/**
 *
 */
public class CreateSkillActivity extends AppCompatActivity {
    private SkillTemplate mTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCreateSkillBinding binding=DataBindingUtil.setContentView(this, R.layout.activity_create_skill);
        if(getIntent().hasExtra("template")){
            this.mTemplate=getIntent().getParcelableExtra("template");
            ViewCompat.setTransitionName(binding.skillPic, "img");
            ViewCompat.setTransitionName(binding.skillName, "title");
        }else{
            mTemplate=new SkillTemplate();
        }
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(CreateSkillActivity.this);
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDone();
            }
        });
        binding.setTemplate(mTemplate);
        Network.image(mTemplate.getPic(),binding.skillPic);
    }

    private void doDone() {
//        Intent intent = new Intent(this, MainTabActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

}
