package maimeng.yodian.app.client.android.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.domain.User;
import maimeng.yodian.app.client.android.network.loader.Circle;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.utils.LogUtil;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */
    public static User getUserInfo(String username) {
        User user = DemoApplication.getInstance().getContactList().get(username);
        if (user == null) {
            user = new User(username);
        }
//
//        if(user != null){
//            //demo没有这些数据，临时填充
//            user.setNick(username);
////            user.setAvatar("http://downloads.easemob.com/downloads/57.png");
//        }
        return user;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        final String avatar;
        if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
            avatar = user.getAvatar();
        } else {
            avatar = "http://www.ketie.me/default.png";
        }
        if (BuildConfig.DEBUG)
            LogUtil.i(UserUtils.class.getName(), "setUserAvatar(),username:%s,avatar:%s", username, avatar);
        if (BuildConfig.DEBUG)
            LogUtil.i(UserUtils.class.getName(), "MainLooper:%s", Looper.myLooper() == Looper.getMainLooper());
        new ImageLoaderManager.Loader(imageView, Uri.parse(avatar)).placeHolder(R.drawable.default_avatar).circle(Circle.obtain()).callback(new ImageLoaderManager.Callback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                if (BuildConfig.DEBUG)
                    LogUtil.i(UserUtils.class.getName(), "onImageLoaded");
            }

            @Override
            public void onLoadEnd() {

            }

            @Override
            public void onLoadFaild() {

            }
        }).start(context);
    }

}
