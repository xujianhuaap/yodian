package maimeng.yodian.app.client.android.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 15-7-15.
 */
public class UmengPushMessageService extends UmengBaseIntentService {
    private static final String TAG = UmengPushMessageService.class.getName();
    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        if (FeedbackPush.getInstance(context).onFBMessage(intent)) {
            return;
        }
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            LogUtil.i(TAG,message);
            UTrack.getInstance(context).trackMsgClick(msg);
            // code  to handle message here
            // ...
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
