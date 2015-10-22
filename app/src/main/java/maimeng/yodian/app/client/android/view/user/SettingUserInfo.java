package maimeng.yodian.app.client.android.view.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivitySettingUserInfoBinding;
import maimeng.yodian.app.client.android.model.City;
import maimeng.yodian.app.client.android.model.user.Sex;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
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
    private AlertDialog dialogAlert;
    private boolean updateAvatar = false;
    private Adapter provinceAdapter;
    private Adapter cityAdapter;
    private Adapter districtAdapter;
    private boolean initCity = false;
    private boolean initDistrict = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            ActivityCompat.finishAfterTransition(SettingUserInfo.this);
            finish();
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
        }
    }

    int indexP = 0;
    int indexD = 0;
    int indexC = 0;

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
        user = User.read(this);
        binding.btnSubmit.setOnClickListener(this);
        ((View) binding.userAvatar.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        new ImageLoaderManager.Loader(this, Uri.parse(user.getAvatar())).callback(this).start(this);
        binding.nickname.addTextChangedListener(new EditTextChangeListener(binding.nickname, binding, user));
        binding.sex.addTextChangedListener(new EditTextChangeListener(binding.sex, binding, user));
//        binding.city.addTextChangedListener(new EditTextChangeListener(binding.city, binding, user));
        provinceAdapter = new Adapter(new ArrayList<City>() {{
//            new City("请选择");
        }});
        cityAdapter = new Adapter(new ArrayList<City>() {{
//            new City("请选择");
        }});
        districtAdapter = new Adapter(new ArrayList<City>() {{
//            new City("请选择");
        }});
        binding.province.setAdapter(provinceAdapter);
        binding.city.setAdapter(cityAdapter);
        binding.district.setAdapter(districtAdapter);
        binding.province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City item = provinceAdapter.getItem(position);
                ArrayList<City> sub = item.getSub();
                cityAdapter.reload(sub);
                districtAdapter.datas.clear();
                districtAdapter.notifyDataSetChanged();
                user.getInfo().setProvince(item.getName());
                if (!initCity) {
                    binding.city.setSelection(indexC, true);
                    initCity = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City item = cityAdapter.getItem(position);
                ArrayList<City> sub = item.getSub();
                if (sub != null) {
                    districtAdapter.reload(sub);
                }
                user.getInfo().setCity(item.getName());
                if (!initDistrict) {
                    binding.district.setSelection(indexD, true);
                    initDistrict = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.getInfo().setDistrict(districtAdapter.getItem(position).getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.job.addTextChangedListener(new EditTextChangeListener(binding.job, binding, user));
        binding.signature.addTextChangedListener(new EditTextChangeListener(binding.signature, binding, user));
        binding.wechat.addTextChangedListener(new EditTextChangeListener(binding.wechat, binding, user));
        binding.qq.addTextChangedListener(new EditTextChangeListener(binding.qq, binding, user));
        binding.phone.addTextChangedListener(new EditTextChangeListener(binding.phone, binding, user));
//        binding.nickname.setText(user.getNickname());
//        binding.wechat.setText(user.getWechat());
//        binding.qq.setText(user.getInfo().getQq());
//        binding.phone.setText(user.getInfo().getContact());
        binding.setUser(user);
        binding.executePendingBindings();
        ArrayList<City> datas = readFile();

        if (user.getInfo() != null) {
            User.Info info = user.getInfo();
            String p = info.getProvince();
            String d = info.getDistrict();
            String c = info.getCity();
            for (int i = 0; i < datas.size(); i++) {
                City province = datas.get(i);
                if (province.getName().equals(p)) {
                    indexP = i;
                    ArrayList<City> cities = province.getSub();
                    for (int j = 0; j < cities.size(); j++) {
                        City city = cities.get(j);
                        if (city.getName().equals(c)) {
                            indexC = j;
                            ArrayList<City> districts = city.getSub();
                            if (districts != null) {
                                for (int k = 0; k < districts.size(); k++) {
                                    City district = districts.get(k);
                                    if (district.getName().equals(d)) {
                                        indexD = k;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        binding.province.setSelection(indexP, true);

    }


    private void toggle() {
        if (dialogAlert == null) {
            dialogAlert = new AlertDialog.Builder(this).setTitle("上传方式").setItems(new String[]{"从相册中选择", "拍照"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    PhotoPickerIntent intentPhoto = new PhotoPickerIntent(SettingUserInfo.this);
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

    @Override
    public void onClick(View v) {
        binding.invalidateAll();
        if (mBitmap == null) {
            Toast.makeText(this, R.string.avatar_input_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(user.getNickname())) {
            binding.nickname.setError(getText(R.string.nickname_input_empty_message));
            return;
        }
        boolean wechatEmpty = TextUtils.isEmpty(user.getWechat());
        boolean qqEmpty = TextUtils.isEmpty(user.getInfo().getQq());
        boolean phomeEmpty = TextUtils.isEmpty(user.getInfo().getContact());
        if (phomeEmpty && qqEmpty && wechatEmpty) {
            binding.nickname.setError(getText(R.string.contact_input_empty_message));
            return;
        }
        Sex sex = user.getInfo().getSex();
        if (updateAvatar) {
            service.modifyInfo(user.getNickname(), sex.getCode(), user.getInfo().getJob(), user.getInfo().getSignature(), user.getWechat(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setMaxHeight(540).setMaxWidth(540).build(), user.getInfo().getQq(), user.getInfo().getContact(), user.getInfo().getCity(), user.getInfo().getProvince(), user.getInfo().getDistrict(), this);
        } else {
            service.modifyInfo(user.getNickname(), sex.getCode(), user.getInfo().getJob(), user.getInfo().getSignature(), user.getWechat(), null, user.getInfo().getQq(), user.getInfo().getContact(), user.getInfo().getCity(), user.getInfo().getProvince(), user.getInfo().getDistrict(), this);
        }
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
            if (updateAvatar) {
                user.setAvatar(res.getData().getAvatar());
            }
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
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            ArrayList<String> paths = (ArrayList<String>) data.getSerializableExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            String uri = Uri.fromFile(new File(paths.get(0))).toString();
            startPhotoZoom(Uri.parse(uri));
        } else if (requestCode == REQUEST_PHOTORESOULT && RESULT_OK == resultCode) {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            if (tempFile != null) {
                Uri uri = Uri.fromFile(tempFile);
                new ImageLoaderManager.Loader(this, uri).callback(this).start(this);
                binding.getUser().setAvatar(uri.toString());
                tempFile.deleteOnExit();
                updateAvatar = true;
            }
//            toggle();
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
            if (user.getInfo() == null) {
                user.setInfo(new User.Info());
            }
            String text = s.toString();
            if (mText == binding.nickname) {
                user.setNickname(text);
            } else if (mText == binding.sex) {
                if (TextUtils.isEmpty(text) || text.equals(Sex.NONE.getName())) {
                    user.getInfo().setSex(Sex.NONE);
                } else {
                    if (text.equals(Sex.MAN.getName())) {
                        user.getInfo().setSex(Sex.MAN);
                    } else {
                        user.getInfo().setSex(Sex.WOMAN);
                    }
                }
            } else if (mText == binding.signature) {
                user.getInfo().setSignature(text);
            } else if (mText == binding.job) {
                user.getInfo().setJob(text);
            } else if (mText == binding.wechat) {
                user.setWechat(text);
            } else if (mText == binding.qq) {
                user.getInfo().setQq(text);
            } else if (mText == binding.phone) {
                user.getInfo().setContact(text);
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

    private ArrayList<City> readFile() {
        InputStream input = getResources().openRawResource(R.raw.city);
        InputStreamReader jsr = new InputStreamReader(input);
        ArrayList<City> citys = new Gson().fromJson(jsr, new TypeToken<ArrayList<City>>() {
        }.getType());
        provinceAdapter.reload(citys);
        return citys;
    }

    private static class Adapter extends BaseAdapter implements SpinnerAdapter {
        private ArrayList<City> datas = new ArrayList<>();

        public Adapter(ArrayList<City> cities) {
            this.datas.clear();
            this.datas.addAll(cities);
        }

        public void reload(ArrayList<City> cities) {
            this.datas.clear();
            this.datas.addAll(cities);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public City getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = new TextView(parent.getContext());
                convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            City item = getItem(position);
            TextView current = (TextView) convertView;
            current.setPadding(10, 10, 10, 10);
//            current.setTextSize(1f);
            current.setGravity(Gravity.CENTER);
            String name = item.getName();
            current.setText(name);
            return convertView;
        }
    }
}
