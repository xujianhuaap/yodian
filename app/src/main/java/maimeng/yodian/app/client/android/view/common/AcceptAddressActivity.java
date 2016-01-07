package maimeng.yodian.app.client.android.view.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityAcceptAddressBinding;
import maimeng.yodian.app.client.android.model.Address;
import maimeng.yodian.app.client.android.model.City;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.AddressRespoonse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by xujianhua on 05/01/16.
 */
public class AcceptAddressActivity extends AbstractActivity implements View.OnClickListener{

    public static final int REQUEST_DETAIL_ADDRESS=0x12;
    
    private String provinceStr;
    private String cityStr;
    private String districtStr;
    private String nameStr;
    private String mobileStr;
    private String addressStr;

    private UserService userService;
    private WaitDialog waitDialog;
    private Adapter provinceAdapter;
    private Adapter cityAdapter;
    private Adapter districtAdapter;


    int indexP = 0;
    int indexD = 0;
    int indexC = 0;
    private PopupWindow addressWindow;
    private User.Info info;
    private boolean isModify;
    private ActivityAcceptAddressBinding binding;
    private Address address;
    private WheelView provinceList;
    private WheelView cityList;
    private WheelView districtList;
    private WaitDialog waitDialog1;

    public static void show(Activity activity,int requestCode,Address address){
        Intent intent=new Intent(activity,AcceptAddressActivity.class);
        intent.putExtra("address", Parcels.wrap(address));
        activity.startActivityForResult(intent,requestCode);

    }
    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mTitle.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = bindView(R.layout.activity_accept_address);
        address=Parcels.unwrap(getIntent().getParcelableExtra("address"));
        if(address==null){address=new Address();}
        isModify=!TextUtils.isEmpty(address.getProvince());
        userService = Network.getService(UserService.class);
        binding.name.addTextChangedListener(new TextWatcherProxy(binding.name));
        binding.phone.addTextChangedListener(new TextWatcherProxy(binding.phone));
        binding.submit.setOnClickListener(this);
        binding.city.setOnClickListener(this);
        binding.address.setOnClickListener(this);

        final ArrayList<City> datas = readFile();
        initWheelViewWindow();
        initWheelViewUI(datas);
        addWheelViewChangListener();
        User user= User.read(this);
        if(user!=null){
            info=user.getInfo();
        }
        if(!isModify){
            address.setProvince(info.getProvince());
            address.setCity(info.getCity());
            address.setDistrict(info.getDistrict());
            address.setName(user.getNickname());
            address.setMobile(info.getMobile());
        }
        binding.setAddress(address);
        initWheelViewIndex(address, datas);
        setWheelViewIndex(indexP, indexC, indexD);

    }

    /***
     *
     */
    private void initWheelViewWindow() {
        View view = getLayoutInflater().inflate(R.layout.pop_cities, null, false);
        provinceList = (WheelView) view.findViewById(R.id.province);
        cityList = (WheelView) view.findViewById(R.id.city);
        districtList = (WheelView) view.findViewById(R.id.district);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideaddressWindow();
            }
        });
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAddressByWheelView();
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        addressWindow = new PopupWindow(view, displayMetrics.widthPixels, displayMetrics.heightPixels / 3, true);
        addressWindow.setTouchable(true);

    }

    /***
     *
     * @param indexP
     * @param indexC
     * @param indexD
     */
    private void setWheelViewIndex(int indexP,int indexC,int indexD) {
        provinceList.setCurrentItem(indexP);
        cityList.setCurrentItem(indexC);
        districtList.setCurrentItem(indexD);
    }

    /***
     *
     */
    private void addWheelViewChangListener() {
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
    }

    /***
     * 配置滑轮的UI
     * @param datas
     */
    private void initWheelViewUI( ArrayList<City> datas) {
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
    }

    /****
     * 初始化 三个滑轮的显示位置
     * @param address
     * @param datas
     */
    private void initWheelViewIndex(Address address, ArrayList<City> datas) {
        if (address != null) {
            String p = address.getProvince().replace("省","").trim();
            String d = address.getDistrict();
            String c = address.getCity().replace("市","").trim();
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
    }


    /***
     * 转动滑轮选取地址信息
     */
    private void chooseAddressByWheelView() {
        City province = provinceAdapter.getDatas().get(provinceList.getCurrentItem());
        City city = cityAdapter.getDatas().get(cityList.getCurrentItem());
        City district;
        if (cityList.getCurrentItem() > 0) {
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
            provinceStr=p;
            cityStr=p;
            districtStr=c;
        } else {
            provinceStr=p+"省";
            cityStr=c;
            districtStr=d;

        }
        cityStr=cityStr+"市";
        address.setProvince(provinceStr);
        address.setCity(cityStr);
        address.setDistrict(districtStr);
        binding.setAddress(address);

        hideaddressWindow();
    }


    private ArrayList<City> readFile() {
        InputStream input = getResources().openRawResource(R.raw.city);
        InputStreamReader jsr = new InputStreamReader(input);
        ArrayList<City> citys = new Gson().fromJson(jsr, new TypeToken<ArrayList<City>>() {
        }.getType());
        return citys;
    }

    private void showaddressWindow() {
        if (addressWindow != null) {
            if (!addressWindow.isShowing()) {
                addressWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
            }
        }
    }

    private void hideaddressWindow() {
        if (addressWindow != null) {
            if (addressWindow.isShowing()) {
                addressWindow.dismiss();
            }
        }
    }


    @Override
    public void onClick(View v) {

        if(v==binding.submit){
            if(TextUtils.isEmpty(address.getName())){
                Toast.makeText(AcceptAddressActivity.this,"收货人姓名不能为空",Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(address.getMobile())){
                Toast.makeText(AcceptAddressActivity.this,"收货人电话不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(address.getAddress())){
                Toast.makeText(AcceptAddressActivity.this,"收货人详细地址不能为空",Toast.LENGTH_SHORT).show();
                return;
            }


            userService.setAddress(address.getProvince(), address.getCity(), address.getDistrict(), address.getAddress(), address.getName(), address.getMobile(), new Callback<ToastResponse>() {
                @Override
                public void start() {
                    if (waitDialog == null) {
                        waitDialog = WaitDialog.show(AcceptAddressActivity.this);
                    }
                }

                @Override
                public void success(ToastResponse toastResponse, Response response) {
                    Toast.makeText(AcceptAddressActivity.this, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, getIntent().putExtra("address", Parcels.wrap(address)));
                    finish();
                }

                @Override
                public void failure(HNetError hNetError) {
                    ErrorUtils.checkError(AcceptAddressActivity.this, hNetError);
                }

                @Override
                public void end() {
                    if (waitDialog != null) {
                        waitDialog.dismiss();
                    }
                }
            });
        }else if(v==binding.city){
            showaddressWindow();
        }else if(v==binding.address){
            DetailAddressActivity.show(AcceptAddressActivity.this,REQUEST_DETAIL_ADDRESS);
        }


    }

    public final class TextWatcherProxy implements TextWatcher{

        private EditText view;

        public TextWatcherProxy(EditText view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
                if(view==binding.name){
                    nameStr=binding.name.getText().toString();
                    address.setName(nameStr);
                }else if(view==binding.phone){
                    mobileStr=binding.phone.getText().toString();
                    address.setMobile(mobileStr);
                }
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_DETAIL_ADDRESS){
                if(data!=null){
                    addressStr= data.getStringExtra("address");
                    if(!TextUtils.isEmpty(addressStr)){
                        address.setAddress(addressStr);
                        binding.setAddress(address);
                    }

                }
            }
        }
    }
}
