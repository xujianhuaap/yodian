package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Get;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.response.Response;
import maimeng.yodian.app.client.android.network.response.StringResponse;

/**
 * Created by android on 2015/11/6.
 */
@FormUrlEncoded
public interface ChatService {
    @Get(Api.SENDSERVICE)
    void sendService(Callback<Response> callback);
}
