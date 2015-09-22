package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.NoneEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;

/**
 * Created by android on 2015/9/21.
 */
@FormUrlEncoded
public interface MoneyService {
    /**
     * 我的余额
     *
     * @param response
     */
    @Post(ApiConfig.Api.MONEY_MY)
    @NoneEncoded
    void remanider(Callback<RemainderResponse> response);


    /**
     * 提现申请
     *
     * @param response
     */
    @Post(ApiConfig.Api.MONEY_WITHDRAW)
    void withdraw(@Param("money") double money, ToastCallback response);

}
