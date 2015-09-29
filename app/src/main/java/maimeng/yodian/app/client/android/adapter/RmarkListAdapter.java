package maimeng.yodian.app.client.android.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.RmarkListItemBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.user.User;

/**
 * Created by android on 2015/7/28.
 */
public class RmarkListAdapter extends AbstractListAdapter<Rmark> {
    private final User me;
    private final ActionListener listener;

    public RmarkListAdapter(Context context, ActionListener listener) {
        super(context);
        me = User.read(context);
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            RmarkListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rmark_list_item, parent, false);
            holder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(getItem(position), position);
        return convertView;
    }

    public class ViewHolder implements View.OnClickListener {
        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        private int position;

        public RmarkListItemBinding getBinding() {
            return binding;
        }

        private final RmarkListItemBinding binding;
        private final PropertyValuesHolder alpha2;
        private final PropertyValuesHolder translation2;
        private final PropertyValuesHolder translation;
        private final PropertyValuesHolder alpha;
        ObjectAnimator open;
        ObjectAnimator close;
        boolean opened = false;

        public ViewHolder(RmarkListItemBinding binding) {
            this.binding = binding;
            translation = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -200f);

            alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.8f, 1.0f);


            translation2 = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -200f, 0f);
            alpha2 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);

            binding.btnMenuMore.setOnClickListener(this);
            binding.btnMenuDelete.setOnClickListener(this);
            binding.btnMenuReport.setOnClickListener(this);
        }

        public void bind(Rmark rmark, int position) {
            this.binding.setRmark(rmark);
            this.position = position;
            reset();
            if (rmark.getUid() == me.getUid()) {
                open = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuDelete, translation, alpha);
                open.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));


                close = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuDelete, translation2, alpha2);
                close.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));

            } else {
                open = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuReport, translation, alpha);
                open.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));

                close = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuReport, translation2, alpha2);
                close.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));

            }


        }

        public void reset() {
            opened = false;
            binding.btnMenuDelete.setTranslationX(0f);
            binding.btnMenuDelete.setAlpha(0f);
            binding.btnMenuReport.setTranslationX(0f);
            binding.btnMenuReport.setAlpha(0f);
        }

        @Override
        public void onClick(View v) {
            if (v == binding.btnMenuMore) {
                if (opened) {
                    opened = false;
                    close.start();
                } else {
                    opened = true;
                    open.start();
                }
            } else if (v == binding.btnMenuDelete) {
                listener.onDelete(this);
                if (opened) {
                    opened = false;
                    close.start();
                } else {
                    opened = true;
                    open.start();
                }
            } else if (v == binding.btnMenuReport) {
                listener.onReport(this);
                if (opened) {
                    opened = false;
                    close.start();
                } else {
                    opened = true;
                    open.start();
                }
            }
        }
    }

    public interface ActionListener {
        void onDelete(ViewHolder holder);

        void onReport(ViewHolder holder);
    }
}
