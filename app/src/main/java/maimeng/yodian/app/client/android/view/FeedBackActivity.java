package maimeng.yodian.app.client.android.view;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.chat.widget.PasteEditText;
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
    private Button mBtnMore;
    private InputMethodManager mInputMethodmanagr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.FeedBackAnim);
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
        mFeedBackContent=(EditText)findViewById(R.id.et_sendmessage);
        mBtnFeedBack=findViewById(R.id.btn_send);
        mBtnFeedBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = mFeedBackContent.getText().toString();
                mFeedBackContent.getEditableText().clear();


                mInputMethodmanagr.hideSoftInputFromWindow(mFeedBackContent.getWindowToken(),0);


                if (!TextUtils.isEmpty(content)) {
                    mComversation.addUserReply(content);//添加到会话列表
                    sync();
                }
                mBtnFeedBack.setVisibility(View.GONE);
                mBtnMore.setVisibility(View.VISIBLE);
            }
        });

        mBtnMore=(Button)findViewById(R.id.btn_more);
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

        mInputMethodmanagr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mFeedBackContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean
            onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        mFeedBackContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                  mBtnMore.setVisibility(View.GONE);
                  mBtnFeedBack.setVisibility(View.VISIBLE);


            }
        });


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

    class Adapter extends AbstractAdapter<Reply,ViewHolder> {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,010,1001,"关闭").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== 010){
            finish();

        }
        return super.onOptionsItemSelected(item);
    }






}
