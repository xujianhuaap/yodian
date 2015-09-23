package maimeng.yodian.app.client.android.view.deal;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityBindBankBinding;
import maimeng.yodian.app.client.android.model.remainder.BindBank;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * 绑定银行卡表单界面
 */
public class BindBankActivity extends AbstractActivity implements View.OnClickListener {
    private ActivityBindBankBinding binding;
    private final BindBank bank = new BindBank();

    private class InputListener implements TextWatcher {
        private final EditText mEdit;

        InputListener(EditText edit) {
            mEdit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            if (mEdit == binding.bank) {
                bank.setName(value);
            } else if (mEdit == binding.branch) {
                bank.setBranch(value);
            } else if (mEdit == binding.idcard) {
                bank.setIdcard(value);
            } else if (mEdit == binding.number) {
                bank.setNumber(value);
            } else if (mEdit == binding.number2) {
                bank.setNumber2(value);
            } else if (mEdit == binding.phone) {
                bank.setPhone(value);
            } else if (mEdit == binding.username) {
                bank.setUsername(value);
            } else if (mEdit == binding.valicode) {
                bank.setValicode(value);
            }
            binding.btnSubmit.setEnabled(checkNotNull());
        }

        private boolean checkNotNull() {
            if (TextUtils.isEmpty(bank.getName())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getBranch())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getIdcard())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getNumber())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getNumber2())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getPhone())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getUsername())) {
                return false;
            }
            if (TextUtils.isEmpty(bank.getValicode())) {
                return false;
            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_bind_bank, null, false);
        setContentView(binding.getRoot());
        binding.setBank(bank);
        binding.btnGetcode.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.bank.addTextChangedListener(new InputListener(binding.bank));
        binding.branch.addTextChangedListener(new InputListener(binding.branch));
        binding.idcard.addTextChangedListener(new InputListener(binding.idcard));
        binding.number.addTextChangedListener(new InputListener(binding.number));
        binding.number2.addTextChangedListener(new InputListener(binding.number2));
        binding.phone.addTextChangedListener(new InputListener(binding.phone));
        binding.username.addTextChangedListener(new InputListener(binding.username));
        binding.valicode.addTextChangedListener(new InputListener(binding.valicode));
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            System.out.println(bank);
        }
    }
}
