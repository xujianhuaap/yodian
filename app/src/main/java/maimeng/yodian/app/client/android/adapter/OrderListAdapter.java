package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Date;
import java.text.SimpleDateFormat;

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
        holder.bind(orderInfo);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(mContext, R.layout.item_order_list,null);
        ViewGroup.LayoutParams layoutParams=view.getLayoutParams();
        if(layoutParams==null){
            layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);
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
            String status=mOrder.getStatus();
            if(!TextUtils.isEmpty(status)){
                String statusStr=null;
                switch (Integer.parseInt(status)){
                    case 0:
                        statusStr=mContext.getString(R.string.order_status_unpay);
                        break;
                    case 1:
                        statusStr=mContext.getString(R.string.order_status_delete);
                        break;
                    case 2:
                        statusStr=mContext.getString(R.string.order_status_payed);
                        break;
                    case 3:
                        statusStr=mContext.getString(R.string.order_status_accept);
                        break;
                    case 4:
                        statusStr=mContext.getString(R.string.order_status_send_goods);
                        break;
                    case 5:
                        statusStr=mContext.getString(R.string.order_status_confirm_deal);
                        break;
                    default:
                        statusStr=null;
                        break;
                }
                UpperSkill skill=mOrder.getSkill();

                mBinding.orderStatus.setText(statusStr);
                Spanned html=Html.fromHtml(mContext.getString(R.string.order_total_fee));
                mBinding.orderPrice.setText(Html.fromHtml(mContext.getString(R.string.order_total_fee,
                        mOrder.getTotal_fee())));
                mBinding.skillPrice.setText(Html.fromHtml(mContext.getString(R.string.lable_price,
                        skill.getPrice(), skill.getUnit())));
                SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月 HH:mm");
                String dateStr= format.format(new java.util.Date(Long.parseLong(mOrder.getCreatetime())*1000));;
                mBinding.orderTime.setText(dateStr);
            }

        }
    }
}
