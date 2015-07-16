package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Get;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.SkillTemplateResponse;

@FormUrlEncoded
public interface SkillService {
    /**
     * 某人的技能列表
     * @param uid
     * @param p
     * @param callback
     */
    @Post(ApiConfig.Api.SKILL_LIST)
    void list(@Param("uid")long uid,@Param("p")int p,Callback<SkillResponse> callback);

    /**
     * 进选技能列表
     * @param p
     * @param callback
     */
    @Post(ApiConfig.Api.SKILL_CHOICE)
    void choose(@Param("p")int p,Callback<SkillResponse> callback);

    @Get(ApiConfig.Api.SKILL_TEMPLATE)
    void template(Callback<SkillTemplateResponse> callback);

}
