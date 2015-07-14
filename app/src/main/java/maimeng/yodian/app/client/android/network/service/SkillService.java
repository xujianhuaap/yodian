package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.response.SkillResponse;

@FormUrlEncoded
public interface SkillService {
    @Post(ApiConfig.Api.SKILL_LIST)
    void list(@Param("uid")long uid,@Param("p")int p,Callback<SkillResponse> callback);
}
