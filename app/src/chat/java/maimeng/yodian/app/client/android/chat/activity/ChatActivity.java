/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package maimeng.yodian.app.client.android.chat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.adapter.ExpressionAdapter;
import maimeng.yodian.app.client.android.chat.adapter.ExpressionPagerAdapter;
import maimeng.yodian.app.client.android.chat.adapter.MessageAdapter;
import maimeng.yodian.app.client.android.chat.adapter.VoicePlayClickListener;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.chat.domain.User;
import maimeng.yodian.app.client.android.chat.utils.CommonUtils;
import maimeng.yodian.app.client.android.chat.utils.ImageUtils;
import maimeng.yodian.app.client.android.chat.utils.SmileUtils;
import maimeng.yodian.app.client.android.chat.widget.ExpandGridView;
import maimeng.yodian.app.client.android.chat.widget.PasteEditText;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.Address;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.chat.ChatUser;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.chat.ContactPathActivity;
import maimeng.yodian.app.client.android.view.common.AcceptAddressActivity;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.user.SettingUserInfo;
import maimeng.yodian.app.client.android.widget.YDView;


/**
 * 聊天页面
 * <p/>
 * 技能属于卖家　聊天发起方可能是卖家也可能是买家
 * 如果买家发起聊天　即联系卖家　有以下入口：技能详情
 */
public class ChatActivity extends BaseActivity implements OnClickListener, EMEventListener {
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;



    public static final int MESSAGE_VOICE = 1;
    public static final int MESSAGE_TEXT = 2;
    public static final int MESSAGE_PIC = 3;
    public static final int MESSAGE_NAME_CARD = 4;
    public static final int MESSAGE_VIDO = 5;
    public static final int MESSAGE_FILE = 6;
    public static final int MESSAGE_LOCATION = 7;


    public static final int FLAG_PUSH = 3;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private static final String LOG_TAG = ChatActivity.TAG;
    private static final int REQUEST_CODE_INPUT_ADDRESS = 8;
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private PasteEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private GridView btnContainer;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private EMConversation conversation;
    // 给谁发送消息
    private String toChatUsername;
    private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private File cameraFile;
    static int resendPos;


    private ImageView iv_emoticons_normal;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    public String playMsgId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    public EMGroup group;
    public EMChatRoom room;
    public boolean isRobot;
    private ChatUser chatUser;
    private int MENU_ID_CONTACT;
    private maimeng.yodian.app.client.android.model.user.User user;

    public Skill getSkill() {
        return skill;
    }

    private Skill skill;
    private OrderInfo order;
    private LinearLayout bannerContainer;
    private TextView btnBannerShow;
    private YDView bannerPic;
    private TextView bannerName;
    private TextView bannerText;


    public static void show(Context context, ChatUser chatUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chatUser", Parcels.wrap(chatUser));
        context.startActivity(intent);
    }

