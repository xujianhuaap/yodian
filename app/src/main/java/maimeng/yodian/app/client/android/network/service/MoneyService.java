package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.NoneEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.VouchResponse;
import maimeng.yodian.app.client.android.network.response.WDListHistoryResponse;

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
    @Post(Api.MONEY_MY)
    @NoneEncoded
    void remanider(Callback<RemainderResponse> response);


    /**
     * 提现申请
     *
     * @param response
     */
    @Post(Api.MONEY_WITHDRAW)
    void withdraw(@Param("money") String money, ToastCallback response);

    /**
     * 提现历史
     *
     * @param page
     * @param response
     */
    @Post(Api.MONEY_WDLIST)
    void wdlist(@Param("p") int page, Callback<WDListHistoryResponse> response);

    /***
     * 增加支付宝账号以备提现
     * @param account
     */
    @Post(Api.MONEY_ADD_ACCOUNT)
    void addAccount(@Param("alipay")String account,Callback<ToastResponse> callback);

}
