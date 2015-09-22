package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.NoneEncoded;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;

/**
 * Created by android on 2015/9/21.
 */
public interface MoneyService {
    @Post(ApiConfig.Api.MONEY_MY)
    @NoneEncoded
    void remanider(Callback<RemainderResponse> response);
}
