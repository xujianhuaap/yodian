package maimeng.yodian.app.client.android.view.skill;

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

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.databinding.ActivityCreateSkillBinding;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillAllResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 *
 */
public class CreateOrEditSkillActivity extends AppCompatActivity implements Target {
    private static final int REQUEST_AUTH = 0x1001;
    private static final int REQUEST_SELECT_PHOTO = 0x2001;
    private static final int REQUEST_DONE = 0x1003;
    private SkillService service;
    private ActivityCreateSkillBinding binding;
    private Bitmap mBitmap;
    private boolean isEdit = false;
    private WaitDialog dialog;
    private File tempFile;
    private ShareDialog mShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        tempFile = new File(dir, getPhotoFileName());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_skill);
        final SkillTemplate mTemplate;
        if (getIntent().hasExtra("template")) {
            mTemplate = getIntent().getParcelableExtra("template");
            ViewCompat.setTransitionName(binding.skillPic, "avatar");
            ViewCompat.setTransitionName(binding.skillName, "title");
        } else {
            mTemplate = new SkillTemplate();
            if (getIntent().hasExtra("skill")) {
                Skill skill = getIntent().getParcelableExtra("skill");
                mTemplate.setPic(skill.getPic());
                mTemplate.setUnit(skill.getUnit());
                mTemplate.setPrice(skill.getPrice());
                mTemplate.setName(skill.getName());
                mTemplate.setContent(skill.getContent());
                mTemplate.setCreatetime(skill.getCreatetime());
                mTemplate.setId(skill.getId());
                mTemplate.setStatus(skill.getStatus());
                isEdit = true;
            }

        }
        if (mTemplate.getPic() != null) Network.image(this, mTemplate.getPic(), this);
        binding.setTemplate(mTemplate);
        binding.skillName.addTextChangedListener(new EditTextChangeListener(binding.skillName, binding, mTemplate));
        binding.skillContent.addTextChangedListener(new EditTextChangeListener(binding.skillContent, binding, mTemplate));
        binding.skillPrice.addTextChangedListener(new EditTextChangeListener(binding.skillPrice, binding, mTemplate));
        binding.skillUnit.addTextChangedListener(new EditTextChangeListener(binding.skillUnit, binding, mTemplate));
        binding.btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
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
        service = Network.getService(SkillService.class);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(CreateOrEditSkillActivity.this);
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doDone();
            }
        });
        binding.btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intentPhoto = new PhotoPickerIntent(CreateOrEditSkillActivity.this);
                intentPhoto.setPhotoCount(1);
                intentPhoto.setShowCamera(false);
                startActivityForResult(intentPhoto, REQUEST_SELECT_PHOTO);
            }
        });
        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intentPhoto = new PhotoPickerIntent(CreateOrEditSkillActivity.this);
                intentPhoto.setPhotoCount(1);
                intentPhoto.setShowCamera(true);
                startActivityForResult(intentPhoto, REQUEST_SELECT_PHOTO);
            }
        });
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

    private void doDone() {
        final SkillTemplate template = binding.getTemplate();
        if (TextUtils.isEmpty(template.getName())) {
            binding.getTemplate();
            binding.skillName.setError(getText(R.string.create_not_empty_name));
            return;
        }
        if (TextUtils.isEmpty(template.getContent())) {
            binding.skillContent.setError(getText(R.string.create_not_empty_content));
            return;
        }
        if (TextUtils.isEmpty(template.getPrice())) {
            binding.skillPrice.setError(getText(R.string.create_not_empty_pirce));
            return;
        }
        if (TextUtils.isEmpty(template.getUnit())) {
            binding.skillUnit.setError(getText(R.string.create_not_empty_unit));
            return;
        }
        if (mBitmap == null) {
            Toast.makeText(this, R.string.create_not_empty_pic, Toast.LENGTH_SHORT).show();
            return;
        }


        final Skill skill = new Skill();
        skill.setPic(template.getPic());
        skill.setId(template.getId());
        skill.setName(template.getName());
        skill.setContent(template.getContent());
        skill.setPrice(template.getPrice());
        skill.setUnit(template.getUnit());
        skill.setNickname(User.read(this).getT_nickname());

        if (isEdit) {
            service.update(template.getId(), template.getName(), template.getContent(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setAutoMatch(getResources()).build(), template.getPrice(), template.getUnit(), new ToastCallback(this) {
                @Override
                public void success(ToastResponse res, Response response) {
                    super.success(res, response);
                    if (res.isSuccess()) {
                        Skill skill = getIntent().getParcelableExtra("skill");
                        skill.setPic(template.getPic());
                        skill.setUnit(template.getUnit());
                        skill.setPrice(template.getPrice());
                        skill.setName(template.getName());
                        skill.setContent(template.getContent());
                        skill.setCreatetime(template.getCreatetime());
                        skill.setStatus(template.getStatus());
                        Intent data = new Intent();
                        data.putExtra("skill", skill);
                        setResult(RESULT_OK, data);
                        finish();
                    } else if (res.isValidateAuth(CreateOrEditSkillActivity.this, REQUEST_AUTH)) ;
                }

                @Override
                public void start() {
                    super.start();
                    dialog = WaitDialog.show(CreateOrEditSkillActivity.this);

                }

                @Override
                public void end() {
                    super.end();
                    if (dialog != null) dialog.dismiss();
                }
            });
        } else {
            service.add(template.getName(), template.getContent(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setAutoMatch(getResources()).build(), template.getPrice(), template.getUnit(), new Callback<SkillAllResponse>() {
                @Override
                public void success(SkillAllResponse res, Response response) {
                    if (res.isSuccess()) {
                        final Skill newSkill = res.getData();
                        if (mShareDialog == null) {
                            ShareDialog.ShareParams shareParams = new ShareDialog.ShareParams(newSkill,
                                    newSkill.getQrcodeUrl(), newSkill.getUid(), newSkill.getNickname(), "");
                            mShareDialog = ShareDialog.show(CreateOrEditSkillActivity.this, shareParams, 1);
                            mShareDialog.setListener(new ShareDialog.Listener() {
                                @Override
                                public void onClose() {
                                    setResult(RESULT_OK, new Intent().putExtra("skill", newSkill));
                                    finish();
                                }
                            });
                        }
                    } else if (res.isValidateAuth(CreateOrEditSkillActivity.this, REQUEST_AUTH)) {

                    } else {
                        res.showMessage(CreateOrEditSkillActivity.this);
                    }
                }

                @Override
                public void failure(HNetError hNetError) {
                    ErrorUtils.checkError(CreateOrEditSkillActivity.this, hNetError);
                }

                @Override
                public void start() {
                    dialog = WaitDialog.show(CreateOrEditSkillActivity.this);

                }

                @Override
                public void end() {
                    if (dialog != null) dialog.dismiss();
                }
            });
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
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        final int width = binding.skillPic.getWidth();
        final int height = binding.skillPic.getHeight();
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        tempFile = new File(dir, getPhotoFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_PHOTORESOULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_AUTH) {
                doDone();
            } else if (requestCode == REQUEST_SELECT_PHOTO) {
                ArrayList<String> paths = (ArrayList<String>) data.getSerializableExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                String uri = Uri.fromFile(new File(paths.get(0))).toString();
                startPhotoZoom(Uri.parse(uri));

            } else if (requestCode == REQUEST_PHOTORESOULT) {
                if (tempFile != null) {
                    final String url = Uri.fromFile(tempFile).toString();
                    int height = binding.skillPic.getHeight();
                    int width = binding.skillPic.getWidth();
                    if (width > 0 && height > 0) {
                        Network.image(this, url, this, 240, 240);
                    }
                    binding.getTemplate().setPic(url);
                    toggle();
                    tempFile.deleteOnExit();
                }
            } else if (resultCode == REQUEST_DONE) {

            }
        }

    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        this.mBitmap = bitmap;
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    class EditTextChangeListener implements TextWatcher {
        private final EditText mText;
        private final SkillTemplate mTemplate;
        private final ActivityCreateSkillBinding mBinding;

        EditTextChangeListener(EditText text, ActivityCreateSkillBinding binding, SkillTemplate template) {
            this.mText = text;
            this.mBinding = binding;
            this.mTemplate = template;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            final String name = s.toString();
            if (mText == binding.skillContent) {
                mTemplate.setContent(name);
                binding.contentCount.setText(name.length() + "/200");
            } else if (mText == binding.skillName) {
                mTemplate.setName(name);
                binding.titleCount.setText(name.length() + "/25");
            } else if (mText == binding.skillPrice) {
                String temp = s.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
                mTemplate.setPrice(s.toString());
            } else if (mText == binding.skillUnit) {
                mTemplate.setUnit(name);
            }
        }
    }
}
