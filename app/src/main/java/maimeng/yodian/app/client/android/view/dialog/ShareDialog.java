package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.glxn.qrgen.android.QRCode;

import org.henjue.library.share.Type;
import org.henjue.library.share.manager.IShareManager;
import org.henjue.library.share.manager.QQShareManager;
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
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.loader.ImageLoader;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.widget.RoundImageView;


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
    private boolean end = false;
    private boolean releaseOk;


    private YApplication app;
    private Skill skill;
    private Rmark rmark;
    private Bitmap shareBitmap;
    private int type;
    private long scid;
    private long sid;
    private long rid;
    private View shareView;


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
         * @param redirect_url
         * @param targetUid
         * @param targetNickname
         * @param reportContent
         * @param skill
         */
        public ShareParams(Skill skill, String redirect_url, long targetUid, String targetNickname, String reportContent) {
            this.redirect_url = redirect_url;
            this.targetNickname = targetNickname;
            this.targetUid = targetUid;
            this.skill = skill;
            this.reportContent = reportContent;
        }
    }


    // modify by xu 08-12

    public static ShareDialog show(Activity context, ShareParams params, int type) {
        ShareDialog shareDialog = newInstance(params);
        shareDialog.show(context.getFragmentManager(), "sharedialog");
        shareDialog.type = type;
        return shareDialog;
    }

    public static ShareDialog show(Activity context, ShareParams params, boolean releaseOk, int type) {
        ShareDialog shareDialog = newInstance(params);
        shareDialog.show(context.getFragmentManager(), "sharedialog");
        shareDialog.releaseOk = releaseOk;
        shareDialog.type = type;
        return shareDialog;
    }

    public void setRmark(Rmark rmark) {
        this.rmark = rmark;
    }

    //end

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        File yodianDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "yodian");
        if (!yodianDir.exists()) {
            yodianDir.mkdirs();
        }
        tempFile = new File(yodianDir, System.currentTimeMillis() + ".png");
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        getDialog().getWindow().getAttributes().windowAnimations = R.style.ShareDialogAnimation;
        View inflate = inflater.inflate(R.layout.pop_share_view, container, false);
        if (releaseOk) {
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
        StringBuffer title = new StringBuffer();
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

    public interface Listener {
        void onClose();
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;

    private File tempFile;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        skill = args.getParcelable("skill");
        targetUid = args.getLong("targetUid");
        title = args.getString("title");
        redirect_url = args.getString("redirect_url");
        reportContent = args.getString("reportContent");
        targetNickname = args.getString("targetNickname");
        if (skill.getPic() != null && (skill.getPic().startsWith("http") || skill.getPic().startsWith("ftp"))) {
            Toast.makeText(getActivity(), "正在分享路上...", Toast.LENGTH_SHORT).show();
            ImageLoader.image(getActivity(), Uri.parse(skill.getPic()), this);
        } else {
            end = true;
        }
        if (User.read(getActivity()).getUid() != targetUid) {
            mReport.setVisibility(View.VISIBLE);
        } else {
            final View parent = (View) mReport.getParent();
            if (parent != null) {
                parent.setVisibility(View.GONE);
            }
            mContent.setRowCount(1);
        }


        shareView = view.findViewById(R.id.share);

        RoundImageView avater = (RoundImageView) shareView.findViewById(R.id.avatar);
        ImageView contentPic = (ImageView) shareView.findViewById(R.id.contenPic);


        TextView title = (TextView) shareView.findViewById(R.id.tv_skill_title);
        TextView price = (TextView) shareView.findViewById(R.id.tv_skill_price);
        TextView content = (TextView) shareView.findViewById(R.id.tv_skill_content);
        TextView nickname = (TextView) shareView.findViewById(R.id.tv_nickname);

        String path = skill.getPic();
        String avaterPath = skill.getAvatar();

        if (path != null) {

            Bitmap bitmap=ImageLoader.image(getActivity(), Uri.parse(path));
            if(bitmap==null){
                Toast.makeText(getActivity(),"分享失败",Toast.LENGTH_SHORT).show();
                dismiss();
            }
            contentPic.setImageBitmap(bitmap);

        }

        if (avaterPath != null) {
            Bitmap avaterBitmap=ImageLoader.image(getActivity(), Uri.parse(avaterPath));
            if(avaterBitmap==null){
                Toast.makeText(getActivity(),"分享失败",Toast.LENGTH_SHORT).show();
                dismiss();
            }
            avater.setImageBitmap(avaterBitmap);
        }

        title.setText(skill.getName());
        price.setText(skill.getPrice());
        content.setText(skill.getContent());
        nickname.setText(skill.getNickname());


    }

    /***
     * @param type 1 新浪分享　２　好友分享 3 其他
     */

    private Bitmap getShareBitmap(int type, View shareView) {
        Bitmap QRCodeBitmap = null;
        if (type == 1) {
            QRCodeBitmap = generatePlatformBitmap(R.drawable.ic_market_sina);
        } else if (type == 2) {
            QRCodeBitmap = generatePlatformBitmap(R.drawable.ic_market_wechat);
        }
        ImageView shareBrand = (ImageView) shareView.findViewById(R.id.share_brand);

        shareBrand.setImageBitmap(QRCodeBitmap);
        Bitmap shareBitmap = convertViewToBitmap(shareView);

        return shareBitmap;
    }

    /**
     * @param drawableId Type.Platform.WeiBo drawableId为R.drawble.ic_market_sina
     *                   <p>
     *                   Type.Platform.WEIXIN  drawableId为R.drawble.ic_market_wechat
     */
    private Bitmap generatePlatformBitmap(int drawableId) {
        int size = getResources().getDimensionPixelSize(R.dimen.qrcode_size);
        int left = getResources().getDimensionPixelSize(R.dimen.qrcode_left);
        int top = getResources().getDimensionPixelSize(R.dimen.qrcode_top);
        QRCode from = QRCode.from(skill.getQrcodeUrl());
        from.withSize(size, size);
        int border = 25;
        Bitmap qrcode = from.bitmap();
        Bitmap bitmap = Bitmap.createBitmap(qrcode.getWidth() - border * 0, qrcode.getHeight() - border * 0, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(border, border, bitmap.getWidth() - border, bitmap.getHeight() - border);
        canvas.clipRect(rect);
        canvas.drawBitmap(qrcode, 0, 0, new Paint());
        Bitmap temp = BitmapFactory.decodeResource(getResources(), drawableId);
        Bitmap bg = temp.copy(temp.getConfig(), true);
        temp.recycle();
        Bitmap QRCodeBitmap = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.RGB_565);
        Canvas can = new Canvas(QRCodeBitmap);
        can.drawBitmap(bg, 0, 0, new Paint());
        can.drawBitmap(bitmap, left, top, new Paint());

        return QRCodeBitmap;
    }


    public static Bitmap
    convertViewToBitmap(View view) {

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.destroyDrawingCache();
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
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
        if (tempFile != null) tempFile.deleteOnExit();
        if (this.listener != null) {
            this.listener.onClose();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Window window = getDialog().getWindow();
//        window.setGravity(Gravity.BOTTOM);
    }

    @OnClick(R.id.qqRoom)
    public void qzone(final View v) {
        IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.QQ);
        iShareManager.share(new MessageWebpage(title, skill.getContent(), redirect_url, tempFile.getPath()), QQShareManager.SHARE_TYPE_QZONE);
    }

    @OnClick(R.id.report)
    public void report(View v) {
        scid = 0;
        sid = 0;
        rid = 0;
        switch (type) {
            case 1:
                sid = skill.getId();
                break;
            case 2:
                if (rmark != null)
                    scid = rmark.getId();
                break;
            case 3:
                rid = skill.getUid();
                break;
        }

        AlertDialog alertDialog = AlertDialog.newInstance("", "你确定要举报吗？").setNegativeListener(new AlertDialog.NegativeListener() {
            @Override
            public void onNegativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public String negativeText() {
                return "取消";
            }
        }).setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(DialogInterface dialog) {
                SkillService skillService = Network.getService(SkillService.class);
                skillService.report(type, scid, sid, rid, new ToastCallback(getActivity()));
            }

            @Override
            public String positiveText() {
                return "确定";
            }
        });
        alertDialog.show(getFragmentManager(), "");

    }

    @OnClick(R.id.sina)
    public void ShareToWeiBo(View view) {

        Bitmap bitmap = getShareBitmap(1, shareView);
        StringBuffer content = new StringBuffer();
        content.append(title).append(skill.getPrice()).append(skill.getUnit()).append("@优点APP");
        IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.WEIBO);
        iShareManager.share(new MessageWebpage("", content.toString(), redirect_url, bitmap), WeiboShareManager.WEIBO_SHARE_TYPE/*,this*/);
    }


    @OnClick({R.id.fridens, R.id.weixin})
    public void ShareToWeiXin(View v) {
        if (!end) {
            Toast.makeText(getActivity(), "还未准备完成，请稍后...", Toast.LENGTH_SHORT).show();
            return;
        }

        IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.WEIXIN);
        if (v.getId() == R.id.weixin) {
            iShareManager.share(new MessageWebpage(title, skill.getContent(), redirect_url, tempFile.toString()), WechatShareManager.WEIXIN_SHARE_TYPE_TALK/*,this*/);
        } else {
            Bitmap bitmap = getShareBitmap(2, shareView);
            iShareManager.share(new MessagePic(bitmap), WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS/*,this*/);
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

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(tempFile));
            skill.setPic(tempFile.toString());
            end = true;
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
