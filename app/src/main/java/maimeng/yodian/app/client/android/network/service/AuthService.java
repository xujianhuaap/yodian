package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.RequestFacade;
import org.henjue.library.hnet.RequestFilter;
import org.henjue.library.hnet.anntoation.Filter;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.AuthResponse;

/**
 * Created by android on 15-7-13.
 */
@FormUrlEncoded
public interface AuthService {
    /**
     * 手机号码登录
     * @param mobile
     * @param code
     * @param pushtoken
     * @param callback
     */
    @Post(ApiConfig.Api.AUTH_LOGIN)
    @Filter(LoginFilter.class)
    void login(@Param("mobile")String mobile,@Param("code")String code,@Param("etoken")String pushtoken,Callback<AuthResponse> callback);

    /**
     * 获取短信验证码
     * @param mobile
     * @param callback
     */
    @Post(ApiConfig.Api.AUTH_GETCODE)
    void getCode(@Param("mobile")String mobile,ToastCallback callback);
    class LoginFilter implements RequestFilter {
        @Override
        public void onComplite(RequestFacade requestFacade) {
            requestFacade.add("type",0);
            requestFacade.add("etype",2);
        }

        @Override
        public void onStart(RequestFacade requestFacade) {

        }

        @Override
        public void onAdd(String s, Object o) {

        }
    }
}
