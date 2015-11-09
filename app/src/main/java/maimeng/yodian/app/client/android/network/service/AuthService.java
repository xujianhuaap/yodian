package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.RequestFacade;
import org.henjue.library.hnet.RequestFilter;
import org.henjue.library.hnet.anntoation.Filter;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.NoneEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.response.CertifyInfoResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.view.deal.DrawMoneyInfoConfirmActivity;

/**
 * Created by android on 15-7-13.
 */
@FormUrlEncoded
public interface AuthService {
    /**
     * 手机号码登录
     *
     * @param mobile
     * @param code
     * @param pushtoken
     * @param callback
     */
    @Post(Api.AUTH_LOGIN)
    @Filter(LoginPhoneFilter.class)
    void login(@Param("mobile") String mobile, @Param("code") String code, @Param("etoken") String pushtoken, Callback<AuthResponse> callback);

    /**
     * 第三方登录
     *
     * @param type      1新浪微博,2微信
     * @param token     第三方token
     * @param usid      第三方用户id
     * @param pushtoken 推送token
     * @param callback
     */
    @Post(Api.AUTH_LOGIN)
    @Filter(LoginThirdPartyFilter.class)
    void thirdParty(@Param("type") int type, @Param("token") String token, @Param("usid") String usid, @Param("etoken") String pushtoken, Callback<AuthResponse> callback);

    /**
     * 获取短信验证码
     *
     * @param mobile
     * @param callback
     */
    @Post(Api.AUTH_GETCODE)
    void getCode(@Param("mobile") String mobile, Callback<ToastResponse> callback);


    class LoginThirdPartyFilter implements RequestFilter {
        @Override
        public void onComplite(RequestFacade requestFacade) {
            requestFacade.add("etype", 2);
        }

        @Override
        public void onStart(RequestFacade requestFacade) {

        }

        @Override
        public void onAdd(String s, Object o) {

        }
    }

    class LoginPhoneFilter implements RequestFilter {
        @Override
        public void onComplite(RequestFacade requestFacade) {
            requestFacade.add("type", 0);
            requestFacade.add("etype", 2);
        }

        @Override
        public void onStart(RequestFacade requestFacade) {

        }

        @Override
        public void onAdd(String s, Object o) {

        }
    }


    /***
     * 获得个人联系方式验证码
     * @param moblie
     * @param type
     *
     */
    @Post(Api.INFO_CODE)
    void getMobileCode(@Param("mobile")String moblie,@Param("type")int type,Callback<ToastResponse>callBack);

    /***
     * 验证个人信息
     * @param name
     * @param idcard
     * @param moblie
     * @param code
     * @param callback
     */
    @Post(Api.INFO_CERTIFY)
    void certifyUserInfo(@Param("name")String name,@Param("idcard")String idcard,
                         @Param("mobile")String moblie,@Param("code")String code,
                         Callback<ToastResponse> callback);


    /***
     * 获得个人认证信息
     * @param callback
     */
    @Post(Api.INFO_GET)
    @NoneEncoded
    void getCertifyInfo(Callback<CertifyInfoResponse>callback);
}
