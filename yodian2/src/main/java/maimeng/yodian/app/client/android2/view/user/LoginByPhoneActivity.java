package maimeng.yodian.app.client.android2.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android2.R;
import maimeng.yodian.app.client.android2.model.Auth;
import maimeng.yodian.app.client.android2.network.Network;
import maimeng.yodian.app.client.android2.network.response.AuthResponse;
import maimeng.yodian.app.client.android2.network.service.AuthService;
import maimeng.yodian.app.client.android2.view.AbstractActivity;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginByPhoneActivity extends AbstractActivity{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mCodeView;
    private AuthService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Network.getService(AuthService.class);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone);

        mCodeView = (EditText) findViewById(R.id.code);
        mCodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mBtnSign = (Button) findViewById(R.id.btn_sign);
        Button mBtnGetCode = (Button) findViewById(R.id.btn_get_code);
        mBtnGetCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneView.setError(null);
                // Store values at the time of the login attempt.
                String phone = mPhoneView.getText().toString();
                // Check for a valid phone address.
                if (TextUtils.isEmpty(phone)) {
                    mPhoneView.setError(getString(R.string.error_field_required));
                    mPhoneView.requestFocus();
                } else if (!isPhoneValid(phone)) {
                    mPhoneView.setError(getString(R.string.error_invalid_phone));
                    mPhoneView.requestFocus();
                }
                service.getCode(phone).enqueue(new Callback<maimeng.yodian.app.client.android2.network.response.Response>() {
                    @Override
                    public void onResponse(Response<maimeng.yodian.app.client.android2.network.response.Response> response, Retrofit retrofit) {
                        response.body().showMessage(LoginByPhoneActivity.this);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        });
        mBtnSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mPhoneView.setError(null);
        mCodeView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneView.getText().toString();
        String code = mCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid code, if the user entered one.
        if (!TextUtils.isEmpty(code) && !isPasswordValid(code)) {
            mCodeView.setError(getString(R.string.error_invalid_code));
            focusView = mCodeView;
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            service.login(phone, code, "").enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Response<AuthResponse> response, Retrofit retrofit) {
                    showProgress(false);
                    if(response.isSuccess()&& response.body().isSuccess()){
                        Auth auth = saveOrUpdate(response.body().getData());
                        Toast.makeText(LoginByPhoneActivity.this,response.body().getData().getNickname(),Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, new Intent().putExtra("auth", Parcels.wrap(auth)));
                        finish();
                    }else {
                        Toast.makeText(LoginByPhoneActivity.this,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    showProgress(false);
                }
            });
        }
    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length()>=11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() == 4;
    }
}

