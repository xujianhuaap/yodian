package maimeng.yodian.app.client.android.view.user;

import android.app.AlertDialog;
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
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

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.databinding.ActivitySettingUserInfoBinding;
import maimeng.yodian.app.client.android.model.City;
import maimeng.yodian.app.client.android.model.user.Sex;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * Created by android on 15-7-20.
 */
public class SettingUserInfo extends AbstractActivity implements View.OnClickListener, Callback<ModifyUserResponse> {
    private static final String LOG_TAG = SettingUserInfo.class.getSimpleName();
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
    private PopupWindow bankList;
    private User oldUser;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(!checkChange()) {
//            ActivityCompat.finishAfterTransition(SettingUserInfo.this);
                finish();
            }
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

    private void showBankList() {
        if (bankList != null) {
            if (!bankList.isShowing()) {
                bankList.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
            }
        }
    }

    private void hideBankList() {
        if (bankList != null) {
            if (bankList.isShowing()) {
                bankList.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        tempFile = new File(dir, getPhotoFileName());
        service = Network.getService(UserService.class);
        binding = bindView(R.layout.activity_setting_user_info);
        binding.cities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankList();
            }
        });
        user = User.read(this);
        YApplication.getInstance().setAuthUser(null);
        oldUser = User.read(this);
        YApplication.getInstance().setAuthUser(user);
        binding.btnSubmit.setOnClickListener(this);
        ((View) binding.userAvatar.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        binding.nickname.addTextChangedListener(new EditTextChangeListener(binding.nickname, binding, user));

        View view = getLayoutInflater().inflate(R.layout.pop_cities, null, false);
        final WheelView provinceList = (WheelView) view.findViewById(R.id.province);
        final WheelView cityList = (WheelView) view.findViewById(R.id.city);
        final WheelView districtList = (WheelView) view.findViewById(R.id.district);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBankList();
            }
        });
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                City province = provinceAdapter.getDatas().get(provinceList.getCurrentItem());
                int currentItem = cityList.getCurrentItem();
                ArrayList<City> datas = cityAdapter.getDatas();
                if(currentItem<datas.size()) {
                    City city = datas.get(currentItem);
                    City district;
                    if (currentItem > 0) {
                        ArrayList<City> districts = districtAdapter.getDatas();
                        if (districts.size() > 0) {
                            district = districts.get(districtList.getCurrentItem());
                        } else {
                            district = new City();
                        }

                    } else {
                        district = new City();
                        district.setName("请选择");
                    }
                    final String p;
                    final String c;
                    final String d;
                    if ("请选择".equals(province.getName())) {
                        p = "";
                    } else {
                        p = province.getName();
                    }
                    if ("请选择".equals(city.getName())) {
                        c = "";
                    } else {
                        c = city.getName();
                    }
                    if ("请选择".equals(district.getName())) {
                        d = "";
                    } else {
                        d = district.getName();

                    }
                    int type = province.getType();
                    if (type == 0) {
                        //直辖市
                        user.getInfo().setProvince(p);
                        user.getInfo().setCity(p);
                        user.getInfo().setDistrict(c);
                        binding.cities.setText(String.format("%1$s\t\t%2$s市\t\t%3$s",p,p,c));
                    } else {
                        user.getInfo().setProvince(p);
                        user.getInfo().setCity(c);
                        user.getInfo().setDistrict(d);
                        binding.cities.setText(String.format("%1$s省\t\t%2$s市\t\t%3$s",p,c,d));
                    }

                }
                hideBankList();
            }
        });
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
        provinceAdapter = new Adapter(this, datas);
        cityAdapter = new Adapter(this, new ArrayList<City>());
        districtAdapter = new Adapter(this, new ArrayList<City>());
        provinceList.setVisibleItems(6); // Number of items
        provinceList.setWheelBackground(android.R.color.white);
        provinceList.setShadowColor(0x00000000, 0x00000000, 0x00000000);
        provinceList.setViewAdapter(provinceAdapter);

        cityList.setVisibleItems(6); // Number of items
        cityList.setWheelBackground(android.R.color.white);
        cityList.setShadowColor(0x00000000, 0x00000000, 0x00000000);
        cityList.setViewAdapter(cityAdapter);

        districtList.setVisibleItems(6); // Number of items
        districtList.setWheelBackground(android.R.color.white);
        districtList.setShadowColor(0x00000000, 0x00000000, 0x00000000);
        districtList.setViewAdapter(districtAdapter);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        bankList = new PopupWindow(view, displayMetrics.widthPixels, displayMetrics.heightPixels / 3, true);
        bankList.setTouchable(true);
        provinceList.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                City item = provinceAdapter.getDatas().get(newValue);
                ArrayList<City> sub = item.getSub();
                cityAdapter.reload(sub);
                cityList.setCurrentItem(0, true);
                districtAdapter.reload(new ArrayList<City>());
                districtList.setCurrentItem(-1);
            }
        });
        cityList.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                City item = cityAdapter.getDatas().get(newValue);
                ArrayList<City> sub = item.getSub();
                if (sub != null) {
                    districtAdapter.reload(sub);
                }
            }
        });
        provinceList.setCurrentItem(indexP);
        cityList.setCurrentItem(indexC);
        districtList.setCurrentItem(indexD);
        binding.job.addTextChangedListener(new EditTextChangeListener(binding.job, binding, user));
        binding.signature.addTextChangedListener(new EditTextChangeListener(binding.signature, binding, user));
        binding.signature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.signatureCount.setText(String.format("%d/%d", s.length(), 30));
            }
        });
        binding.wechat.addTextChangedListener(new EditTextChangeListener(binding.wechat, binding, user));
        binding.qq.addTextChangedListener(new EditTextChangeListener(binding.qq, binding, user));
        binding.phone.addTextChangedListener(new EditTextChangeListener(binding.phone, binding, user));