    public static void show(Context context, Skill skill, ChatUser chatUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("skill", Parcels.wrap(skill));
        intent.putExtra("chatUser", Parcels.wrap(chatUser));
        context.startActivity(intent);
    }
    public static void show(Context context, OrderInfo order, ChatUser chatUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("order", Parcels.wrap(order));
        intent.putExtra("chatUser", Parcels.wrap(chatUser));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        MobclickAgent.onEvent(this, UEvent.CONVERSATION_CHAT);
        Intent intent = getIntent();
        user= maimeng.yodian.app.client.android.model.user.User.read(this);
        chatUser = get("chatUser");
        if ("hx_admin".equals(chatUser.getChatName())) {
            create(CommonService.class).getCustomer(new Callback<String>() {
                @Override
                public void start() {

                }

                @Override
                public void success(String s, Response response) {
                    try {
                        JSONObject res = new JSONObject(s);
                        if (res.getInt("code") == 20000) {
                            JSONObject customer = res.getJSONObject("data").getJSONObject("customer");
                            chatUser.setMobile(customer.getString("contact"));
                            chatUser.setQq(customer.getString("qq"));
                            chatUser.setWechat(customer.getString("weichat"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void failure(HNetError hNetError) {

                }

                @Override
                public void end() {

                }
            });
        }
        setTitle(chatUser.getNickName());
        if (intent.hasExtra("skill")) {
            skill = get("skill");
        }
        if (intent.hasExtra("order")) {
            order = get("order");
        }

        toChatUsername = chatUser.getChatName();
        initView();
        setUpView();
        showBannerIfExist(skill);

    }


    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
            mTitle.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_menu, menu);
        if ("hx_admin".equals(chatUser.getChatName())) {
            MENU_ID_CONTACT = 1001;
            menu.add(0, MENU_ID_CONTACT, 0, "").setIcon(R.mipmap.ic_contact_path).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }


    private class ViewEntry {
        private final Drawable icon;
        private final String text;
        private final Intent intent;
        private final int requestCode;
        private final OnClickListener onClickListener;
        private boolean enable = true;

        ViewEntry(Drawable icon, String text, Intent intent, int requestCode) {
            this.icon = icon;
            this.text = text;
            this.intent = intent;
            this.onClickListener = null;
            this.requestCode = requestCode;
        }

        ViewEntry(Drawable icon, String text, OnClickListener onClickListener) {
            this.icon = icon;
            this.text = text;
            this.intent = null;
            this.onClickListener = onClickListener;
            this.requestCode = -1;
        }
    }

    private class ViewHolder {
        public final TextView icon;

        ViewHolder(TextView v) {
            this.icon = v;
            this.icon.setTextColor(getResources().getColor(R.color.colorPrimary));
            this.icon.setPadding(0, 40, 0, 40);
            this.icon.setGravity(Gravity.CENTER);
        }
    }

    private List<ViewEntry> entries = new ArrayList<>();

    /**
     * initView
     */

    protected void initView() {
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        btnContainer = (GridView) findViewById(R.id.ll_btn_container);
        bannerContainer = (LinearLayout) findViewById(R.id.banner_container);
        bannerPic = (YDView) findViewById(R.id.banner_pic);
        bannerName = (TextView) findViewById(R.id.banner_name);
        bannerText = (TextView) findViewById(R.id.banner_text);
        btnBannerShow = (TextView)findViewById(R.id.btn_banner_show);
        btnBannerShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(skill!=null) {
                    MobclickAgent.onEvent(v.getContext(), UEvent.CONVERSATION_VIEW_SKILL);
                    startActivity(new Intent(v.getContext(), SkillDetailsActivity.class).putExtra("skill", Parcels.wrap(skill)));
                }else if(order!=null){
                    sendOrderToAdmin();
                }
            }
        });
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return entries.size();
            }

            @Override
            public ViewEntry getItem(int position) {
                return entries.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    final TextView textView = new TextView(ChatActivity.this);
                    convertView = textView;

                    holder = new ViewHolder((TextView) convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                final ViewEntry item = getItem(position);
                holder.icon.setText(item.text);
                holder.icon.setTag(R.id.tag_item, item);
                holder.icon.setEnabled(item.enable);
                holder.icon.setCompoundDrawablesWithIntrinsicBounds(null, item.icon, null, null);
                holder.icon.setCompoundDrawablePadding(10);
                if (item.onClickListener != null && item.intent == null) {
                    holder.icon.setOnClickListener(item.onClickListener);
                }
                if (item.onClickListener == null && item.intent != null) {
                    holder.icon.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewEntry entry = (ViewEntry) v.getTag(R.id.tag_item);
                            startActivityForResult(entry.intent, entry.requestCode);
                        }
                    });
                }
                return convertView;
            }
        };
        btnContainer.setAdapter(adapter);
        initEntryData(entries);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        more = findViewById(R.id.more);
        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{getResources().getDrawable(R.mipmap.record_animate_01),
                getResources().getDrawable(R.mipmap.record_animate_02),
                getResources().getDrawable(R.mipmap.record_animate_03),
                getResources().getDrawable(R.mipmap.record_animate_04),
                getResources().getDrawable(R.mipmap.record_animate_05),
                getResources().getDrawable(R.mipmap.record_animate_06),
                getResources().getDrawable(R.mipmap.record_animate_07),
                getResources().getDrawable(R.mipmap.record_animate_08),
                getResources().getDrawable(R.mipmap.record_animate_09),
                getResources().getDrawable(R.mipmap.record_animate_10),
                getResources().getDrawable(R.mipmap.record_animate_11),
                getResources().getDrawable(R.mipmap.record_animate_12),
                getResources().getDrawable(R.mipmap.record_animate_13),
                getResources().getDrawable(R.mipmap.record_animate_14),};

        // 表情list
        reslist = getExpressionRes(35);
        // 初始化表情viewpager
        List<View> views = new ArrayList<>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        edittext_layout.requestFocus();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());

        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;

                            try {
                                    messages = conversation.loadMoreMsgFromDB(ChatActivity.this.adapter.getItem(0).getMsgId(), pagesize);
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            if (messages.size() > 0) {
                                ChatActivity.this.adapter.notifyDataSetChanged();
                                ChatActivity.this.adapter.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.no_more_messages), Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void sendOrderToAdmin() {
        StringBuffer sb=new StringBuffer();
        maimeng.yodian.app.client.android.model.user.User user = maimeng.yodian.app.client.android.model.user.User.read(this);
        sb.append("优点名:").append(user.getNickname()).append("\n")
            .append("订单编号:").append(order.getId()).append("\n")
                .append("订单状态:").append(bannerText.getText().toString());
        sendText(sb.toString());
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            menu.findItem(R.id.menu_chat_clear).setVisible(true);
            MenuItem item = menu.findItem(R.id.menu_chat_clear);
            item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpView() {
        iv_emoticons_normal.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
            Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }

        // for chatroom type, we only init conversation and create view adapter on success
            onConversationInit();

            onListViewCreation();

            // show forward message if the message is not null
            String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
            if (forward_msg_id != null) {
                // 显示发送要转发的消息
                forwardMessage(forward_msg_id);
            }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_chat_clear) {
            emptyHistory();
        }
        int itemId = item.getItemId();
        if (itemId == MENU_ID_CONTACT) {
            ContactPathActivity.show(ChatActivity.this, chatUser.getQq(), chatUser.getMobile(), chatUser.getWechat());
        } else if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onConversationInit() {
        conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.Chat);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        boolean handlerSkill = false;
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                EMMessage emMessage = msgs.get(0);


                msgId = emMessage.getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize);
        }
        final EMMessage msg = conversation.getMessage(0);
        if (msg != null) {
            showBannerIfExist(Skill.parse(msg));
        }
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
                        finish();
                    }
                }
            }

        });
    }

    protected void onListViewCreation() {
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername);
        adapter.setUid(chatUser.getUid());
        // 显示消息
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
    }

    protected void onGroupViewCreation() {
        group = EMGroupManager.getInstance().getGroup(toChatUsername);

        if (group != null) {
            ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
        } else {
            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
        }
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_INPUT_ADDRESS && resultCode==RESULT_OK){
            sendAddress();
        }
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                    // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
                    // ((TextMessageBody) copyMsg.getBody()).getMessage()));
                    clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                    break;
                case RESULT_CODE_DELETE: // 删除消息
                    EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                    conversation.removeMessage(deleteMsg.getMsgId());
                    adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
                    break;

                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                Bitmap bitmap = null;
                FileOutputStream fos = null;
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    if (bitmap == null) {
                        EMLog.d("chatactivity", "problem start video thumbnail bitmap,use default icon");
                        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_panel_video_icon);
                    }
                    fos = new FileOutputStream(file);

                    bitmap.compress(CompressFormat.JPEG, 100, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fos = null;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                }
                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFile(uri);
                    }
                }

            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    toggleMore(more);
                    sendLocationMsg(latitude, longitude, "", locationAddress);
                } else {
                    String st = getResources().getString(R.string.unable_to_get_loaction);
                    Toast.makeText(this, st, Toast.LENGTH_SHORT).show();
                }
                // 重发消息
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
                    || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }

                }
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                addUserToBlacklist(deleteMsg.getFrom());
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }

    private boolean enableTakePic = false;
    private boolean enableImage = true;
    private boolean enableVoiceCall = false;
    private boolean enableVideoCall = false;
    private boolean showWechatVcard = true;
    private boolean showVideo = false;
    private boolean showFile = false;
    private boolean showAddress = true;

    private void initEntryData(List<ViewEntry> entries) {
        if (entries == null) {
            entries = new ArrayList<>();
        } else {
            entries.clear();
        }
        if (enableTakePic) {
            entries.add(new ViewEntry(getResources().getDrawable(R.drawable.chat_takepic_selector), getResources().getString(R.string.attach_take_pic), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPicFromCamera();// 点击照相图标
                }
            }));
        }
        if (enableImage) {
            entries.add(new ViewEntry(getResources().getDrawable(R.drawable.chat_image_selector), getResources().getString(R.string.attach_picture), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPicFromLocal(); // 点击图片图标
                }
            }));
        }
        if (showWechatVcard) {
            entries.add(new ViewEntry(getResources().getDrawable(R.drawable.chat_vcard_selector), "名片", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(v.getContext(), UEvent.CONVERSATION_SEND_CARD);
                    sendText("", true);
                }
            }));//微信名片
        }
        if (showAddress) {
            entries.add(new ViewEntry(getResources().getDrawable(R.drawable.address_selector), "地址", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MobclickAgent.onEvent(v.getContext(), UEvent.CONVERSATION_SEND_CARD);
                    sendAddress();
                }
            }));//收货地址
        }
        if (showVideo)
            entries.add(new ViewEntry(getResources().getDrawable(R.drawable.chat_video_selector), getResources().getString(R.string.attach_video), new Intent(ChatActivity.this, ImageGridActivity.class), REQUEST_CODE_SELECT_VIDEO));// 点击摄像图标
        if (showFile)
            entries.add(new ViewEntry(getResources().getDrawable(R.drawable.chat_file_selector), getResources().getString(R.string.attach_file), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectFileFromLocal();// 点击文件图标
                }
            }));
        ((BaseAdapter) btnContainer.getAdapter()).notifyDataSetChanged();
    }
    private void sendAddress(){
        Address address = Address.readAcceptAddress(this);
        if(TextUtils.isEmpty(address.getAddress())){
            AcceptAddressActivity.show(ChatActivity.this,REQUEST_CODE_INPUT_ADDRESS,null);
        }else {
            StringBuffer sb = new StringBuffer();
            sb.append(address.getProvince()).append(" ")
                    .append(address.getCity()).append(" ")
                    .append(address.getDistrict()).append("\n")
                    .append(address.getAddress()).append("\n\n")
                    .append(address.getName()).append("\n")
                    .append(address.getMobile()).append("");
            mEditTextContent.getText().append(sb.toString());
        }
    }
    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        String st1 = getResources().getString(R.string.not_connect_to_server);
        int id = view.getId();
        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            String s = mEditTextContent.getText().toString();
            sendText(s);
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.GONE);
            emojiIconContainer.setVisibility(View.VISIBLE);
            hideKeyboard();
        }
    }
    /**
     * 刷新技能信息
     *
     * @param skill
     */
    private void showBannerIfExist(final Skill skill) {
        final Context mContext=this;
        if (skill != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bannerContainer.setVisibility(View.VISIBLE);
                    final String pic = skill.getPic();
                    bannerPic.setImageURI(Uri.parse(pic));
                    bannerName.setText(skill.getName());
                    bannerText.setText(Html.fromHtml(getResources().getString(R.string.lable_price, skill.getPrice(), skill.getUnit())));
                }
            });
        }else if(order!=null){
            btnBannerShow.setText(R.string.button_send);
            final boolean isSaled = order.getSeller_id() == maimeng.yodian.app.client.android.model.user.User.read(ChatActivity.this).getUid();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bannerContainer.setVisibility(View.VISIBLE);
                    final String pic = order.getSkill().getPic();
                    bannerPic.setImageURI(Uri.parse(pic));
                    String statusStr="";
                    switch (order.getStatus()) {
                        case 0:
                            statusStr = mContext.getString(R.string.order_status_unpay);

                            break;
                        case 1:
                            //后台预留
                            statusStr = mContext.getString(R.string.order_status_delete);
                            break;
                        case 2:
                            statusStr = mContext.getString(R.string.buyer_operator_wait_accept);

                            break;
                        case 3:
                            statusStr = mContext.getString(R.string.buyer_operator_wait_send);

                            break;
                        case 4:
                            statusStr = mContext.getString(R.string.order_status_send_goods);

                            break;
                        case 5:
                            statusStr = mContext.getString(R.string.order_status_confirm_deal);

                            break;
                        case 6:
                            //订单关闭
                            if (isSaled) {
                                statusStr = mContext.getString(R.string.order_status_buyer_close);
                            } else {
                                statusStr = mContext.getString(R.string.order_status_close);
                            }
                            break;
                        case 7:
                            //订单取消
                            if (isSaled) {
                                statusStr = mContext.getString(R.string.order_status_buyer_cancle);
                            } else {
                                statusStr = mContext.getString(R.string.order_status_cancle);
                            }
                            break;
                        default:
                            statusStr = null;
                            break;
                    }
                    bannerName.setText(order.getSkill().getName());
                    bannerText.setText(statusStr);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bannerContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * 事件监听
     * <p/>
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {
                //获取到message
                final EMMessage message = (EMMessage) event.getData();
                String username = null;
                //群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    //单聊消息
                    username = message.getFrom();
                }

                //如果是当前会话的消息，刷新聊天页面
                if (username.equals(getToChatUsername())) {
                    refreshUIWithNewMessage();
                    //声音和震动提示有新消息
                    HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    //如果消息不是和当前聊天ID的消息
                    HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onNewMessage(message);
                        handlerSkillBanner(message);//处理技能banner信息
                    }
                });
                break;
            }
            case EventDeliveryAck: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                refreshUI();
                break;
            }
            case EventNewCMDMessage:
                break;
            case EventReadAck: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                refreshUI();
                break;
            }
            case EventOfflineMessage: {
                //a list of offline messages
                //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
                refreshUI();
                break;
            }
            case EventConversationListChanged:
                break;
            case EventMessageChanged:
                break;
            case EventLogout:
                break;
            default:
                break;
        }

    }

    private void onNewMessage(EMMessage message) {
        User user = User.parse(message);
        RobotUser robot = RobotUser.parse(message);
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
        if (message.getFrom().equals(chatUser.getChatName())) {
            if (!chatUser.getMobile().equals(user.getMobile())) {
                chatUser.setMobile(user.getMobile());
            }
            if (!chatUser.getWechat().equals(user.getWechat())) {
                chatUser.setWechat(user.getWechat());
            }
            if (!chatUser.getQq().equals(user.getQq())) {
                chatUser.setQq(user.getQq());
            }
        }
        refreshUIWithNewMessage();
    }

    /***
     * 新消息来时时候　刷新Ｓｋｉｌｌ信息
     *
     * @param message
     */
    private void handlerSkillBanner(final EMMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject skillJson = message.getJSONObjectAttribute("skill");
                    if (skillJson != null) {

                        //避免改变成员变量Skill
                        Skill skill = new Skill();
                        skill.setName(skillJson.getString("name"));
                        skill.setId(skillJson.getLong("sid"));
                        skill.setPrice(java.lang.Float.parseFloat(skillJson.getString("price")));
                        skill.setUnit(skillJson.getString("unit"));
                        skill.setPic(skillJson.getString("pic"));
                        skill.setAvatar(skillJson.getString("avatar"));
                        skill.setContact(skillJson.getString("contact"));
                        skill.setQq(skillJson.getString("qq"));
                        skill.setWeichat(skillJson.getString("weichat"));
                        initView();
                        setUpView();
                        showBannerIfExist(skill);
                    }
                } catch (EaseMobException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void refreshUIWithNewMessage() {
        if (adapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
            }
        });
    }

    private void refreshUI() {
        if (adapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refresh();
            }
        });
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 发送文本消息
     *
     * @param content message content
     */
    public void sendText(String content, boolean sendVcard) {
        if (sendVcard) {
            content = "[名片]";
        }
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            if (isRobot) {
                message.setAttribute("em_robot_message", true);
            }

            if (sendVcard) {
                maimeng.yodian.app.client.android.model.user.User user = maimeng.yodian.app.client.android.model.user.User.read(this);
                boolean weChatIsEmpty = TextUtils.isEmpty(user.getWechat());
                boolean mobileIsEmpty = TextUtils.isEmpty(user.getInfo().getContact());
                boolean qqIsEmpty = TextUtils.isEmpty(user.getInfo().getQq());
                if (weChatIsEmpty && mobileIsEmpty && qqIsEmpty) {
                    maimeng.yodian.app.client.android.view.dialog.AlertDialog.newInstance("提示", "请完善信息").setNegativeListener(new maimeng.yodian.app.client.android.view.dialog.AlertDialog.NegativeListener() {
                        @Override
                        public void onNegativeClick(DialogInterface dialog) {
                            startActivity(new Intent(ChatActivity.this, SettingUserInfo.class));
                        }

                        @Override
                        public String negativeText() {
                            return "确定";
                        }
                    }).show(getFragmentManager(), "dialog");
                    return;
                }
            }
            if (sendVcard) {
                setExtAttribute(message, MESSAGE_NAME_CARD);
                if("hx_admin".equals(chatUser.getChatName())){
                    String weChat="";
                    String qq="";
                    String mobile="";
                    try {
                        weChat = message.getStringAttribute("weChat");
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                    try {
                        qq = message.getStringAttribute("qq");
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                    try {
                        mobile = message.getStringAttribute("mobile");
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }

                    StringBuffer sb=new StringBuffer();

                    if(!"".equals(weChat))sb.append("微信:").append(weChat).append("\n");
                    if(!"".equals(qq))sb.append("QQ:").append(qq).append("\n");
                    if(!"".equals(mobile))sb.append("手机:").append(mobile);
                    content=sb.toString();
                }
            } else {
                setExtAttribute(message, MESSAGE_TEXT);
            }

            final TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);


            // 把messgage加到conversation中
            conversation.addMessage(message);
            onNewMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refreshSelectLast();
            mEditTextContent.setText("");

            setResult(RESULT_OK);

        }
    }

    /**
     * 发送文本消息
     *
     * @param content message content
     */
    public void sendText(String content) {
        sendText(content, false);
    }

    /**
     * 给消息设置附加数据
     *
     * @param message
     */
    private void setExtAttribute(EMMessage message, int messageType) {
        maimeng.yodian.app.client.android.model.user.User currentUser = maimeng.yodian.app.client.android.model.user.User.read(this);
        String nick = currentUser.getNickname();
        String avatar = currentUser.getAvatar();
        String id = currentUser.getUid() + "";
        message.setAttribute("nickName", nick);
        message.setAttribute("avatar", avatar);
        message.setAttribute("uid", id);
        if (messageType == MESSAGE_NAME_CARD) {
            String contact = currentUser.getInfo().getContact();
            String qq = currentUser.getInfo().getQq();
            String wechat = currentUser.getInfo().getWechat();
            contact = TextUtils.isEmpty(contact) ? "" : contact;
            qq = TextUtils.isEmpty(qq) ? "" : qq;
            wechat = TextUtils.isEmpty(wechat) ? "" : wechat;
            if(!TextUtils.isEmpty(contact)) message.setAttribute("mobile", contact);
            if(!TextUtils.isEmpty(qq)) message.setAttribute("qq", qq);
            if(!TextUtils.isEmpty(wechat)) message.setAttribute("weChat", wechat);
        }
        if("hx_admin".equals(chatUser.getChatName())){
            HashMap<String,String> data=new HashMap<>();
            data.put("userNickname",user.getNickname());
            data.put("trueName",user.getNickname());
            data.put("qq",user.getInfo().getQq());
            try {
                message.setAttribute("weichat",new JSONObject().put("visitor",new JSONObject(data)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        message.setAttribute("yd_type", FLAG_PUSH);
        if (messageType == MESSAGE_NAME_CARD) {
            message.setAttribute("em_push_title", "[名片]");//推送类型名片
        } else if (messageType == MESSAGE_TEXT) {
            message.setAttribute("em_push_title", "[文本]");//推送类型文本
        } else if (messageType == MESSAGE_VOICE) {
            message.setAttribute("em_push_title", "[语音]");//推送类型语音
        } else if (messageType == MESSAGE_PIC) {
            message.setAttribute("em_push_title", "[图片]");//推送类型图片
        } else if (messageType == MESSAGE_VIDO) {
            message.setAttribute("em_push_title", "[视频]");//推送类型视频
        }

        if(skill!=null) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(Network.getOne().getGson().toJson(skill));
                message.setAttribute("skill", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            message.setReceipt(toChatUsername);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);
            if (isRobot) {
                message.setAttribute("em_robot_message", true);
            }
            setExtAttribute(message, MESSAGE_VOICE);
            conversation.addMessage(message);
            onNewMessage(message);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
            // send file
            // sendVoiceSub(filePath, fileName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        String to = toChatUsername;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        setExtAttribute(message, MESSAGE_PIC);

        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        if (isRobot) {
            message.setAttribute("em_robot_message", true);
        }
        conversation.addMessage(message);
        onNewMessage(message);
        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
        // more(more);
    }

    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath, final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
            setExtAttribute(message, MESSAGE_VIDO);
            String to = toChatUsername;
            message.setReceipt(to);
            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
            message.addBody(body);
            if (isRobot) {
                message.setAttribute("em_robot_message", true);
            }
            conversation.addMessage(message);
            onNewMessage(message);
            listView.setAdapter(adapter);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 发送位置信息
     *
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        setExtAttribute(message, MESSAGE_LOCATION);
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(toChatUsername);
        if (isRobot) {
            message.setAttribute("em_robot_message", true);
        }
        conversation.addMessage(message);
        onNewMessage(message);

        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);

    }

    /**
     * 发送文件
     *
     * @param uri
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            String st7 = getResources().getString(R.string.File_does_not_exist);
            Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            String st6 = getResources().getString(R.string.The_file_is_not_greater_than_10_m);
            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
        setExtAttribute(message, MESSAGE_FILE);

        message.setReceipt(toChatUsername);
        // add message body
        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
        message.addBody(body);
        if (isRobot) {
            message.setAttribute("em_robot_message", true);
        }
        conversation.addMessage(message);
        onNewMessage(message);
        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
    }

    /**
     * 重发消息
     */
    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;

        adapter.refreshSeekTo(resendPos);
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);

    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
        // {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if(hasFocus){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // }
        // }
        // });
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击清空聊天记录
     */
    public void emptyHistory() {
        String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
        startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
                .putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
    }


    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void toggleMore(View view) {
        if (more.getVisibility() == View.GONE) {
            EMLog.d(TAG, "more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    /**
     * 点击文字输入框
     *
     * @param v
     */
    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
        }

    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

                        if (filename != "delete_expression") { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("maimeng.yodian.app.client.android.chat.utils.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this,
                                    (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {

                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(i, selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete(selectionStart - 1,
                                                    selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;

            reslist.add(filename);

        }
        return reslist;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (group != null)
            ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
        enableVoiceCall = true;
        enableVideoCall = true;
        initEntryData(entries);
        if (adapter != null) {
            adapter.refresh();
        }

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
    }

    @Override
    protected void onStop() {
        // unregister this event listener when this activity enters the
        // background
        EMChatManager.getInstance().unregisterEventListener(this);

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();

        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld())
            wakeLock.release();
        if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
            // 停止语音播放
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }

        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 加入到黑名单
     *
     * @param username
     */
    private void addUserToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.Is_moved_into_blacklist));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        EMChatManager.getInstance().unregisterEventListener(this);
        finish();
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * listview滑动监听listener
     */
    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                /*if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
                    isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					EMMessage firstMsg = conversation.getAllMessages().get(0);
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						if (messages.size() > 0) {
							adapter.refreshSeekTo(messages.size() - 1);
						}

						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}*/
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        ChatUser user = intent.getParcelableExtra("chatUser");
        if (user != null) {
            if (toChatUsername.equals(user.getChatName()))
                super.onNewIntent(intent);
            else {
                finish();
                startActivity(intent);
            }
        }


    }

    /**
     * 转发消息
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                // 获取消息内容，发送消息
                String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
                sendText(content);
                break;
            case IMAGE:
                // 发送图片
                String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // 不存在大图发送缩略图
                        filePath = ImageUtils.getThumbnailImagePath(filePath);
                    }
                    sendPicture(filePath);
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
            EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
        }
    }


    public String getToChatUsername() {
        return toChatUsername;
    }

    public ListView getListView() {
        return listView;
    }

}
