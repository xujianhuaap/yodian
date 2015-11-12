package maimeng.yodian.app.client.android2.network.service;

import maimeng.yodian.app.client.android2.network.Api;
import maimeng.yodian.app.client.android2.network.response.AuthResponse;
import maimeng.yodian.app.client.android2.network.response.Response;
import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by android on 15-7-13.
 */
public interface AuthService {
    /**
     * 手机号码登录
     *
     * @param mobile
     * @param code
     * @param pushtoken
     */
    @FormUrlEncoded
    @POST(Api.AUTH_LOGIN)
    Call<AuthResponse> login(@Field("mobile") String mobile, @Field("code") String code, @Field("etoken") String pushtoken);

    /**
     * 获取短信验证码
     *
     * @param mobile
     */
    @FormUrlEncoded
    @POST(Api.AUTH_GETCODE)
    Call<Response> getCode(@Field("mobile") String mobile);
}
