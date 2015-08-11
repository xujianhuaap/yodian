package maimeng.yodian.app.client.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Theme;

/**
 * Created by android on 15-8-10.
 */
public class ThemeAdapter extends AbstractListAdapter<Theme> {

    private AdapterClickListener adapterClickListener;

    public void setAdapterClickListener(AdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }

    public ThemeAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.theme_select_item,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.bind(getItem(position));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  final class ViewHolder implements View.OnClickListener{
        private  Theme theme;

        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public void bind(Theme theme){
            this.theme=theme;
            TextView tv=(TextView)convertView.findViewById(R.id.theme_name);
            tv.setText(theme.getName());
            tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            adapterClickListener.onClickListner(v,theme);
        }
    }

    public interface  AdapterClickListener {
        public void onClickListner(View v,Theme theme);

    }
}
