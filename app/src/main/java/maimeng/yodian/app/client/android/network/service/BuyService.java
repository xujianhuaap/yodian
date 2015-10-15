package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.response.ToastResponse;

/**
 * Created by xujianhua on 10/12/15.
 */
@FormUrlEncoded
public interface BuyService {
    /***
     * 购买技能
     * @param sid
     * @param paytype
     * @param callback
     */
    @Post(Api.SKILL_BUY)
    void buySkill(@Param("sid")long sid,@Param("paytype")int paytype,Callback<String>callback);

    /***
     * 未支付订单支付
     * @param oid
     * @param payType
     * @param callback
     */
    @Post(Api.ORDER_BUY)
    void buyOrder(@Param("oid")String oid,@Param("paytype")int payType,Callback<String>callback);

    @Post(Api.ORDER_REMAINDER_BUY)
    void remainderPay(@Param("oid")String oid,Callback<ToastResponse> callback);
}
