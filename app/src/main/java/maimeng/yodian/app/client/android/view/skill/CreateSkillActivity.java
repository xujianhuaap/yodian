package maimeng.yodian.app.client.android.view.skill;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.henjue.library.hnet.Response;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityCreateSkillBinding;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypeBitmap;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;

/**
 *
 */
public class CreateSkillActivity extends AppCompatActivity implements Target{
    private static final int REQUEST_AUTH = 0x1001;
    private SkillService service;
    private ActivityCreateSkillBinding binding;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this, R.layout.activity_create_skill);
        final SkillTemplate mTemplate;
        if(getIntent().hasExtra("template")){
            mTemplate=getIntent().getParcelableExtra("template");
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
        binding.skillName.addTextChangedListener(new EditTextChangeListener(binding.skillName, binding, mTemplate));
        binding.skillContent.addTextChangedListener(new EditTextChangeListener(binding.skillContent,binding,mTemplate));
        binding.skillPrice.addTextChangedListener(new EditTextChangeListener(binding.skillPrice, binding, mTemplate));
        binding.skillUnit.addTextChangedListener(new EditTextChangeListener(binding.skillUnit,binding,mTemplate));
        service=Network.getService(SkillService.class);
        Network.image(this,mTemplate.getPic(),this);
    }

    private void doDone() {
        binding.invalidateAll();
        SkillTemplate template = binding.getTemplate();
        if(TextUtils.isEmpty(template.getName())){
            binding.getTemplate();
            binding.skillName.setError(getText(R.string.create_not_empty_name));
            return;
        }
        if(TextUtils.isEmpty(template.getContent())){
            binding.skillContent.setError(getText(R.string.create_not_empty_content));
            return;
        }
        if(TextUtils.isEmpty(template.getPrice())){
            binding.skillPrice.setError(getText(R.string.create_not_empty_pirce));
            return;
        }
        if(TextUtils.isEmpty(template.getUnit())){
            binding.skillUnit.setError(getText(R.string.create_not_empty_unit));
            return;
        }
        if(mBitmap==null){
            Toast.makeText(this,R.string.create_not_empty_pic,Toast.LENGTH_SHORT).show();
            return;
        }
        service.add(template.getName(), template.getContent(), new TypeBitmap(mBitmap), template.getPrice(), template.getUnit(), new ToastCallback(this){
            @Override
            public void success(ToastResponse res, Response response) {
                super.success(res, response);
                if(res.isSuccess()){
                    setResult(RESULT_OK);
                    finish();
                }else if(res.isValidateAuth(CreateSkillActivity.this,REQUEST_AUTH));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_AUTH && resultCode==RESULT_OK){
            doDone();
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        this.mBitmap=bitmap;
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    class EditTextChangeListener implements TextWatcher{
        private final EditText mText;
        private final SkillTemplate mTemplate;
        private final ActivityCreateSkillBinding mBinding;

        EditTextChangeListener(EditText text,ActivityCreateSkillBinding binding,SkillTemplate template){
            this.mText=text;
            this.mBinding=binding;
            this.mTemplate=template;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mText==binding.skillContent){
                mTemplate.setContent(s.toString());
            }else if(mText==binding.skillName){
                mTemplate.setName(s.toString());
            }else if(mText==binding.skillPrice){
                mTemplate.setPrice(s.toString());
            }else if(mText==binding.skillUnit){
                mTemplate.setUnit(s.toString());
            }
        }
    }
}
