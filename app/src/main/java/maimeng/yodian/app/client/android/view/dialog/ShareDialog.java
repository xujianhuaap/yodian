package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.tencent.open.utils.SystemUtils;
import com.umeng.analytics.MobclickAgent;

import net.glxn.qrgen.android.QRCode;

import org.henjue.library.share.Type;
import org.henjue.library.share.manager.IShareManager;
import org.henjue.library.share.manager.QQShareManager;
import org.henjue.library.share.manager.ShareFactory;
import org.henjue.library.share.manager.WechatShareManager;
import org.henjue.library.share.manager.WeiboShareManager;
import org.henjue.library.share.model.MessagePic;
import org.henjue.library.share.model.MessageWebpage;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.widget.YDView;


/**
 * Created by android on 2015/5/26.
 */
public class ShareDialog extends DialogFragment {

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
    private int type;
    private long scid;
    private long sid;
    private long rid;
    private View shareView;
    private TextView releaseok;
    private TextView weixin;
    private TextView fridens;
    private TextView sina;
    private TextView qqRoom;
    private TextView mReport;
    private GridLayout mContent;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


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
        shareView =inflater.inflate(R.layout.view_share,null);
        this.mContent = (GridLayout) inflate.findViewById(R.id.content);
        this.mReport = (TextView) inflate.findViewById(R.id.report);
        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report(v);
            }
        });
        this.qqRoom = (TextView) inflate.findViewById(R.id.qqRoom);
        qqRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qzone(v);
            }
        });
        this.sina = (TextView) inflate.findViewById(R.id.sina);
        this.sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareToWeiBo(v);
            }
        });
        this.fridens = (TextView) inflate.findViewById(R.id.fridens);
        this.weixin = (TextView) inflate.findViewById(R.id.weixin);
        weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareToWeiXin(v);
            }
        });
        fridens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareToWeiXin(v);
            }
        });
        this.releaseok = (TextView) inflate.findViewById(R.id.release_ok);
        if (releaseOk) {
            inflate.findViewById(R.id.release_ok).setVisibility(View.VISIBLE);
        }

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
        args.putParcelable("skill", Parcels.wrap(skill));
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
        skill = Parcels.unwrap(args.getParcelable("skill"));
        targetUid = args.getLong("targetUid");
        title = args.getString("title");
        redirect_url = args.getString("redirect_url");
        reportContent = args.getString("reportContent");
        targetNickname = args.getString("targetNickname");

        if (skill.getPic() != null && (skill.getPic().startsWith("http") || skill.getPic().startsWith("ftp"))) {
            Toast.makeText(getActivity(), "正在分享路上...", Toast.LENGTH_SHORT).show();
            DataSource<CloseableReference<CloseableImage>> sub = Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(Uri.parse(skill.getPic())), null);
            sub.subscribe(new BaseBitmapDataSubscriber() {

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }

                @Override
                protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                    if (bitmap != null) {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(tempFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            end = true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }, AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            end = true;
        }
        if (User.read(getActivity()).getUid() != targetUid) {
            mReport.setVisibility(View.VISIBLE);
        } else {
            mReport.setVisibility(View.GONE);
//            final View parent = (View) mReport.getParent();
//            if (parent != null) {
//                parent.setVisibility(View.GONE);
//            }
            mContent.setRowCount(1);
        }

        YDView avater = (YDView) shareView.findViewById(R.id.avatar);
        YDView contentPic = (YDView) shareView.findViewById(R.id.contenPic);
        ViewGroup.LayoutParams lp = contentPic.getLayoutParams();
        lp.width=getResources().getDisplayMetrics().widthPixels;
        contentPic.setLayoutParams(lp);

        TextView title = (TextView) shareView.findViewById(R.id.tv_skill_title);
        TextView price = (TextView) shareView.findViewById(R.id.tv_skill_price);
        TextView nickname = (TextView) shareView.findViewById(R.id.tv_nickname);

        String path = skill.getPic();
        String avaterPath = skill.getAvatar();

        if (path != null) {
            contentPic.setImageURI(Uri.parse(path));
        }

        if (avaterPath != null) {
            avater.setImageURI(Uri.parse(avaterPath));

        }

        title.setText(skill.getName());
        price.setText(Html.fromHtml(getResources().getString(R.string.share_price,skill.getPrice(),skill.getUnit())));
        nickname.setText(skill.getNickname());


    }

    /***
     * @param type 1 新浪分享　２　好友分享 3 其他
     */

    private Bitmap getShareBitmap(int type, View shareView) {
        Bitmap QRCodeBitmap = generatePlatformBitmap();;
        ImageView shareBrand = (ImageView) shareView.findViewById(R.id.share_brand);

        shareBrand.setImageBitmap(QRCodeBitmap);
        Bitmap shareBitmap = convertViewToBitmap(shareView);

        return shareBitmap;
    }

    /**
     */
    private Bitmap generatePlatformBitmap() {
        int size = getResources().getDimensionPixelSize(R.dimen.qrcode_size);
        int left = getResources().getDimensionPixelSize(R.dimen.qrcode_left);
        int top = getResources().getDimensionPixelSize(R.dimen.qrcode_top);
        QRCode from = QRCode.from(skill.getQrcodeUrl());
        from.withSize(size, size);
        int border = 25;
        Bitmap qrcode = from.bitmap();
        return qrcode;
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
//        bitmap=Bitmap.createBitmap( view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas=new Canvas(bitmap);
//        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0, R.style.ShareDialogStyle);
        app = (YApplication) getActivity().getApplication();
        MobclickAgent.onEvent(getActivity(), UEvent.SKILL_SHARE);
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

    public void qzone(final View v) {
        boolean isInstall = SystemUtils.checkMobileQQ(getActivity());
        if (isInstall) {
            MobclickAgent.onEvent(getActivity(), UEvent.SKILL_SHARE_QQ);
            IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.QQ);
            iShareManager.share(new MessageWebpage(title, skill.getContent(), redirect_url, tempFile.getPath()), QQShareManager.SHARE_TYPE_QZONE);
        } else {
            Toast.makeText(getActivity(), "请安装QQ客户端", Toast.LENGTH_SHORT).show();
        }

    }

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

    public void ShareToWeiBo(View view) {
        MobclickAgent.onEvent(getActivity(), UEvent.SKILL_SHARE_SINA);
        Bitmap bitmap = getShareBitmap(1, shareView);
        StringBuffer content = new StringBuffer();
        content.append(title).append(skill.getPrice()).append(skill.getUnit()).append("@优点APP");
        IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.WEIBO);
        iShareManager.share(new MessageWebpage("", content.toString(), redirect_url, bitmap), WeiboShareManager.WEIBO_SHARE_TYPE/*,this*/);
    }


    public void ShareToWeiXin(View v) {
        if (!end) {
            Toast.makeText(getActivity(), "还未准备完成，请稍后...", Toast.LENGTH_SHORT).show();
            return;
        }

        IShareManager iShareManager = ShareFactory.create(getActivity(), Type.Platform.WEIXIN);
        if (v.getId() == R.id.weixin) {
            MobclickAgent.onEvent(getActivity(), UEvent.SKILL_SHARE_WECHAT);
            iShareManager.share(new MessageWebpage(title, skill.getContent(), redirect_url, tempFile.toString()), WechatShareManager.WEIXIN_SHARE_TYPE_TALK/*,this*/);

        } else {
            MobclickAgent.onEvent(getActivity(), UEvent.SKILL_SHARE_FRIENDS);
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

}
