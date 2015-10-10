package maimeng.yodian.app.client.android.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.skill.Theme;

/**
 * Created by android on 15-8-10.
 */
public class ThemeAdapter extends AbstractListAdapter<Theme> {

    private AdapterClickListener mAdapterClickListener;
    private final SharedPreferences mSharedPreferences;

    public void setAdapterClickListener(AdapterClickListener adapterClickListener) {
        this.mAdapterClickListener = adapterClickListener;
    }

    public ThemeAdapter(Context context) {
        super(context);
        this.mSharedPreferences = context.getSharedPreferences("yodian", Context.MODE_PRIVATE);
        mSharedPreferences.edit().putInt("logo",0).commit();
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
        holder.bind(getItem(position),position);
        if(position==mSharedPreferences.getInt("logo",0x89)){
            convertView.findViewById(R.id.white_dot).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  final class ViewHolder implements View.OnClickListener{
        private  Theme mTheme;

        private final View mConvertView;
        private final CheckBox mLogo;
        private int mPosition;

        public ViewHolder(View convertView) {
            this.mConvertView = convertView;
            this.mLogo=(CheckBox)convertView.findViewById(R.id.white_dot);
        }

        public void bind(Theme theme,int position){
            this.mTheme=theme;
            mPosition=position;
            TextView tv=(TextView)mConvertView.findViewById(R.id.theme_name);
            View containter=mConvertView.findViewById(R.id.container);
            tv.setText(theme.getName());
            containter.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSharedPreferences.edit().putInt("logo",mPosition).commit();
            mAdapterClickListener.onClickListner(v,mTheme,mPosition);
        }
    }

    public interface  AdapterClickListener {
        public void onClickListner(View v,Theme theme,int position);

    }
}
