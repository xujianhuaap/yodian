package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Get;
import org.henjue.library.hnet.anntoation.Multipart;
import org.henjue.library.hnet.anntoation.NoneEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.response.AddressRespoonse;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.UserInfoResponse;

/**
 * Created by android on 15-7-14.
 */
@FormUrlEncoded
public interface UserService {

    /**
     * 上传头像和昵称
     *
     * @param nickname
     * @param callback
     */
    @Multipart
    @Post(Api.USER_INFO_UPDATE)
    void modifyInfo(@Param("nickname") String nickname, @Param("avatar") TypedBitmap avatar,
                    Callback<ModifyUserResponse> callback);

    /**
     * 上传头像和昵称
     *
     * @param nickname
     * @param callback
     */
    @Multipart
    @Post(Api.USER_INFO_UPDATE)
    void modifyInfo(@Param("nickname") String nickname, @Param("sex") int sex, @Param("job") String job, @Param("signature") String signature, @Param("weichat") String wechat,
                    @Param("avatar") TypedBitmap avatar, @Param("qq") String qq,
                    @Param("contact") String contact,
                    @Param("city") String city,
                    @Param("province") String province,
                    @Param("district") String district,
                    Callback<ModifyUserResponse> callback);


    /**
     * 昵称
     *
     * @param nickname
     * @param callback
     */
    @Multipart
    @Post(Api.USER_INFO_UPDATE)
    void modifyInfo(@Param("nickname") String nickname, @Param("sex") int sex, @Param("job") String job, @Param("signature") String signature, @Param("weichat") String wechat,
                    @Param("qq") String qq,
                    @Param("contact") String contact,
                    @Param("city") String city,
                    @Param("province") String province,
                    @Param("district") String district,
                    Callback<ModifyUserResponse> callback);

    /***
     *
     * @param callback
     */
    @Get(Api.USER_INFO)
    void info(Callback<UserInfoResponse> callback);

    /***
     *
     * @param callback
     */
    @Post(Api.GET_USER_INFO)
    void getInfo(@Param("uid")long uid,Callback<UserInfoResponse> callback);

    /***
     * 设置收货地址
     * @param province
     * @param city
     * @param district
     * @param address
     * @param name
     * @param mobile
     * @param callback
     */
    @Post(Api.SETTING_ADDRESS)
    void setAddress(@Param("province")String province,@Param("city")String city,
                    @Param("district")String district,@Param("address")String address,
                    @Param("name")String name,@Param("mobile")String mobile,
                    Callback<ToastResponse> callback);

    /****
     *
     * @param callback
     */
    @Post(Api.GET_ADDRESS)
    @NoneEncoded
    void getAddress(Callback<AddressRespoonse>callback);

}
