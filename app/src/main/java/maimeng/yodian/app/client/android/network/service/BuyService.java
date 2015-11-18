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
     *
     * @param sid
     * @param paytype
     * @param flag 1抵扣余额，0不抵扣
     * @param callback
     */
    @Post(Api.SKILL_BUYSKILL)
    void buySkill(@Param("sid") long sid, @Param("paytype") int paytype,@Param("flag") int flag, Callback<String> callback);

    /***
     * 未支付订单支付
     *
     * @param oid
     * @param payType
     * @param callback
     */
    @Post(Api.ORDER_BUYORDER)
    void buyOrder(@Param("oid") long oid, @Param("paytype") int payType,@Param("flag") int flag, Callback<String> callback);

    @Post(Api.ORDER_REMAINDER_BUY)
    void remainderPay(@Param("oid") long oid, Callback<ToastResponse> callback);
}
