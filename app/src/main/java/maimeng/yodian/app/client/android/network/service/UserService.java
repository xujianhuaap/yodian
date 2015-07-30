package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Multipart;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.TypeBitmap;
import maimeng.yodian.app.client.android.network.response.ModifyUserResponse;

/**
 * Created by android on 15-7-14.
 */
@FormUrlEncoded
public interface UserService{

    /**
     * 上传头像和昵称
     * @param nickname
     * @param callback
     */
    @Multipart
    @Post(ApiConfig.Api.USER_INFO_UPDATE)
    void modifyInfo(@Param("nickname") String nickname, @Param("avatar") TypeBitmap avatar, Callback<ModifyUserResponse> callback);

    /**
     * 上传头像和昵称
     * @param nickname
     * @param callback
     */
    @Multipart
    @Post(ApiConfig.Api.USER_INFO_UPDATE)
    void modifyInfo(@Param("nickname") String nickname, @Param("weichat") String wechat, @Param("avatar") TypeBitmap avatar, Callback<ModifyUserResponse> callback);

}
