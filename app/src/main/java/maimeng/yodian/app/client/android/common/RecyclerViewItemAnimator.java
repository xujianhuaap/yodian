package maimeng.yodian.app.client.android.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 15-7-14.
 */
public class RecyclerViewItemAnimator extends DefaultItemAnimator {
    List<RecyclerView.ViewHolder> mAnimationAddViewHolders = new ArrayList<RecyclerView.ViewHolder>();
    List<RecyclerView.ViewHolder> mAnimationRemoveViewHolders = new ArrayList<RecyclerView.ViewHolder>();
    //需要执行动画时会系统会调用，用户无需手动调用
    @Override
    public void runPendingAnimations() {
        if (!mAnimationAddViewHolders.isEmpty()) {

            AnimatorSet animator;
            View target;
            for (final RecyclerView.ViewHolder viewHolder : mAnimationAddViewHolders) {
                target = viewHolder.itemView;
                animator = new AnimatorSet();

                animator.playTogether(
                        ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
                        ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f)
                );

                animator.setTarget(target);
                animator.setDuration(target.getResources().getInteger(R.integer.duration));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mAnimationAddViewHolders.remove(viewHolder);
                        if (!isRunning()) {
                            dispatchAnimationsFinished();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }
        }
        else if(!mAnimationRemoveViewHolders.isEmpty()){
        }
    }
    //remove时系统会调用，返回值表示是否需要执行动画
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        return mAnimationRemoveViewHolders.add(viewHolder);
    }

    //viewholder添加时系统会调用
    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        return mAnimationAddViewHolders.add(viewHolder);
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    public void endAnimations() {
    }

    @Override
    public boolean isRunning() {
        return !(mAnimationAddViewHolders.isEmpty()&&mAnimationRemoveViewHolders.isEmpty());
    }
}
