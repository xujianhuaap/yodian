package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.henjue.library.share.Type;
import org.henjue.library.share.manager.IShareManager;
import org.henjue.library.share.manager.QQShareManager;
import org.henjue.library.share.manager.ShareFactory;
import org.henjue.library.share.manager.WechatShareManager;
import org.henjue.library.share.manager.WeiboShareManager;
import org.henjue.library.share.model.MessageWebpage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.utils.LogUtil;


/**
 * Created by android on 2015/5/26.
 */
public class ShareDialog extends DialogFragment implements Target {

    @Bind(R.id.report)
    TextView mReport;
    @Bind(R.id.content)
    GridLayout mContent;
    private long id;
    private long targetUid;
    private String reportContent;
    private String skillName;
    private String price;
    private String targetNickname;
    private String title;
    private String unit;
    private String redirect_url;
    private String img_url;
    private boolean end=false;



    private YApplication app;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    public static class ShareParams {
        public final long id;
        public final long targetUid;
        public final String targetNickname;
        public final String price;
        public final String unit;
        public final String reportContent;
        public final String skillName;
        public final String redirect_url;
        public final String img_url;

        /**
         * @param id            元素id
         * @param targetUid     目标用户id
         * @param reportContent 举报内容说明
         */
        public ShareParams(long id,String redirect_url,String img_url,String skillName, long targetUid,String targetNickname,String price,String unit, String reportContent) {
            this.id = id;
            this.img_url=img_url;
            this.redirect_url=redirect_url;
            this.skillName=skillName;
            this.price = price;
            this.unit=unit;
            this.targetNickname=targetNickname;
            this.targetUid = targetUid;
            this.reportContent = reportContent;
        }
    }



    public static void show(Activity context, ShareParams params) {
        newInstance(params).show(context.getFragmentManager(), "sharedialog");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tempFile=new File(getActivity().getCacheDir(),System.currentTimeMillis()+".png");
        if(!tempFile.exists()){
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        getDialog().getWindow().getAttributes().windowAnimations = R.style.ShareDialogAnimation;
        View inflate = inflater.inflate(R.layout.pop_share_view, container, false);
        ButterKnife.bind(this, inflate);
        return inflate;
    }
    /**
     * @param params Options
     */
    public static ShareDialog newInstance(ShareParams params) {
        ShareDialog dialog = new ShareDialog();
        StringBuffer title=new StringBuffer();
        title.append("【");
        title.append(params.targetNickname);
        title.append("】");
        title.append("正在出售Ta的时间技能:");
        Bundle args = new Bundle();
        args.putLong("id", params.id);
        args.putLong("targetUid", params.targetUid);
        args.putString("title", title.toString());
        args.putString("price", params.price);
        args.putString("unit", params.unit);
        args.putString("redirect_url", params.redirect_url);
        args.putString("img_url", params.img_url);
        args.putString("reportContent", params.reportContent);
        args.putString("skillName", params.skillName);
        args.putString("targetNickname", params.targetNickname);
        dialog.setArguments(args);
        return dialog;
    }
    private File tempFile;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        id = args.getLong("id");
        targetUid = args.getLong("targetUid");
        title = args.getString("title");
        price = args.getString("price");
        unit = args.getString("unit");
        redirect_url = args.getString("redirect_url");
        img_url = args.getString("img_url");
        reportContent = args.getString("reportContent");
        skillName = args.getString("skillName");
        targetNickname = args.getString("targetNickname");
        if(img_url!=null && (img_url.startsWith("http") || img_url.startsWith("ftp"))){
            Toast.makeText(getActivity(),"正在分享路上...",Toast.LENGTH_SHORT).show();
            Network.image(getActivity(), img_url, this);
        }else{
            end=true;
        }

        if (UserAuth.read(getActivity()).uid!= targetUid) {
            mReport.setVisibility(View.VISIBLE);
        }else{
            ((View) mReport.getParent()).setVisibility(View.GONE);
            mContent.setRowCount(1);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0, R.style.ShareDialogStyle);
        app = (YApplication) getActivity().getApplication();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(tempFile!=null)tempFile.deleteOnExit();
    }
    @Override
    public void onResume() {
        super.onResume();
//        Window window = getDialog().getWindow();
//        window.setGravity(Gravity.BOTTOM);
    }

    @OnClick(R.id.qqRoom)
    public void qzone(final View v) {
        StringBuffer content=new StringBuffer();
        content.append(skillName).append(price).append(unit);
        IShareManager iShareManager= ShareFactory.create(getActivity(), Type.Platform.QQ);
        iShareManager.share(new MessageWebpage(title, content.toString(), redirect_url, img_url), QQShareManager.QZONE_SHARE_TYPE);
        dismiss();
    }

    @OnClick(R.id.report)
    public void report(View v) {
    }

    @OnClick(R.id.sina)
    public void ShareToWeiBo(View view) {
        StringBuffer content=new StringBuffer();
        content.append(title).append(skillName).append(price).append(unit).append("@优点APP");
        IShareManager iShareManager= ShareFactory.create(getActivity(), Type.Platform.WEIBO);
        iShareManager.share(new MessageWebpage("", content.toString(), redirect_url, img_url), WeiboShareManager.WEIBO_SHARE_TYPE);
        dismiss();
    }

    @OnClick({R.id.fridens, R.id.weixin})
    public void ShareToWeiXin(View v) {
        if(!end){
            Toast.makeText(getActivity(),"还未准备完成，请稍后...",Toast.LENGTH_SHORT).show();
            return;
        }
            StringBuffer content=new StringBuffer();
            content.append(skillName).append(price).append(unit);
            IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.WEIXIN);
            iShareManager.share(new MessageWebpage(title,content.toString(),redirect_url,img_url),v.getId() == R.id.weixin?WechatShareManager.WEIXIN_SHARE_TYPE_TALK:WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);
            dismiss();
    }



    private void showMessage(Context context, String message) {
        if (context != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(String message) {
        Activity context = getActivity();
        showMessage(context, message);
    }
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(tempFile));
            this.img_url=tempFile.toString();
            end=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        LogUtil.e(ShareDialog.class.getSimpleName(), "onBitmapFailed");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        LogUtil.e(ShareDialog.class.getSimpleName(), "onPrepareLoad");
    }
}