//        binding.nickname.setText(user.getNickname());
//        binding.wechat.setText(user.getWechat());
//        binding.qq.setText(user.getInfo().getQq());
//        binding.phone.setText(user.getInfo().getContact());
        binding.setUser(user);
        binding.executePendingBindings();


        binding.sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext()).setItems(new String[]{"保密", "男", "女"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                binding.sex.setText(Sex.NONE.getName());
                                binding.getUser().getInfo().setSex(Sex.NONE);
                                break;
                            case 1:
                                binding.sex.setText(Sex.MAN.getName());
                                binding.getUser().getInfo().setSex(Sex.MAN);
                                break;
                            case 2:
                                binding.sex.setText(Sex.WOMAN.getName());
                                binding.getUser().getInfo().setSex(Sex.WOMAN);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).show();
            }
        });

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
    private boolean checkChange(){
        boolean changed=false;
        User.Info oi = oldUser.getInfo();
        User.Info info = user.getInfo();
        if(!oldUser.getNickname().equals(user.getNickname())){
            changed=true;
        } else if(oi.getSex()!=info.getSex()){
            changed=true;
        } else if(!oi.getCity().equals(info.getCity())){
            changed=true;
        } else if(!oi.getDistrict().equals(info.getDistrict())){
            changed=true;
        } else if(!oi.getProvince().equals(info.getProvince())){
            changed=true;
        } else if(!oi.getJob().equals(info.getJob())){
            changed=true;
        } else if(!oi.getSignature().equals(info.getSignature())){
            changed=true;
        } else if(!oi.getWechat().equals(info.getWechat())){
            changed=true;
        } else if(!oi.getQq().equals(info.getQq())){
            changed=true;
        } else if(!oi.getContact().equals(info.getContact())){
            changed=true;
        }
        if(updateAvatar || changed){
            maimeng.yodian.app.client.android.view.dialog.AlertDialog confim = maimeng.yodian.app.client.android.view.dialog.AlertDialog.newInstance("提示", "你已修改资料是否保存");
            confim.setCancelable(false);
            confim.setNegativeListener(new maimeng.yodian.app.client.android.view.dialog.AlertDialog.NegativeListener() {
                @Override
                public void onNegativeClick(DialogInterface dialog) {
                    dialog.dismiss();
                    onClick(null);
                }

                @Override
                public String negativeText() {
                    return "是";
                }
            });
            confim.setPositiveListener(new maimeng.yodian.app.client.android.view.dialog.AlertDialog.PositiveListener() {
                @Override
                public void onPositiveClick(DialogInterface dialog) {
                    dialog.dismiss();
                    finish();
                }

                @Override
                public String positiveText() {
                    return "否";
                }
            });
            confim.show(getFragmentManager(),"confim");
            return true;
        }
        return changed;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && checkChange()){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        binding.invalidateAll();
        mBitmap = binding.userAvatar.getBitmap();
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
            Toast.makeText(this, R.string.contact_input_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Sex sex = user.getInfo().getSex();
        String distr = user.getInfo().getDistrict();
        if (TextUtils.isEmpty(distr)) {
            distr = "";
        }
        if (updateAvatar) {
            TypedBitmap build = new TypedBitmap.Builder(mBitmap).setSize(540).build();
            service.modifyInfo(user.getNickname(), sex.getCode(), user.getInfo().getJob() + "", user.getInfo().getSignature() + "", user.getWechat() + "", build, user.getInfo().getQq() + "", user.getInfo().getContact() + "", user.getInfo().getCity(), user.getInfo().getProvince(), distr, this);
        } else {
            service.modifyInfo(user.getNickname(), sex.getCode(), user.getInfo().getJob() + "", user.getInfo().getSignature() + "", user.getWechat() + "", user.getInfo().getQq() + "", user.getInfo().getContact() + "", user.getInfo().getCity(), user.getInfo().getProvince(), distr, this);
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
                tempFile.deleteOnExit();
            }
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
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
                binding.userAvatar.setImageURI(uri);
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
        return citys;
    }

    private static class Adapter extends AbstractWheelTextAdapter {
        public ArrayList<City> getDatas() {
            return datas;
        }

        private ArrayList<City> datas = new ArrayList<>();

        public Adapter(Context context, ArrayList<City> cities) {
            super(context);
            this.datas.clear();
            this.datas.addAll(cities);
        }

        public void reload(ArrayList<City> cities) {
            this.datas.clear();
            this.datas.addAll(cities);
//            notifyDataChangedEvent();
            notifyDataInvalidatedEvent();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return datas.get(index).getName();
        }

        @Override
        public int getItemsCount() {
            return datas.size();
        }
    }
}
