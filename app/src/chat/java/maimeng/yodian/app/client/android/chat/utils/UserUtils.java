package maimeng.yodian.app.client.android.chat.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.domain.User;
import maimeng.yodian.app.client.android.network.loader.ImageLoader;

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
        if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
            ImageLoader.image(imageView, Uri.parse(user.getAvatar()));
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).transform(ImageLoader.createTransformation()).into(imageView);
        }
    }

}