package maimeng.yodian.app.client.android.view.skill;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String LOG_TAG = SkillDetailsActivity.class.getName();
//    @Bind(R.id.recyclerView)
//    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_details);
        ButterKnife.bind(this);
//        mList = (RecyclerView) findViewById(R.id.recyclerView);
//        mList.setLayoutManager(new LinearLayoutManager(this));
//        mList.setAdapter(new RecyclerView.Adapter() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                return new ViewHolder(new TextView(parent.getContext()));
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//                ((TextView) holder.itemView).setText("test" + position);
//            }
//
//            @Override
//            public int getItemCount() {
//                return 100;
//            }
//        });
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
