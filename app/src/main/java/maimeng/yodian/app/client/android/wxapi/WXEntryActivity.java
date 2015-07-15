package maimeng.yodian.app.client.android.wxapi;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;

import org.henjue.library.share.wechat.WechatHandlerActivity;

/**
 * Created by android on 15-7-13.
 */
public class WXEntryActivity extends WechatHandlerActivity {
    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
        switch (resp.errCode){
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                break;
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        super.onReq(baseReq);
    }
}
