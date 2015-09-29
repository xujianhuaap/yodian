package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ItemOrderListBinding;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.skill.UpperSkill;

/**
 * Created by xujianhua on 9/29/15.
 */
public class OrderListAdapter extends AbstractAdapter<OrderInfo,OrderListAdapter.ViewHolder>{
    public OrderListAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public OrderListAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public OrderListAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }
    @Override
    public void delete(int position) {
        super.delete(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderInfo orderInfo=getItem(position);
        UpperSkill skill=orderInfo.getSkill();
        holder.mBinding.setOrder(orderInfo);
        holder.mBinding.setSkill(skill);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(mContext, R.layout.item_order_list,null);
        ItemOrderListBinding listBinding=DataBindingUtil.bind(view);
        return new ViewHolder(listBinding);
    }


    /***
     *
     */

    public final class ViewHolder extends RecyclerView.ViewHolder{
        private OrderInfo mOrder;
        private final ItemOrderListBinding mBinding;

        public ViewHolder(ItemOrderListBinding listBinding) {
            super(listBinding.getRoot());
            mBinding=listBinding;
        }

        public void bind(OrderInfo orderInfo){
            mOrder=orderInfo;
        }
    }
}
