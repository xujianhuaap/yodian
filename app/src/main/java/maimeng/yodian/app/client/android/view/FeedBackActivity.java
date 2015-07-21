package maimeng.yodian.app.client.android.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.fb.model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.model.User;

/**
 * Created by android on 2015/7/21.
 */
public class FeedBackActivity extends AbstractActivity implements PtrHandler {
    private View mBtnBack;
    private RecyclerView mRecyclerView;
    private PtrFrameLayout mRefreshLayout;
    private EditText mFeedBackContent;
    private View mBtnFeedBack;
    private FeedbackAgent mAgent;
    private Conversation mComversation;
    private Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mBtnBack=findViewById(R.id.btn_back);
        ViewCompat.setTransitionName(mBtnBack, "back");
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(FeedBackActivity.this);
            }
        });
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        mRefreshLayout=(PtrFrameLayout)findViewById(R.id.refresh_layout);
        mFeedBackContent=(EditText)findViewById(R.id.feedback_content);
        mBtnFeedBack=findViewById(R.id.btn_feedback);
        mBtnFeedBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = mFeedBackContent.getText().toString();
                mFeedBackContent.getEditableText().clear();
                if (!TextUtils.isEmpty(content)) {
                    mComversation.addUserReply(content);//添加到会话列表
                    sync();
                }

            }
        });
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header=new StoreHouseHeader(this);
        header.setPadding(0, (int) getResources().getDimension(R.dimen.pull_refresh_paddingTop), 0, 0);
        header.initWithString("YoDian");
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layout);

        adapter=new Adapter(this,null);
        mRecyclerView.setAdapter(adapter);
        mAgent = new FeedbackAgent(this);
        UserInfo userinfo = mAgent.getUserInfo();
        Map<String, String> contact = userinfo.getContact();
        User read = User.read(this);
        contact.put("nickname", read.getNickname());
        contact.put("email", read.getNickname());
        userinfo.setContact(contact);
        mComversation = mAgent.getDefaultConversation();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAgent.updateUserInfo();
            }
        });
        sync();

    }
    // 数据同步
    private void sync() {

        mComversation.sync(new SyncListener() {
            @Override
            public void onSendUserReply(List<Reply> replyList) {
            }
            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                mRefreshLayout.refreshComplete();
                List<Reply> data = mComversation.getReplyList();
                if (data == null || data.size() < 1) {
                    return;
                }
                adapter.reload(data, false);
                adapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(adapter.getItemCount()-1);
            }
        });
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        sync();
    }

    class Adapter extends AbstractAdapter<Reply,ViewHolder>{
        public static final int VIEW_TYPE_DEV=1;
        public static final int VIEW_TYPE_USER=2;
        final SimpleDateFormat format;
        public Adapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
            super(context, viewHolderClickListener);
            format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType==VIEW_TYPE_USER?new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_feedback_item_user,parent,false)):new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_feedback_item_dev,parent,false));
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Reply data = getItem(position);
            if(holder.getItemViewType()==VIEW_TYPE_USER){
                holder.mContent.setText(data.content);
                holder.mDateTime.setText(format.format(new Date(data.created_at)));
            }else{
                holder.mContent.setText(data.content);
                holder.mDateTime.setText(format.format(new Date(data.created_at)));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return Reply.TYPE_DEV_REPLY.endsWith(getItem(position).type)?VIEW_TYPE_DEV:VIEW_TYPE_USER;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mDateTime;
        public final TextView mContent;
        public ViewHolder(View itemView) {
            super(itemView);
            mContent=(TextView)itemView.findViewById(R.id.content);
            mDateTime=(TextView)itemView.findViewById(R.id.datetime);
        }
    }
}
