package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.regex.Pattern;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.databinding.ActivityBindBankBinding;
import maimeng.yodian.app.client.android.model.remainder.Bank;
import maimeng.yodian.app.client.android.model.remainder.BindBank;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.BankListResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.BankService;
import maimeng.yodian.app.client.android.utils.bank.checkBankCard;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * 绑定银行卡表单界面
 */
public class BindBankActivity extends AbstractActivity implements View.OnClickListener, AbstractAdapter.ViewHolderClickListener<BindBankActivity.ViewHolder> {
    private ActivityBindBankBinding binding;
    private final BindBank bank = new BindBank();
    private BankService service;
    private InputMethodManager inputmanger;
    private Toast toast;

    @Override
    public void onItemClick(ViewHolder holder, int postion) {
        this.bank.setBankName(holder.getBank().getName());
        this.bank.setId(holder.getBank().getId());
        binding.bank.setText(this.bank.getBankName());
        hideBankList();
    }

    @Override
    public void onClick(ViewHolder holder, View clickItem, int postion) {

    }

    private class InputListener implements TextWatcher {
        private final TextView mEdit;

        InputListener(TextView edit) {
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
            if (mEdit == binding.branch) {
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
            if (TextUtils.isEmpty(bank.getBankName()) || bank.getBankId() <= 0) {
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

    private PopupWindow bankList;
    private BankListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding = bindView(R.layout.activity_bind_bank);
        binding.setBank(bank);
        binding.btnGetcode.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.bank.setOnClickListener(this);
        binding.bank.addTextChangedListener(new InputListener(binding.bank));
        binding.branch.addTextChangedListener(new InputListener(binding.branch));
        binding.idcard.addTextChangedListener(new InputListener(binding.idcard));
        binding.number.addTextChangedListener(new InputListener(binding.number));
        binding.number2.addTextChangedListener(new InputListener(binding.number2));
        binding.phone.addTextChangedListener(new InputListener(binding.phone));
        binding.username.addTextChangedListener(new InputListener(binding.username));
        binding.valicode.addTextChangedListener(new InputListener(binding.valicode));
        service = Network.getService(BankService.class);
        View view = getLayoutInflater().inflate(R.layout.pop_bank_list, null, false);
        RecyclerView bankListView = (RecyclerView) view.findViewById(R.id.list);
        adapter = new BankListAdapter(this, this);
        bankListView.setLayoutManager(new LinearLayoutManager(this));
        bankListView.setAdapter(adapter);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        bankList = new PopupWindow(view, displayMetrics.widthPixels, displayMetrics.heightPixels / 3, true);
        bankList.setBackgroundDrawable(new BitmapDrawable());
        bankList.setTouchable(true);
        syncBankList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (bankList != null && bankList.isShowing()) {
            hideBankList();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    CountDownTimer timeout;
    Pattern p = Pattern.compile("^170|((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    @Override
    public void onClick(View v) {

        if (v == binding.btnSubmit) {
            if (checkBankInfo()) {
                service.bind(bank.getId(), bank.getNumber(), bank.getBranch(), bank.getIdcard(),
                        bank.getPhone(), bank.getUsername(), bank.getValicode(),
                        new ToastCallback(this) {
                            @Override
                            public void success(ToastResponse res, Response response) {
                                super.success(res, response);
                                if (res.isSuccess()) {
                                    if (getIntent().getBooleanExtra("result", false)) {
                                        setResult(RESULT_OK);
                                    } else {
                                        startActivity(new Intent(BindBankActivity.this, BindBankCompliteActivity.class));
                                    }
                                    finish();
                                }
                            }
                        });
            }
        } else if (v == binding.btnGetcode) {
            if (TextUtils.isEmpty(bank.getPhone()) || !p.matcher(bank.getPhone()).matches()) {
                toast("手机号无效!");
            }
            service.getcode(bank.getPhone(), new ToastCallback(this) {
                @Override
                public void success(ToastResponse toastResponse, Response response) {
                    super.success(toastResponse, response);
                    if (toastResponse.isSuccess()) {
                        if (timeout == null) {
                            timeout = new CountDownTimer(binding.btnGetcode, 1000);
                        }
                        binding.btnGetcode.setEnabled(false);
                        timeout.start();
                    }
                }
            });
        } else if (v == binding.bank) {
            showBankList();
            if (!initedBankList) {
                Toast.makeText(this, "正在获取银行列表", Toast.LENGTH_SHORT).show();
                syncBankList();
            }
        }
    }

    final int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
    final char[] validate = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值

    /**
     * 检查身份证合法性
     *
     * @param id18
     * @return
     */
    public boolean checkId(String id18) {
        if (TextUtils.isEmpty(id18)) return false;
        if (id18.length() < 15) return false;
        if (id18.length() == 15) return true;//15位身份证无法校验
        if (id18.length() > 15 && id18.length() != 18) return false;
        int sum = 0;
        int mode = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(id18.charAt(i))) * weight[i];
        }
        mode = sum % 11;
        char valicode = validate[mode];
        return valicode == id18.charAt(17);
    }

    private boolean checkBankInfo() {
        String number = bank.getNumber();
        number = number.replaceAll(" ", "");
        if (number.length() != 16 && number.length() != 19) {
            toast("银行卡位数无效");
            return false;
        }
        if (!checkBankCard.checkBankCard(number)) {
            toast("银行卡输入无效");
            return false;
        }
        String number2 = bank.getNumber2();
        if (!bank.getNumber().equals(number2)) {
            toast("银行卡两次输入不一致");
            return false;
        }
        if (!checkId(bank.getIdcard())) {
            toast("身份证无效!");
            return false;
        }

        return true;
    }

    private void toast(String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean initedBankList = false;

    private void syncBankList() {
        service.list(new BanListCallback());
    }

    private void showBankList() {
        if (bankList != null) {
            if (!bankList.isShowing()) {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
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

    private static class CountDownTimer extends android.os.CountDownTimer {
        private final TextView tv;
        private final CharSequence oldText;

        public CountDownTimer(TextView tv, long timeout) {
            super(10 * 1000, timeout);
            this.tv = tv;
            this.oldText = tv.getText();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            tv.setEnabled(true);
            tv.setText(oldText);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AbstractAdapter.ViewHolderClickListener<ViewHolder> listener;
        private TextView mName;

        public Bank getBank() {
            return bank;
        }

        private Bank bank;

        public ViewHolder(View itemView, AbstractAdapter.ViewHolderClickListener<ViewHolder> listener) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(Bank bank) {
            mName.setText(bank.getName());
            this.bank = bank;
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                listener.onItemClick(this, getLayoutPosition());
            }
        }
    }

    private class BankListAdapter extends AbstractAdapter<Bank, ViewHolder> {
        public BankListAdapter(Context context, ViewHolderClickListener<ViewHolder> listener) {
            super(context, listener);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.bank_list_item, parent, false);
            return new ViewHolder(view, mViewHolderClickListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(getItem(position));
        }
    }

    private class BanListCallback implements Callback<BankListResponse> {

        @Override
        public void start() {

        }

        @Override
        public void success(BankListResponse res, Response response) {
            if (res.isSuccess()) {
                initedBankList = true;
                adapter.reload(res.getData(), false);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(BindBankActivity.this, "获取银行列表失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(BindBankActivity.this, hNetError);
        }

        @Override
        public void end() {
            if (!initedBankList) {
                hideBankList();
            }
        }
    }
}
