package maimeng.yodian.app.client.android.view.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityAuthSettingInfoBinding;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.loader.Circle;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class AuthSettingInfoActivity extends AbstractActivity implements View.OnClickListener {
    private static final String LOG_TAG = AuthSettingInfoActivity.class.getName();
    private Bitmap bitmap;
    private User user;
    public WaitDialog dialog;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int REQUEST_PHOTORESOULT = 0x1003;// 结果
    private UserService service;
    private ActivityAuthSettingInfoBinding binding;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            ActivityCompat.finishAfterTransition(SettingUserInfo.this);
            User.clear(AuthSettingInfoActivity.this);
            AuthRedirect.toHome(AuthSettingInfoActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        toolbar.setBackgroundResource(android.R.color.transparent);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back_black);
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged("", color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.read(this);
        service = Network.getService(UserService.class);
        setContentView(R.layout.activity_auth_setting_info);
        binding = bindView(R.layout.activity_auth_setting_info);
        binding.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        binding.btnSubmit.setOnClickListener(this);
        binding.btnCleanNickname.setOnClickListener(this);
        binding.btnCleanWechat.setOnClickListener(this);
        binding.btnCleanQq.setOnClickListener(this);
        binding.btnCleanMobile.setOnClickListener(this);
        binding.nickname.addTextChangedListener(new TextListener(binding.nickname, binding));
        binding.wechat.addTextChangedListener(new TextListener(binding.wechat, binding));
        binding.qq.addTextChangedListener(new TextListener(binding.qq, binding));
        binding.mobile.addTextChangedListener(new TextListener(binding.mobile, binding));
        pull();
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void createTempFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "chuangketie");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        tempFile = new File(dir, getPhotoFileName());
    }

    File tempFile;

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        float dimension = getResources().getDisplayMetrics().widthPixels / 2;

        intent.putExtra("outputX", dimension);
        intent.putExtra("outputY", dimension);
        createTempFile();
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        startActivityForResult(intent, REQUEST_PHOTORESOULT);
    }


    /**
     * 从第三方拉取信息
     */
    private void pull() {
        if (user.loginType == 1) {
            pullByWeixin();
        } else if (user.loginType == 2) {
            pullByWeibo();
        }
    }

    private void pullByWeixin() {
        if (user == null) return;
        String name = TextUtils.isEmpty(user.getNickname()) ? user.getT_nickname() : user.getNickname();
        String head = TextUtils.isEmpty(user.getAvatar()) ? user.getT_img() : user.getAvatar();
        setDefaultInfo(name, head);
    }

    private void pullByWeibo() {
        if (user == null) return;
        String name = TextUtils.isEmpty(user.getNickname()) ? user.getT_nickname() : user.getNickname();
        String head = TextUtils.isEmpty(user.getAvatar()) ? user.getT_img() : user.getAvatar();
        setDefaultInfo(name, head);

    }

    private void setDefaultInfo(String nickname, String headUrl) {
        if (headUrl == null) return;
        new ImageLoaderManager.Loader(binding.imgAvatar, Uri.parse(headUrl)).circle(Circle.obtain()).callback(new ImageLoaderManager.Callback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                AuthSettingInfoActivity.this.bitmap = bitmap;
            }

            @Override
            public void onLoadEnd() {

            }

            @Override
            public void onLoadFaild() {

            }
        }).start(this);
        binding.nickname.setText(nickname);
    }

    private class TextListener implements TextWatcher {
        private final EditText editText;
        private final ActivityAuthSettingInfoBinding binding;

        public TextListener(EditText editText, ActivityAuthSettingInfoBinding binding) {
            this.editText = editText;
            this.binding = binding;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean show = s.toString().trim().length() > 0;
            if (editText == binding.nickname) {
                binding.btnCleanNickname.setVisibility(show ? View.VISIBLE : View.GONE);
            } else if (editText == binding.wechat) {
                binding.btnCleanWechat.setVisibility(show ? View.VISIBLE : View.GONE);
            } else if (editText == binding.qq) {
                binding.btnCleanQq.setVisibility(show ? View.VISIBLE : View.GONE);
            } else if (editText == binding.mobile) {
                binding.btnCleanMobile.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECT_PHOTO:
                    if (dialogAlert.isShowing()) {
                        dialogAlert.dismiss();
                    }
                    ArrayList<String> paths = (ArrayList<String>) data.getSerializableExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    String uri = Uri.fromFile(new File(paths.get(0))).toString();
                    startPhotoZoom(Uri.parse(uri));
                    break;
                case REQUEST_PHOTORESOULT:
                    if (dialogAlert.isShowing()) {
                        dialogAlert.dismiss();
                    }
                    if (tempFile != null) {
                        new ImageLoaderManager.Loader(binding.imgAvatar, Uri.fromFile(tempFile)).circle(Circle.obtain()).callback(new ImageLoaderManager.Callback() {
                            @Override
                            public void onImageLoaded(Bitmap bitmap) {
                                AuthSettingInfoActivity.this.bitmap = bitmap;
                            }

                            @Override
                            public void onLoadEnd() {

                            }

                            @Override
                            public void onLoadFaild() {

                            }
                        }).start(this);
                    }

                    break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            final Editable nickname = binding.nickname.getText();
            if (TextUtils.isEmpty(nickname)) {
                Toast.makeText(this, R.string.nickname_input_empty_message, Toast.LENGTH_SHORT).show();

            } else if (bitmap == null) {
                Toast.makeText(this, R.string.avatar_input_empty_message, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.wechat.getText()) && TextUtils.isEmpty(binding.qq.getText()) && TextUtils.isEmpty(binding.mobile.getText())) {
                Toast.makeText(this, R.string.contact_input_empty_message, Toast.LENGTH_SHORT).show();
            } else {
                service.modifyInfo(nickname.toString(), 0, "", "", binding.wechat.getText().toString(), new TypedBitmap.Builder(bitmap).setMaxSize(300).setAutoMatch(getResources()).build(), binding.qq.getText().toString(), binding.mobile.getText().toString(), "", "", ""
                        , new Callback<ModifyUserResponse>() {
                            @Override
                            public void start() {
                                if (!isFinishing()) {
                                    dialog = WaitDialog.show(AuthSettingInfoActivity.this);
                                }

                            }

                            @Override
                            public void success(ModifyUserResponse res, Response response) {
                                res.showMessage(AuthSettingInfoActivity.this);
                                if (res.isSuccess()) {
                                    user = new User(user.getT_nickname(), user.getT_img(), user.loginType, user.getToken(), user.getUid(), nickname.toString(), user.getChatLoginName(), res.getData().getAvatar());
                                    User.Info info = user.getInfo();
                                    if (info == null) {
                                        info = new User.Info();
                                    }
                                    info.setQq(binding.qq.getText().toString());
                                    info.setWechat(binding.wechat.getText().toString());
                                    info.setMobile(binding.mobile.getText().toString());
                                    user.write(AuthSettingInfoActivity.this);
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }

                            @Override
                            public void failure(HNetError hNetError) {
                                ErrorUtils.checkError(AuthSettingInfoActivity.this, hNetError);
                            }

                            @Override
                            public void end() {
                                if (dialog != null) dialog.dismiss();
                            }
                        });
            }
        } else if (v == binding.btnCleanNickname) {
            binding.nickname.setText("");
        } else if (v == binding.btnCleanWechat) {
            binding.wechat.setText("");
        } else if (v == binding.btnCleanQq) {
            binding.qq.setText("");
        } else if (v == binding.btnCleanMobile) {
            binding.mobile.setText("");
        }
    }

    private AlertDialog dialogAlert;
    private static final int REQUEST_SELECT_PHOTO = 0x2001;

    private void toggle() {
        if (dialogAlert == null) {
            dialogAlert = new AlertDialog.Builder(this).setTitle("上传方式").setItems(new String[]{"从相册中选择", "拍照"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    PhotoPickerIntent intentPhoto = new PhotoPickerIntent(AuthSettingInfoActivity.this);
                    switch (which) {
                        case 0:
                            intentPhoto.setPhotoCount(1);
                            intentPhoto.setShowCamera(false);
                            startActivityForResult(intentPhoto, REQUEST_SELECT_PHOTO);
                            dialog.dismiss();
                            break;
                        case 1:
                            intentPhoto.setPhotoCount(1);
                            intentPhoto.setShowCamera(true);
                            startActivityForResult(intentPhoto, REQUEST_SELECT_PHOTO);
                            dialog.dismiss();

                            break;
                    }
                }
            }).create();
//                dialogAlert.getWindow().setGravity(Gravity.BOTTOM);
        }
        if (dialogAlert.isShowing()) {
            dialogAlert.hide();
        } else {
            dialogAlert.show();
        }
    }
}
