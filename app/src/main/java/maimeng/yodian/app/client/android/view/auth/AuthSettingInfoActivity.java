package maimeng.yodian.app.client.android.view.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
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

public class AuthSettingInfoActivity extends AppCompatActivity implements Target, View.OnClickListener {
    private static final String LOG_TAG = AuthSettingInfoActivity.class.getName();
    private ImageView mUserImg;
    private EditText mNickname;
    private Bitmap bitmap;
    private User user;
    private PopupWindow window;
    public WaitDialog dialog;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int REQUEST_PHOTOHRAPH = 0x1001;// 拍照
    public static final int REQUEST_PHOTOZOOM = 0x1002; // 缩放
    public static final int REQUEST_PHOTORESOULT = 0x1003;// 结果
    private UserService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.read(this);
        service = Network.getService(UserService.class);
        setContentView(R.layout.activity_auth_setting_info);
        mUserImg = (ImageView) findViewById(R.id.img_avatar);
        findViewById(R.id.btn_album).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_done).setOnClickListener(this);
        mNickname = (EditText) findViewById(R.id.nickname);
        pull();
    }

    public void onClickNext(View v) {
        if (bitmap == null) {
            Toast.makeText(this, "头像为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final String nickname = this.mNickname.getText().toString();

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

    private void showPhotoHraph() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
        startActivityForResult(intent, REQUEST_PHOTOHRAPH);
    }

    private void showPhotoZoom() {
        PhotoPickerIntent intentPhoto = new PhotoPickerIntent(AuthSettingInfoActivity.this);
        intentPhoto.setPhotoCount(1);
        intentPhoto.setShowCamera(false);
        startActivityForResult(intentPhoto, REQUEST_PHOTOZOOM);
    }

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
        new ImageLoaderManager.Loader(this.mUserImg, Uri.parse(headUrl)).callback(new ImageLoaderManager.Callback() {
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
        }).start();
        mUserImg.setTag(headUrl);
        mNickname.setText(nickname);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PHOTOHRAPH:
                    //设置文件保存路径这里放在跟目录下
                    File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
                    startPhotoZoom(Uri.fromFile(picture));
                    break;
                case REQUEST_PHOTOZOOM:
                    ArrayList<String> paths = (ArrayList<String>) data.getSerializableExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    startPhotoZoom(Uri.fromFile(new File(paths.get(0))));
                    break;
                case REQUEST_PHOTORESOULT:
                    Uri uri = Uri.fromFile(tempFile);
                    new ImageLoaderManager.Loader(this.mUserImg, uri).callback(new ImageLoaderManager.Callback() {
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
                    }).start();
                    if (window != null) {
                        window.dismiss();
                    }

                    break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null) {
            this.bitmap = bitmap;
            mUserImg.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取第三方头像失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        System.out.println("onBitmapFailed");
        Toast.makeText(this, "获取第三方头像失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        System.out.println("onPrepareLoad");
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
        if (v.getId() == R.id.btn_album) {
            showPhotoZoom();
        } else if (v.getId() == R.id.btn_camera) {
            showPhotoHraph();
        } else if (v.getId() == R.id.btn_done) {
            final Editable text = mNickname.getText();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, R.string.nickname_input_empty_message, Toast.LENGTH_SHORT).show();

            } else if (bitmap == null) {
                Toast.makeText(this, R.string.avatar_input_empty_message, Toast.LENGTH_SHORT).show();
            } else {
                service.modifyInfo(text.toString(), new TypedBitmap.Builder(bitmap).setMaxSize(300).setAutoMatch(getResources()).build(), new Callback<ModifyUserResponse>() {
                    @Override
                    public void start() {
                        dialog = WaitDialog.show(AuthSettingInfoActivity.this);
                    }

                    @Override
                    public void success(ModifyUserResponse res, Response response) {
                        res.showMessage(AuthSettingInfoActivity.this);
                        if (res.isSuccess()) {
                            user = new User(user.getT_nickname(), user.getT_img(), user.loginType, user.getToken(), user.getUid(), text.toString(), user.getChatLoginName(), res.getData().getAvatar());
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
        }
    }
}
