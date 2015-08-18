package maimeng.yodian.app.client.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;

/**
 * Created by android on 8/18/15.
 */
public class WebLauncherUtils {
    public static boolean handler(Activity mContext,Uri uri){
        if(uri==null)return false;
        if(uri.getScheme().equalsIgnoreCase("intent")){
            String _type=uri.getQueryParameter("ydtype");
            String _id=uri.getQueryParameter("ydid");
            if(!TextUtils.isEmpty(_type) && !TextUtils.isEmpty(_id)){
                int type=Integer.parseInt(_type);
                long id=Long.parseLong(_id);
                switch (type){
                    case 0:
                        mContext.startActivity(new Intent(mContext,UserHomeActivity.class).putExtra("uid",id));
                        return true;
                    case 1:
                        mContext.startActivity(new Intent(mContext,SkillDetailsActivity.class).putExtra("sid", id));
                        return true;
                }
            }

        }
        return false;
    }
    public static boolean handler(Activity mContext,String url){
        if(url==null)return false;
        Uri uri = Uri.parse(url);
        return handler(mContext,uri);
    }
}
