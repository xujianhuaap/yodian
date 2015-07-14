package maimeng.yodian.app.client.android.view.proxy;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import maimeng.yodian.app.client.android.MainActivity;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.MainHomeAdapter;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeProxy implements ActivityProxy,AbstractAdapter.ViewHolderClickListener<MainHomeAdapter.ViewHolder>{
    private final View mView;
    private final MainActivity mActivity;
    private final RecyclerView mRecyclerView;
    private boolean inited=false;
    private final MainHomeAdapter adapter;
    public MainHomeProxy(MainActivity activity, View view){
        this.mView=view;
        this.mActivity=activity;
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter=new MainHomeAdapter(mActivity,this);
        mRecyclerView.setAdapter(adapter);
    }
    public boolean isInited(){
        return this.inited;
    }
    @Override
    public void init() {
        inited=true;
        ArrayList<String> datas=new ArrayList<>();
        for(int i=0;i<=100;i++){
            datas.add("texttext"+i);
        }
        adapter.reload(datas,false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {
    }

    @Override
    public void show(FloatingActionButton button) {

    }

    @Override
    public void hide(FloatingActionButton button) {

    }

    @Override
    public boolean isShow() {
        return false;
    }

    @Override
    public void onItemClick(MainHomeAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(MainHomeAdapter.ViewHolder holder, View clickItem, int postion) {

    }
}
