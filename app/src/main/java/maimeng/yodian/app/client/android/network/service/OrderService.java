package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.response.ToastResponse;

/**
 * Created by xujianhua on 9/28/15.
 */

@FormUrlEncoded
public interface OrderService {
    @Post(ApiConfig.Api.ORDER_LIST_BUYER)
    void buyers(@Param("P")int p,Callback<ToastResponse>callback);
}
