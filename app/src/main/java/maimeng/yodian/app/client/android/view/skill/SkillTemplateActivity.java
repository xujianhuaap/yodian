package maimeng.yodian.app.client.android.view.skill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.j256.ormlite.dao.Dao;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.SkillTemplateAdapter;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.db.SQLiteHelper;
import maimeng.yodian.app.client.android.entry.skilltemplate.AddButtonViewEntry;
import maimeng.yodian.app.client.android.entry.skilltemplate.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skilltemplate.ViewEntry;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillTemplateResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.BaseFragment;

public class SkillTemplateActivity extends AbstractActivity implements Callback<SkillTemplateResponse>, AbstractAdapter.ViewHolderClickListener<SkillTemplateAdapter.ViewHolder> {
    private SkillService service;
    private RecyclerView mTemplateList;
    private SkillTemplateAdapter adapter;
    private View mStarCircle;

    /***
     * @param activity
     * @param requestCode
     * @param pairs
     */
    public static void show(Activity activity, int requestCode, Pair<View, String>... pairs) {
        Intent intent = new Intent(activity, SkillTemplateActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = Network.getService(SkillService.class);
        setContentView(R.layout.activity_skill_template, false);
        View floatbutton = findViewById(R.id.btn_back);
        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SkillTemplateActivity.this);
            }
        });
        adapter = new SkillTemplateAdapter(this, this);
        mTemplateList = (RecyclerView) findViewById(R.id.template_list);
        mTemplateList.setLayoutManager(new LinearLayoutManager(this));
        mTemplateList.setAdapter(adapter);
        try {
            Dao<SkillTemplate, Integer> dao = SQLiteHelper.getHelper(this).getDao();
            List<SkillTemplate> data = dao.queryForAll();
            List<ViewEntry> list = new ArrayList<>(data.size() + 1);
            for (SkillTemplate template : data) {
                list.add(new ItemViewEntry(template));
            }
            list.add(new AddButtonViewEntry());
            adapter.reload(list, false);
            adapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        mStarCircle = findViewById(R.id.btn_star);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotation_clockwise);
        mStarCircle.setAnimation(animation);
        animation.startNow();

        service.template(this);
        MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(SkillTemplateResponse res, Response response) {
        if (res.isSuccess()) {
            Dao<SkillTemplate, Integer> dao = null;
            try {
                dao = SQLiteHelper.getHelper(this).getDao();
                dao.delete(dao.deleteBuilder().prepare());
                List<SkillTemplate> data = res.getData().getList();
                int size = Math.min(data.size(), 5);
                List<ViewEntry> list = new ArrayList<>(size + 1);
                for (int i = 0; i < size; i++) {
                    SkillTemplate template = data.get(i);
                    list.add(new ItemViewEntry(template));
                    if (dao != null) {
                        dao.create(template);
                    }
                }
                list.add(new AddButtonViewEntry());
                adapter.reload(list, false);
                adapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void end() {

    }

    @Override
    public void onItemClick(SkillTemplateAdapter.ViewHolder holder, int postion) {
        switch (postion) {
            case 0:
                MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL_FIRST);
                break;
            case 1:
                MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL_SECOND);
                break;
            case 2:
                MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL_THIRD);
                break;
            case 3:
                MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL_FOURTH);
                break;
            case 4:
                MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL_FIFTH);
                break;
            case 5:
                MobclickAgent.onEvent(this, UEvent.TEMPLATE_SKILL_DEFINE);
                break;

        }
        if (holder.getItemViewType() == ViewEntry.VIEW_TYPE_ITEM) {
            SkillTemplateAdapter.ItemViewHolder itemHolder = (SkillTemplateAdapter.ItemViewHolder) holder;
            SkillTemplate template = itemHolder.getTemplate();
            CreateOrEditSkillActivity.show(this, BaseFragment.REQUEST_CREATE_SKILL, template);
        } else {
            CreateOrEditSkillActivity.show(this, BaseFragment.REQUEST_EDIT_SKILL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            data.setClass(this, SkillDetailsActivity.class);
            startActivity(data);
            finish();
        }
    }

    @Override
    public void onClick(SkillTemplateAdapter.ViewHolder holder, View clickItem, int postion) {

    }
}
