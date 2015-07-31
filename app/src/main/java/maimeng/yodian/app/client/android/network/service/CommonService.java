package maimeng.yodian.app.client.android.network.service;

import org.henjue.library.hnet.anntoation.FormUrlEncoded;
import org.henjue.library.hnet.anntoation.Param;
import org.henjue.library.hnet.anntoation.Post;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.common.ToastCallback;

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
    @Post(ApiConfig.Api.REPORT)
    void report(@Param("type") int type, @Param("sid") long sid, @Param("scid") long scid, @Param("ruid") long ruid, ToastCallback callback);
}
