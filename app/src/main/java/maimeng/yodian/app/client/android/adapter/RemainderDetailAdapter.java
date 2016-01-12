package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ItemRemainderBinding;
import maimeng.yodian.app.client.android.databings.ResourceAdapter;
import maimeng.yodian.app.client.android.model.BalanceInfo;
import maimeng.yodian.app.client.android.model.OrderInfo;

/**
 * Created by xujianhua on 05/01/16.
 */
public class RemainderDetailAdapter extends AbstractAdapter<BalanceInfo,RemainderDetailAdapter.ViewHolder>{

    public RemainderDetailAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        ItemRemainderBinding viewDataBinding=DataBindingUtil.inflate(inflater, R.layout.item_remainder, parent, false);
        View root=viewDataBinding.getRoot();
        ViewGroup.LayoutParams layoutParams=root.getLayoutParams();
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        root.setLayoutParams(layoutParams);
        return  new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public final class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemRemainderBinding viewDataBinding;
        public ViewHolder(ItemRemainderBinding dataBinding) {
            super(dataBinding.getRoot());
            this.viewDataBinding=dataBinding;

        }
        public void bind(BalanceInfo orderInfo){
            viewDataBinding.setOrderinfo(orderInfo);
            ResourceAdapter.text(viewDataBinding.time,orderInfo.getCreatetime(),"MM/dd\t\tHH:mm","yyyy/MM/dd\t\tHH:mm");
            viewDataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewHolderClickListener.onItemClick(ViewHolder.this, getLayoutPosition());
                }
            });
        }

    }

}
