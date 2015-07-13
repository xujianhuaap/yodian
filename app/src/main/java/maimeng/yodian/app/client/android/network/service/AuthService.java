package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.RequestFacade;
import org.henjue.library.hnet.RequestFilter;
import org.henjue.library.hnet.anntoation.Filter;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.response.AuthResponse;

/**
 * Created by android on 15-7-13.
 */
@FormUrlEncoded
public interface AuthService {
    @Post(ApiConfig.Api.USER_LOGIN)
    @Filter(LoginFilter.class)
    void login(@Param("mobile")String mobile,@Param("code")String code,@Param("etoken")String pushtoken,Callback<AuthResponse> callback);

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
