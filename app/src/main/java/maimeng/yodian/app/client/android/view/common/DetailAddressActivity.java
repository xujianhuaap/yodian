package maimeng.yodian.app.client.android.view.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import maimeng.yodian.app.client.android.R;

/**
 * Created by xujianhua on 06/01/16.
 */
public class DetailAddressActivity extends AbstractActivity{

    private EditText address;
    private TextView textNum;
    public static void show(Activity activity,String address,int requestCode){
        Intent intent=new Intent(activity,DetailAddressActivity.class);
        intent.putExtra("address",address);
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
            Intent intent=getIntent();
            intent.putExtra("address",address.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_address);
        String addressStr=getIntent().getStringExtra("address");
        address=(EditText)findViewById(R.id.address);
        textNum=(TextView)findViewById(R.id.text_num);
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String addressStr=address.getText().toString();
                int len=addressStr.length();
                if(len<81){
                    textNum.setText(len+"/80");
                }else {
                    Toast.makeText(DetailAddressActivity.this,"地址信息不能超过80个字",Toast.LENGTH_SHORT).show();
                }

            }
        });
        address.setText(addressStr);

    }
}
