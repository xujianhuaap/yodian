package maimeng.yodian.app.client.android.view.skill;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.adapters.TextViewBindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.FileUtils;
import com.squareup.picasso.Picasso;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.io.File;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.databinding.ActivityRmarkPublishBinding;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 15-8-6.
 */
public class RmarkPublishActivity extends AppCompatActivity implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener{

    private static final String IMAGE_UNSPECIFIED ="image/*" ;
    private static final String LOG_TAG =RmarkPublishActivity.class.getName() ;
    private ActivityRmarkPublishBinding mBinding;

    private final int REQUEST_CODE_CAMMRA=0x23;
    private final int REQUEST_CODE_CLIPPING=0x24;
    private final int REQUEST_CODE_ALBUM=0x25;
    private Uri mTempUri;
    private File mFile;
    private SkillService mSkillService;
    private Skill mSkill;

    public static void show(Context context,Skill skill){
        Intent intent=new Intent();
        intent.putExtra("skill",skill);
        intent.setClass(context, RmarkPublishActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_rmark_publish);
        mSkill = getIntent().getParcelableExtra("skill");
        mSkillService = Network.getService(SkillService.class);

        EditTextProxy editTextProxy=new EditTextProxy();

        mBinding.fabGoback.setOnClickListener(this);
        mBinding.fabSubmit.setOnClickListener(this);
        mBinding.btnCamera.setOnClickListener(this);
        mBinding.btnAlbum.setOnClickListener(this);
        mBinding.cheSelectPhoto.setOnCheckedChangeListener(this);
        mBinding.editDiary.addTextChangedListener(editTextProxy);
        mBinding.editDiary.setOnKeyListener(editTextProxy);

    }

    private void refresh(Skill skill){

        Bitmap bitmap=BitmapFactory.decodeFile(skill.getPic());
        if(bitmap!=null){
            TypedBitmap typedBitmap=new TypedBitmap.Builder(bitmap,360,360).build();
            mSkillService.add_rmark(mSkill.getId(),skill.getContent(),typedBitmap,new ToastCallBack());
        }

    }

    private class ToastCallBack implements Callback<ToastResponse>{
        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {

            if(toastResponse.isSuccess()){
                Toast.makeText(RmarkPublishActivity.this,"日记上传成功",Toast.LENGTH_SHORT).show();
                mFile.delete();
                mTempUri=null;
            }
        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(RmarkPublishActivity.this,hNetError);
        }

        @Override
        public void end() {

        }
    }

    /***
     * 
     */
    private class EditTextProxy implements TextWatcher,View.OnKeyListener{

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            LogUtil.d(LOG_TAG,"keycode"+keyCode);
            if(keyCode==66){
                InputMethodManager inputMethodService= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodService.hideSoftInputFromInputMethod(mBinding.editDiary.getWindowToken(),0);
            }
            return false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
                    LogUtil.d(LOG_TAG,"count"+count);
                    mBinding.tvNumber.setText("" + s.length() + " / 150");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
   
    
    

    @Override
    public void onClick(View v) {

        if(v==mBinding.fabGoback){
            finish();
        }else if(v==mBinding.fabSubmit){
            if(mFile!=null){
                String content=mBinding.editDiary.getText().toString();
                if(content!=null){
                    String path=mFile.getAbsolutePath();
                    if(content!=null&&path!=null){
                        Skill skill=new Skill();
                        skill.setPic(path);
                        skill.setContent(content);
                        refresh(skill);
                    }
                }else {
                    Toast.makeText(this,"您还没编辑日记内容",Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this,"不要忘记选择图片",Toast.LENGTH_SHORT).show();
            }


        }else {

            Intent intent=new Intent();
            mFile = maimeng.yodian.app.client.android.utils.FileUtils.createFile("temp.jpg");
            if(mTempUri==null) mTempUri = Uri.fromFile(mFile);

            if(v==mBinding.btnAlbum){
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
            }else if(v==mBinding.btnCamera){
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mTempUri );
                startActivityForResult(intent, REQUEST_CODE_CAMMRA);
            }

        }
    }

    /***
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            mBinding.buttonContainer.setVisibility(View.VISIBLE);
        }else{
            mBinding.buttonContainer.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CODE_CLIPPING){
                Bitmap bitmap=BitmapFactory.decodeFile(mTempUri.getPath());
                mBinding.cheSelectPhoto.setChecked(false);
                mBinding.skillPic.setImageBitmap(bitmap);
            }else if(requestCode==REQUEST_CODE_ALBUM){
                startPhotoZoom(data.getData());
            }else{
                startPhotoZoom(mTempUri);
            }

        }
    }

    /**
     *  照片裁剪
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
        LogUtil.d(LOG_TAG,"onback");
    }
}