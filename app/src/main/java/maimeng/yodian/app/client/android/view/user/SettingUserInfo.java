package maimeng.yodian.app.client.android.view.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.AbstractActivity;
import maimeng.yodian.app.client.android.databinding.ActivitySettingUserInfoBinding;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * Created by android on 15-7-20.
 */
public class SettingUserInfo extends AbstractActivity implements View.OnClickListener, Callback<ModifyUserResponse>, ImageLoaderManager.Callback {
    private ActivitySettingUserInfoBinding binding;
    private Bitmap mBitmap = null;
    private User user;
    private UserService service;
    private static final int REQUEST_AUTH = 0x1001;
    private static final int REQUEST_SELECT_PHOTO = 0x2001;
    private WaitDialog dialog;
    private File tempFile;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(SettingUserInfo.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        tempFile = new File(dir, getPhotoFileName());
        service = Network.getService(UserService.class);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_setting_user_info, null, false);
        setContentView(binding.getRoot());
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        setTitle("");
        ViewCompat.setTransitionName(binding.userAvatar, "avatar");
        ViewCompat.setTransitionName(binding.btnBack, "back");
        user = User.read(this);
        binding.setUser(user);
        binding.executePendingBindings();
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
        binding.btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intentPhoto = new PhotoPickerIntent(SettingUserInfo.this);
                intentPhoto.setPhotoCount(1);
                intentPhoto.setShowCamera(false);
                startActivityForResult(intentPhoto, REQUEST_SELECT_PHOTO);
            }
        });
        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intentPhoto = new PhotoPickerIntent(SettingUserInfo.this);
                intentPhoto.setPhotoCount(1);
                intentPhoto.setShowCamera(true);
                startActivityForResult(intentPhoto, REQUEST_SELECT_PHOTO);
            }
        });
        new ImageLoaderManager.Loader(this, Uri.parse(user.getAvatar())).callback(this).start();
        binding.nickname.addTextChangedListener(new EditTextChangeListener(binding.nickname, binding, user));
        binding.wechat.addTextChangedListener(new EditTextChangeListener(binding.wechat, binding, user));
    }

    private void toggle() {
        final Animation animation;
        if (binding.buttonContainer.getVisibility() == View.VISIBLE) {
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

        } else {
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
        if (TextUtils.isEmpty(user.getNickname())) {
            binding.nickname.setError(getText(R.string.nickname_input_empty_message));
            return;
        }
        if (TextUtils.isEmpty(user.getWechat())) {
            binding.wechat.setError(getText(R.string.wechat_input_empty_message));
            return;
        }
        if (mBitmap == null) {
            Toast.makeText(this, R.string.avatar_input_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }

//                service.modifyInfo(user.getNickname(),user.getWechat(),new TypeBitmap(BitmapUtils.compress(mBitmap,540,540,false)),this);
        service.modifyInfo(user.getNickname(), user.getWechat(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setMaxHeight(540).setMaxWidth(540).build(), this);
    }

    @Override
    public void start() {
        binding.btnSubmit.setEnabled(false);
        dialog = WaitDialog.show(this);
    }

    @Override
    public void success(ModifyUserResponse res, Response response) {
        res.showMessage(this);
        if (res.isSuccess()) {
            user.setAvatar(res.getData().getAvatar());
            user.write(this);
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override
    public void onLoadEnd() {

    }

    @Override
    public void onLoadFaild() {

    }

    @Override
    public void end() {
        binding.btnSubmit.setEnabled(true);
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {
            ArrayList<String> paths = (ArrayList<String>) data.getSerializableExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            String uri = Uri.fromFile(new File(paths.get(0))).toString();
            startPhotoZoom(Uri.parse(uri));
        } else if (requestCode == REQUEST_PHOTORESOULT && RESULT_OK == resultCode) {
            if (tempFile != null) {
                Uri uri = Uri.fromFile(tempFile);
                new ImageLoaderManager.Loader(this, uri).callback(this).start();
                binding.getUser().setAvatar(uri.toString());
                tempFile.deleteOnExit();
            }

            toggle();
        }
    }

    class EditTextChangeListener implements TextWatcher {
        private final EditText mText;
        private final User user;
        private final ActivitySettingUserInfoBinding mBinding;

        EditTextChangeListener(EditText text, ActivitySettingUserInfoBinding binding, User user) {
            this.mText = text;
            this.mBinding = binding;
            this.user = user;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mText == binding.wechat) {
                user.setWechat(s.toString());
            } else if (mText == binding.nickname) {
                user.setNickname(s.toString());
            }
        }
    }

    public static final String IMAGE_UNSPECIFIED = "image/*";

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "yodian");
    public static final int REQUEST_PHOTORESOULT = 0x1003;// 结果

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        float dimension = 320;
        intent.putExtra("outputX", dimension);
        intent.putExtra("outputY", dimension);
        intent.putExtra("return-data", false);
        tempFile = new File(dir, getPhotoFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_PHOTORESOULT);
    }
}
