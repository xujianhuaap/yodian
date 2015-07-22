package maimeng.yodian.app.client.android.view.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.ChatSessionListAdapter;
import maimeng.yodian.app.client.android.model.chat.Session;

/**
 * Created by android on 2015/7/22.
 */
public class ChatSessionListFragment extends Fragment implements AbstractAdapter.ViewHolderClickListener<ChatSessionListAdapter.ViewHolder>, PtrHandler {
    private RecyclerView mRecyclerView;
    private PtrFrameLayout mRefreshLayout;
    private ChatSessionListAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_list,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mRefreshLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header=new StoreHouseHeader(getActivity());
        header.setPadding(0, (int) getResources().getDimension(R.dimen.pull_refresh_paddingTop), 0, 0);
        header.initWithString("YoDian");
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layout);
        adapter=new ChatSessionListAdapter(this,this);
        mRecyclerView.setAdapter(adapter);
        mRefreshLayout.autoRefresh();
    }

    /**
     * 获取所有会话
     * @return
     */
    private List<Session> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Session> list = new ArrayList<Session>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(new Session(getActivity(),sortItem.second));
        }
        return list;
    }
    /**
     * 根据最后一条消息的时间排序
     *
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
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

    private void sync() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.refreshComplete();
                EMChatManager.getInstance().loadAllConversations();
                List<Session> chatList = loadConversationsWithRecentChat();
                adapter.reload(chatList, false);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onItemClick(ChatSessionListAdapter.ViewHolder holder, int postion) {
        Session session = adapter.getItem(postion);
        ChatActivity.ChatIntent intent=new ChatActivity.ChatIntent(session.getUsername());
        startActivity(intent);
    }

    @Override
    public void onClick(ChatSessionListAdapter.ViewHolder holder, View clickItem, int postion) {

    }
}
