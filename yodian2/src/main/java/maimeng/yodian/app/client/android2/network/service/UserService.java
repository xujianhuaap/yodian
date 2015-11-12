package maimeng.yodian.app.client.android2.network.service;

import maimeng.yodian.app.client.android2.network.Api;
import maimeng.yodian.app.client.android2.network.response.UserInfoResponse;
import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by android on 2015/11/12.
 */
public interface UserService {
    @POST(Api.GET_USER_INFO)
    @FormUrlEncoded
    Call<UserInfoResponse> getInfo(@Field("uid")long uid);
}
