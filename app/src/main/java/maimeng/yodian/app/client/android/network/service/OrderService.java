package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.response.OrderRepsonse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;

/**
 * Created by xujianhua on 9/28/15.
 */

@FormUrlEncoded
public interface OrderService {
    /***
     * 购买者列表
     * @param p
     * @param callback
     */
    @Post(Api.ORDER_LIST_BUYER)
    void buyers(@Param("P")int p,Callback<OrderRepsonse>callback);

    /***
     * 出售者列表
     * @param p
     * @param callback
     */
    @Post(Api.ORDER_LIST_SELLER)
    void seller(@Param("p")int p,Callback<OrderRepsonse>callback);

    /***
     * 卖家接单
     * @param oid
     * @param callback
     */
    @Post(Api.ORDER_ACCEPT)
    void acceptOrder(@Param("oid")String oid,Callback<ToastResponse>callback);

    /***
     * 卖家发货
     * @param oid
     * @param callback
     */
    @Post(Api.ORDER_SEND)
    void sendGoods(@Param("oid")String oid,Callback<ToastResponse>callback);

    /****
     * 买家确认交易成功
     * @param oid
     * @param callback
     */
    @Post(Api.ORDER_CONFIRM)
    void confirmOrder(@Param("oid")String oid,Callback<ToastResponse>callback);





}
