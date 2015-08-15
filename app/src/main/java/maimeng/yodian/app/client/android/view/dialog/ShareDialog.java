package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.glxn.qrgen.android.QRCode;

import org.henjue.library.share.Type;
import org.henjue.library.share.manager.IShareManager;
import org.henjue.library.share.manager.ShareFactory;
import org.henjue.library.share.manager.WechatShareManager;
import org.henjue.library.share.manager.WeiboShareManager;
import org.henjue.library.share.model.MessagePic;
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
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.utils.LogUtil;


/**
 * Created by android on 2015/5/26.
 */
public class ShareDialog extends DialogFragment implements Target/*, ShareListener*/ {

    @Bind(R.id.report)
    TextView mReport;
    @Bind(R.id.content)
    GridLayout mContent;
    private long targetUid;
    private String reportContent;
    private String targetNickname;
    private String title;
    private String redirect_url;
    private boolean end=false;
    private boolean releaseOk;



    private YApplication app;
    private Skill skill;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

//    @Override
//    public void onSuccess() {
//        dismiss();
//        Toast.makeText(getActivity(),R.string.share_success,Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onFaild() {
//        Toast.makeText(getActivity(),R.string.share_failed,Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onCancel() {
//        Toast.makeText(getActivity(),R.string.share_cancel,Toast.LENGTH_SHORT).show();
//    }

    public static class ShareParams {
        public final long targetUid;
        public final String targetNickname;
        public final String reportContent;
        public final String redirect_url;
        private final Skill skill;

        /**
         *  @param redirect_url
         * @param targetUid
         * @param targetNickname
         * @param reportContent
         * @param skill
         */
        public ShareParams(Skill skill,String redirect_url, long targetUid, String targetNickname, String reportContent) {
            this.redirect_url=redirect_url;
            this.targetNickname=targetNickname;
            this.targetUid = targetUid;
            this.skill=skill;
            this.reportContent = reportContent;
        }
    }


    // modify by xu 08-12

    public static ShareDialog show(Activity context, ShareParams params) {
        ShareDialog shareDialog=newInstance(params);
        shareDialog.show(context.getFragmentManager(), "sharedialog");
        return shareDialog;
    }

    public static ShareDialog show(Activity context, ShareParams params,boolean releaseOk) {
        ShareDialog shareDialog=newInstance(params);
        shareDialog.show(context.getFragmentManager(), "sharedialog");
        shareDialog.releaseOk=releaseOk;
        return shareDialog;
    }
    //end

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        File yodianDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "yodian");
        if(!yodianDir.exists()){
            yodianDir.mkdirs();
        }
        tempFile=new File(yodianDir,System.currentTimeMillis()+".png");
        if(!tempFile.exists()){
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        getDialog().getWindow().getAttributes().windowAnimations = R.style.ShareDialogAnimation;
        View inflate = inflater.inflate(R.layout.pop_share_view, container, false);
        if(releaseOk){
            inflate.findViewById(R.id.release_ok).setVisibility(View.VISIBLE);
        }

        ButterKnife.bind(this, inflate);
        return inflate;
    }
    /**
     * @param params Options
     */
    public static ShareDialog newInstance(ShareParams params) {
        ShareDialog dialog = new ShareDialog();
        Bundle args = new Bundle();
        Skill skill = params.skill;
        StringBuffer title=new StringBuffer();
        title.append("【");
        title.append(params.targetNickname);
        title.append("】");
        title.append("正在出售Ta的时间技能:");
        title.append(skill.getName());


        args.putLong("targetUid", params.targetUid);
        args.putString("title", title.toString());
        args.putString("redirect_url", params.redirect_url);
        args.putString("reportContent", params.reportContent);
        args.putString("targetNickname", params.targetNickname);
        args.putParcelable("skill", skill);
        dialog.setArguments(args);
        return dialog;
    }
    private File tempFile;
    private Bitmap QRCodeBitmap;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        skill=args.getParcelable("skill");
        targetUid = args.getLong("targetUid");
        title = args.getString("title");
        redirect_url = args.getString("redirect_url");
        reportContent = args.getString("reportContent");
        targetNickname = args.getString("targetNickname");
        if(skill.getPic()!=null && (skill.getPic().startsWith("http") || skill.getPic().startsWith("ftp"))){
            Toast.makeText(getActivity(),"正在分享路上...",Toast.LENGTH_SHORT).show();
            Network.image(getActivity(), skill.getPic(), this);
        }else{
            end=true;
        }
        if (User.read(getActivity()).getUid()!= targetUid) {
            mReport.setVisibility(View.VISIBLE);
        }else{
            ((View) mReport.getParent()).setVisibility(View.GONE);
            mContent.setRowCount(1);
        }
        int size=getResources().getDimensionPixelSize(R.dimen.qrcode_size);
        int left=getResources().getDimensionPixelSize(R.dimen.qrcode_left);
        int top=getResources().getDimensionPixelSize(R.dimen.qrcode_top);
        QRCode from = QRCode.from(skill.getQrcodeUrl());
        from.withSize(size,size);
        Bitmap qrcode = from.bitmap();
        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.fingerprint_bg);


        Bitmap bg = temp.copy(temp.getConfig(), true);
        temp.recycle();
        QRCodeBitmap = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.RGB_565);
        Canvas can=new Canvas(QRCodeBitmap);
        can.drawBitmap(bg,0,0,new Paint());
        can.drawBitmap(qrcode, left, top, new Paint());
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
        IShareManager iShareManager= ShareFactory.create(getActivity(), Type.Platform.QQ);
        String imgPath=skill.getPic();
        if(imgPath!=null&&imgPath.startsWith("file://")){
            imgPath=Uri.parse(skill.getPic()).getPath();
        }
        iShareManager.share(new MessageWebpage(title, skill.getContent(), redirect_url, imgPath),0/*,this*/);
    }

    @OnClick(R.id.report)
    public void report(View v) {
    }

    @OnClick(R.id.sina)
    public void ShareToWeiBo(View view) {
        StringBuffer content=new StringBuffer();
        content.append(title).append(skill.getPrice()).append(skill.getUnit()).append("@优点APP");
        IShareManager iShareManager= ShareFactory.create(getActivity(), Type.Platform.WEIBO);
        iShareManager.share(new MessageWebpage("", content.toString(), redirect_url, QRCodeBitmap), WeiboShareManager.WEIBO_SHARE_TYPE/*,this*/);
    }

    @OnClick({R.id.fridens, R.id.weixin})
    public void ShareToWeiXin(View v) {
        if(!end){
            Toast.makeText(getActivity(),"还未准备完成，请稍后...",Toast.LENGTH_SHORT).show();
            return;
        }

        IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.WEIXIN);
        if(v.getId() == R.id.weixin){
            iShareManager.share(new MessageWebpage(title,skill.getContent(),redirect_url, tempFile.toString()),WechatShareManager.WEIXIN_SHARE_TYPE_TALK/*,this*/);
        }else{
            iShareManager.share(new MessagePic(QRCodeBitmap),WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS/*,this*/);
        }
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
            skill.setPic(tempFile.toString());
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
