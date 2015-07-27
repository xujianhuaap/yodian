package maimeng.yodian.app.client.android.view.skill;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AppCompatActivity {
    private RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_details);

        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        //通过CollapsingToolbarLayout修改字体颜色
        mList=(RecyclerView)findViewById(R.id.recyclerView);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(new TextView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView)holder.itemView).setText("test"+position);
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
