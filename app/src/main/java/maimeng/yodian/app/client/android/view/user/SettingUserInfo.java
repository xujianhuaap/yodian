package maimeng.yodian.app.client.android.view.user;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.databinding.ActivitySettingUserInfoBinding;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypeBitmap;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;
import maimeng.yodian.app.client.android.network.service.UserService;

/**
 * Created by android on 15-7-20.
 */
public class SettingUserInfo extends AppCompatActivity implements View.OnClickListener,Target, Callback<ModifyUserResponse> {
    private ActivitySettingUserInfoBinding binding;
    private Bitmap mBitmap=null;
    private User user;
    private UserService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=Network.getService(UserService.class);
        binding=DataBindingUtil.setContentView(this, R.layout.activity_setting_user_info);
        ViewCompat.setTransitionName(binding.userAvatar, "avatar");
        ViewCompat.setTransitionName(binding.btnBack,"back");
        user = User.read(this);
        binding.setUser(user);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SettingUserInfo.this);
            }
        });
        binding.btnCleanName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nickname.setText("");
            }
        });
        binding.btnCleanWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wechat.setText("");
            }
        });
        binding.nickname.addTextChangedListener(new EditTextChangeListener(binding.nickname, binding, user));
        binding.wechat.addTextChangedListener(new EditTextChangeListener(binding.wechat, binding, user));
        binding.btnSubmit.setOnClickListener(this);
        binding.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        binding.buttonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        Network.image(this, user.getAvatar(), this);
    }
    private void toggle() {
        final Animation animation;
        if(binding.buttonContainer.getVisibility()==View.VISIBLE){
            animation = AnimationUtils.loadAnimation(this, R.anim.alpha_to0);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.buttonContainer.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }else{
            animation = AnimationUtils.loadAnimation(this, R.anim.alpha_to1);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.buttonContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        binding.buttonContainer.startAnimation(animation);
    }
    @Override
    public void onClick(View v) {
        binding.invalidateAll();
        if(TextUtils.isEmpty(user.getNickname())){
            binding.nickname.setError(getText(R.string.nickname_input_empty_message));
            return;
        }
        if(TextUtils.isEmpty(user.getWechat())){
            binding.wechat.setError(getText(R.string.wechat_input_empty_message));
            return;
        }
        if(mBitmap==null){
            Toast.makeText(this, R.string.avatar_input_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }
        service.modifyInfo(user.getNickname(),user.getWechat(),new TypeBitmap(mBitmap),this);
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

    @Override
    public void start() {

    }

    @Override
    public void success(ModifyUserResponse res, Response response) {
        if(res.isSuccess()){
            res.showMessage(this);
            user.write(this);
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this,hNetError);
    }

    @Override
    public void end() {

    }

    class EditTextChangeListener implements TextWatcher {
        private final EditText mText;
        private final User user;
        private final ActivitySettingUserInfoBinding mBinding;

        EditTextChangeListener(EditText text,ActivitySettingUserInfoBinding binding,User user){
            this.mText=text;
            this.mBinding=binding;
            this.user=user;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mText==binding.wechat){
                user.setWechat(s.toString());
            }else if(mText==binding.nickname){
                user.setNickname(s.toString());
            }
        }
    }
}
