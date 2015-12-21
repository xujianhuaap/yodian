package maimeng.yodian.app.client.android.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.Constant;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.adapter.ChatAllHistoryAdapter;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.chat.domain.User;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.model.chat.ChatUser;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class ChatAllHistoryFragment extends Fragment implements View.OnClickListener, PtrHandler {

    private static final String SHOW_TITLE = "_show_title";
    private InputMethodManager inputMethodManager;
    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    //	private EditText query;
//	private ImageButton clearSearch;
    public RelativeLayout errorItem;
    private PtrFrameLayout mRefreshLayout;

    public static ChatAllHistoryFragment getInstance(boolean showTitle) {
        ChatAllHistoryFragment fragment = new ChatAllHistoryFragment();
        Bundle opt = new Bundle();
        opt.putBoolean(SHOW_TITLE, showTitle);
        fragment.setArguments(opt);
        return fragment;
    }

    /**
     * @Deprecated use {@link #getInstance(boolean)}
     */
    @Deprecated
    public ChatAllHistoryFragment() {

    }

    public TextView errorText;
    private boolean hidden;
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conversation_history, container, false);

        root.findViewById(R.id.title_containar).setVisibility(getArguments().getBoolean(SHOW_TITLE, false) ? View.VISIBLE : View.GONE);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRefreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(getContext());
        header.setTextColor(0x000000);
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        conversationList.addAll(loadConversationsWithRecentChat());
        listView = (ListView) getView().findViewById(R.id.list);
        ImageView noNews = (ImageView) getView().findViewById(R.id.no_news);
        if (conversationList.size() == 0) {
            noNews.setVisibility(View.VISIBLE);
            noNews.setImageResource(R.mipmap.ic_no_news);
        } else {
            noNews.setVisibility(View.GONE);
        }
        adapter = new ChatAllHistoryAdapter(getActivity(), conversationList);
        // 设置adapter
        listView.setAdapter(adapter);
        final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                Map<String, User> users = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
                User user = users.get(conversation.getUserName());
//                Skill skill = Skill.parse(msg);
                ChatUser chatUser = new ChatUser(user.getUsername(), user.getId(), user.getNick());
                chatUser.setMobile(user.getMobile());
                chatUser.setQq(user.getQq());
                chatUser.setWechat(user.getWechat());
                String username = conversation.getUserName();
                if (username.equals(DemoApplication.getInstance().getUserName()))
                    Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                else {
                    if (conversation.isGroup()) {

                    } else {
                        // it is single chat
                        //ChatActivity.show(view.getContext(), skill, chatUser);
                        ChatActivity.show(view.getContext(), chatUser);
                    }
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                hideSoftKeyboard();
                return false;
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
        refresh();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
    }

    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int position = ((AdapterContextMenuInfo) menuInfo).position;
        EMConversation item = adapter.getItem(position);
        if (item != null && "hx_admin".equals(item.getUserName())) {
            return;
        }
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
            handled = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
            handled = true;
        }
        ArrayAdapter a;
        EMConversation tobeDeleteCons = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
        adapter.remove(tobeDeleteCons);
        adapter.notifyDataSetChanged();

        // 更新消息未读数
        ((MainActivity) getActivity()).updateUnreadLabel();

        return handled || super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        conversationList.clear();
        List<EMConversation> collection = loadConversationsWithRecentChat();
        conversationList.addAll(collection);
        mRefreshLayout.refreshComplete();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     *
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
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
                    EMMessage lastMessage = conversation.getLastMessage();
                    if ("hx_admin".equals(lastMessage.getUserName())) {
                        continue;
                    }
                    User user = User.parse(lastMessage);
                    RobotUser robot = RobotUser.parse(lastMessage);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
                    sortList.add(new Pair<Long, EMConversation>(lastMessage.getMsgTime(), conversation));
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
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        EMConversation hx_admin = conversations.get("hx_admin");
        if (hx_admin != null) {
            list.add(0, hx_admin);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @Override
    public void onClick(View v) {
    }
}
