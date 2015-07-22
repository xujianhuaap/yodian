package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import java.util.Date;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ChatSessionListItemBinding;
import maimeng.yodian.app.client.android.model.chat.Session;
import maimeng.yodian.app.client.android.utils.CommonUtils;
import maimeng.yodian.app.client.android.utils.DateUtils;
import maimeng.yodian.app.client.android.utils.SmileUtils;

/**
 * Created by android on 2015/7/22.
 */
public class ChatSessionListAdapter extends AbstractAdapter<Session,ChatSessionListAdapter.ViewHolder>{
    public ChatSessionListAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public ChatSessionListAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ChatSessionListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_session_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatSessionListItemBinding binding=holder.binding;
        Session conversation = getItem(position);
        binding.setSession(conversation);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final ChatSessionListItemBinding binding;

        public ViewHolder(ChatSessionListItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
