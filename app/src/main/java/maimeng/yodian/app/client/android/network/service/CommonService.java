package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.RequestFacade;
import org.henjue.library.hnet.RequestFilter;
import org.henjue.library.hnet.anntoation.Filter;
import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Get;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.Api;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.FloatResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.VersionResponse;

/**
 * Created by android on 2015/7/30.
 */
@FormUrlEncoded
public interface CommonService {
    /**
     * 举报
     *
     * @param type     1技能2日记3用户
     * @param sid      技能id，type=1时传
     * @param scid     日记id，type=2时
     * @param ruid     用户id，type=3
     * @param callback
     */
    @Post(Api.REPORT)
    void report(@Param("type") int type, @Param("sid") long sid, @Param("scid") long scid, @Param("ruid") long ruid, ToastCallback callback);

    @Get(Api.CHECK_VERSION)
    @Filter(CheckVersion.class)
    void checkVersion(Callback<VersionResponse> callback);

    class CheckVersion implements RequestFilter {

        @Override
        public void onComplite(RequestFacade requestFacade) {
            requestFacade.add("os", 2);
        }

        @Override
        public void onStart(RequestFacade requestFacade) {

        }

        @Override
        public void onAdd(String s, Object o) {

        }
    }

    @Post(Api.PUSH)
    void push(@Param("eclose") String eclose, Callback<ToastResponse> callback);

    @Get(Api.FLOAT)
    void getFloat(Callback<FloatResponse> callback);

}
