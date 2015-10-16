package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Get;
import org.henjue.library.hnet.anntoation.Multipart;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.response.SkillAllResponse;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.SkillTemplateResponse;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;

@FormUrlEncoded
public interface SkillService {
    /**
     * 某人的技能列表
     *
     * @param uid
     * @param p
     * @param callback
     */
    @Post(Api.SKILL_LIST)
    void list(@Param("uid") long uid, @Param("p") int p, Callback<SkillUserResponse> callback);

    /**
     * 进选技能列表
     *
     * @param p
     * @param callback
     */
    @Post(Api.SKILL_CHOICE)
    void choose(@Param("p") int p, @Param("scid") long scid, Callback<SkillResponse> callback);

    /**
     * 获取技能模板
     *
     * @param callback
     */
    @Get(Api.SKILL_TEMPLATE)
    void template(Callback<SkillTemplateResponse> callback);

    /**
     * 添加技能
     *
     * @param name
     * @param callback
     */
    @Multipart
    @Post(Api.SKILL_ADD)
    void add(@Param("name") String name, @Param("content") String content,
             @Param("pic") TypedBitmap pic, @Param("price") String price,
             @Param("unit") String unit, @Param("allow_sell")int allowSell,Callback<SkillAllResponse> callback);

    /**
     * 修改技能
     *
     * @param sid
     * @param name
     * @param content
     * @param pic
     * @param price
     * @param unit
     * @param callback
     */
    @Multipart
    @Post(Api.SKILL_UPDATE)
    void update(@Param("sid") long sid, @Param("name") String name,
                @Param("content") String content, @Param("pic") TypedBitmap pic,
                @Param("price") String price, @Param("unit") String unit,
                @Param("allow_sell")int allowSell,ToastCallback callback);


    /**
     * 删除技能
     *
     * @param sid
     * @param callback
     */
    @Post(Api.SKILL_DELETE)
    void delete(@Param("sid") long sid, ToastCallback callback);

    /**
     * * 上架下架技能
     *
     * @param sid
     * @param up       1上架，0下架
     * @param callback
     */
    @Post(Api.SKILL_UP)
    void up(@Param("sid") long sid, @Param("up") int up, ToastCallback callback);

    /**
     * 技能日记列表
     *
     * @param sid
     * @param callback
     */
    @Post(Api.RMARK_LIST)
    void rmark_list(@Param("sid") long sid, @Param("p") int page, Callback<RmarkListResponse> callback);

    /**
     * 删除日记
     *
     * @param id
     * @param callback
     */
    @Post(Api.RMARK_DELETE)
    void delete_rmark(@Param("scid") long id, ToastCallback callback);

    /***
     * 添加日记
     *
     * @param sid
     * @param content
     * @param pic
     * @param callback
     */
    @Multipart
    @Post(Api.RMARK_ADD)
    void add_rmark(@Param("sid") long sid, @Param("content") String content,
                   @Param("pic") TypedBitmap pic,
                   ToastCallback callback);


    @Post(Api.REPORT)
    void report(@Param("type") int type, @Param("scid") long scid,
                @Param("sid") long sid, @Param("rid") long rid,
                ToastCallback callback);

}
