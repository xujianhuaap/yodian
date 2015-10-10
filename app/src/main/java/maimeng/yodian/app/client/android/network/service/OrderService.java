package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.response.OrderRepsonse;

/**
 * Created by xujianhua on 9/28/15.
 */

@FormUrlEncoded
public interface OrderService {
    @Post(Api.ORDER_LIST_BUYER)
    void buyers(@Param("P")int p,Callback<OrderRepsonse>callback);
    @Post(Api.ORDER_LIST_SELLER)
    void seller(@Param("p")int p,Callback<OrderRepsonse>callback);
}
