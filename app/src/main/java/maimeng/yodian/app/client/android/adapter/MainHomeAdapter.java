package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.widget.RoundImageView;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeAdapter extends AbstractAdapter<Skill,MainHomeAdapter.ViewHolder>{
    public MainHomeAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public MainHomeAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public MainHomeAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Skill item = getItem(position);
        holder.mUserNickname.setText(item.getNickname());
        Network.image(item.getAvatar(), holder.mUserAvatar);
        Network.image(item.getPic(),holder.mImg);
        holder.mUserAvatar.bringToFront();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mUserNickname;
        public final RoundImageView mUserAvatar;
        public final ImageView mImg;
        public ViewHolder(View itemView) {
            super(itemView);
            this.mUserNickname=(TextView)itemView.findViewById(R.id.user_nickname);
            this.mUserAvatar=(RoundImageView)itemView.findViewById(R.id.user_avatar);
            this.mImg=(ImageView)itemView.findViewById(R.id.img);
        }
    }
}

