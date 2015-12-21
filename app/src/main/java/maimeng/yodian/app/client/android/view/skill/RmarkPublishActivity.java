package maimeng.yodian.app.client.android.view.skill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Response;
import org.parceler.Parcels;

import java.io.File;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.ActivityRmarkPublishBinding;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * 日记发布
 */
public class RmarkPublishActivity extends AbstractActivity implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener {

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String LOG_TAG = RmarkPublishActivity.class.getName();
    private ActivityRmarkPublishBinding mBinding;

    private int mScreenWidth;

    private final int REQUEST_CODE_CAMMRA = 0x23;
    private final int REQUEST_CODE_CLIPPING = 0x24;
    private final int REQUEST_CODE_ALBUM = 0x25;
    private Uri mTempUri;
    private File mFile;
    private SkillService mSkillService;
    private Skill mSkill;
    private WaitDialog dialog;
    private Bitmap mBitmap;
    private int mScreenHeight;
    private ImageView mPic;

    public static void show(Activity context, Skill skill, View backView, int requestCode) {
        Intent intent = new Intent();
        intent.putExtra("skill", Parcels.wrap(skill));
        intent.setClass(context, RmarkPublishActivity.class);
        ActivityCompat.startActivityForResult(context, intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(context, backView, "back").toBundle());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mBinding = bindView(R.layout.activity_rmark_publish);
        mSkill = Parcels.unwrap(getIntent().getParcelableExtra("skill"));
        mSkillService = Network.getService(SkillService.class);
        EditTextProxy editTextProxy = new EditTextProxy();

        mBinding.ivPic.setOnClickListener(this);
        mBinding.btnDone.setOnClickListener(this);
        mBinding.btnCamera.setOnClickListener(this);
        mBinding.btnAlbum.setOnClickListener(this);
        mBinding.skillPic.setOnClickListener(this);
        mBinding.cheSelectPhoto.setOnCheckedChangeListener(this);
        mBinding.editDiary.addTextChangedListener(editTextProxy);
        mBinding.editDiary.setOnKeyListener(editTextProxy);
        MobclickAgent.onEvent(this, UEvent.RMARK_PUBLISH);
    }

    private void refresh(Skill skill) {

        if (mBitmap != null) {
            TypedBitmap typedBitmap = new TypedBitmap.Builder(mBitmap, mBitmap.getWidth(), mBitmap.getHeight()).build();

            mSkillService.add_rmark(mSkill.getId(), skill.getContent(), typedBitmap, new ToastCallback(this) {
                @Override
                public void success(ToastResponse res, Response response) {
                    super.success(res, response);
                    if (mFile != null) mFile.deleteOnExit();
                    mTempUri = null;
                    if (res.isSuccess()) {
                        setResult(RESULT_OK);
                        ActivityCompat.finishAfterTransition(RmarkPublishActivity.this);
                    }
                }

                @Override
                public void start() {
                    super.start();
                    dialog = WaitDialog.show(RmarkPublishActivity.this);
                }

                @Override
                public void end() {
                    super.end();
                    dialog.dismiss();
                }
            });
        }

    }

    /***
     *
     */
    private class EditTextProxy implements TextWatcher, View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            LogUtil.d(LOG_TAG, "keycode" + keyCode);
            if (keyCode == 66) {
                InputMethodManager inputMethodService = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodService.hideSoftInputFromInputMethod(mBinding.editDiary.getWindowToken(), 0);
            }
            return false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtil.d(LOG_TAG, "count" + count);
            mBinding.tvNumber.setText(s.length() + " / 150");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    @Override
    public void onClick(View v) {

        if (v == mBinding.btnDone) {
            MobclickAgent.onEvent(this, UEvent.SKILL_PUBLISH_SUBMIT);
            if (mFile != null) {
                Editable text = mBinding.editDiary.getText();
                if (!TextUtils.isEmpty(text)) {
                    String content = text.toString();
                    String path = mFile.getAbsolutePath();
                    Skill skill = new Skill();
                    skill.setPic(path);
                    skill.setContent(content);
                    refresh(skill);
                } else {
                    Toast.makeText(this, "您还没编辑日记内容", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "不要忘记选择图片", Toast.LENGTH_SHORT).show();
            }


        } else if (v == mBinding.ivPic) {
            mBinding.ivPic.setVisibility(View.GONE);
            mBinding.pulishRmark.setVisibility(View.VISIBLE);
        } else if (v == mBinding.skillPic) {
            if (mBinding.buttonContainer.getVisibility() == View.INVISIBLE && mBitmap != null) {
                mBinding.ivPic.setVisibility(View.VISIBLE);
                mBinding.pulishRmark.setVisibility(View.GONE);
            }

        } else {

            Intent intent = new Intent();
            mFile = maimeng.yodian.app.client.android.utils.FileUtils.createFile("temp.jpg");
            if (mTempUri == null) mTempUri = Uri.fromFile(mFile);

            if (v == mBinding.btnAlbum) {
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
            } else if (v == mBinding.btnCamera) {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempUri);
                startActivityForResult(intent, REQUEST_CODE_CAMMRA);
            }

        }
    }

    /***
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mBinding.buttonContainer.setVisibility(View.VISIBLE);
        } else {
            mBinding.buttonContainer.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (requestCode == REQUEST_CODE_ALBUM) {
                uri = data.getData();
            } else {
                uri = mTempUri;
            }

            mBinding.cheSelectPhoto.setChecked(false);


            mBitmap = getScaledBitmap(uri);
            mBinding.skillPic.setImageBitmap(mBitmap);
            mBinding.ivPic.setImageBitmap(mBitmap);
            if(mBitmap!=null){
                LogUtil.d(LOG_TAG ,"mBitmap bytecount: %d,width: %d,rowByte: %d",mBitmap.getByteCount(),mBitmap.getWidth(),mBitmap.getRowBytes());
            }


        }
    }

    private Bitmap getScaledBitmap(Uri uri) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        options.inSampleSize=1;
        BitmapFactory.decodeFile(uri.getPath(),options);
        int height=options.outHeight;
        int width=options.outWidth;
        int temWidth=width;
        int temHeight=0;
        while (true){
            temHeight=temWidth*height/width;
            if(temWidth*temHeight*4<2.5*1024*1024){
                width=temWidth;
                height=temHeight;
                break;
            }
            temWidth-=1;
        }
        Bitmap bitmap=BitmapFactory.decodeFile(uri.getPath());
        if(bitmap!=null){
            Bitmap bmp=Bitmap.createScaledBitmap(bitmap, width, height, false);
            LogUtil.d(LOG_TAG, "Bitmap bytecount: %d,width: %d,rowByte: %d", bitmap.getByteCount(), bitmap.getWidth(), bitmap.getRowBytes());
            return bmp;
        }
        return null;

    }

    /**
     * 照片裁剪
     *
     * @param uri
     */

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高

        intent.putExtra("outputX", 360);
        intent.putExtra("outputY", 360);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempUri);
        startActivityForResult(intent, REQUEST_CODE_CLIPPING);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtil.d(LOG_TAG, "onback");
    }


    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        mTitle.setTextColor(Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
