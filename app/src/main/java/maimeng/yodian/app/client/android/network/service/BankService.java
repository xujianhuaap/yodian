package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.NoneEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.BankBindInfoResponse;
import maimeng.yodian.app.client.android.network.response.BankListResponse;

@FormUrlEncoded
public interface BankService {
    /**
     * 银行列表
     *
     * @param response
     */
    @Post(ApiConfig.Api.BANK_LIST)
    @NoneEncoded
    void list(Callback<BankListResponse> response);


    /**
     * 获取验证码
     *
     * @param response
     */
    @Post(ApiConfig.Api.BANK_GETCODE)
    void getcode(@Param("mobile") String mobile, ToastCallback response);

    /**
     * 绑定
     *
     * @param response
     */
    @Post(ApiConfig.Api.BANK_BIND)
    void bind(@Param("bank_id") long bank_id, @Param("card_no") String number,
              @Param("sub_branch_name") String branch, @Param("certif_no") String Id8,
              @Param("mobile") String mobile, @Param("real_name") String username,
              @Param("code") String code, ToastCallback response);

    /**
     * 绑定详情
     *
     * @param response
     */
    @Post(ApiConfig.Api.BANK_BIND_INFO)
    @NoneEncoded
    void bindInfo(Callback<BankBindInfoResponse> response);


    /**
     * 解除绑定
     *
     * @param response
     */
    @Post(ApiConfig.Api.BANK_UNBIND)
    @NoneEncoded
    void unbind(ToastCallback response);
}
