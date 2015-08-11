package maimeng.yodian.app.client.android.view.skill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.j256.ormlite.dao.Dao;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.adapter.SkillTemplateAdapter;
import maimeng.yodian.app.client.android.db.SQLiteHelper;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillTemplateResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.skill.proxy.ActivityProxyController;
import maimeng.yodian.app.client.android.viewentry.skill.AddButtonViewEntry;
import maimeng.yodian.app.client.android.viewentry.skill.ItemViewEntry;
import maimeng.yodian.app.client.android.viewentry.skill.ViewEntry;

public class SkillTemplateActivity extends AppCompatActivity implements Callback<SkillTemplateResponse>, AbstractAdapter.ViewHolderClickListener<SkillTemplateAdapter.ViewHolder> {
    private SkillService service;
    private RecyclerView mTemplateList;
    private SkillTemplateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = Network.getService(SkillService.class);
        setContentView(R.layout.activity_skill_template);
        ViewCompat.setTransitionName(findViewById(R.id.top), "top");
        View floatbutton = findViewById(R.id.btn_back);
        ViewCompat.setTransitionName(floatbutton, "floatbutton");
        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SkillTemplateActivity.this);
            }
        });
        adapter = new SkillTemplateAdapter(this, this);
        mTemplateList = (RecyclerView) findViewById(R.id.template_list);
        mTemplateList.setLayoutManager(new GridLayoutManager(this, 2));
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
        service.template(this);
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
        Intent intent = new Intent(this, CreateOrEditSkillActivity.class);
        if (holder.getItemViewType() == ViewEntry.VIEW_TYPE_ITEM) {
            SkillTemplateAdapter.ItemViewHolder itemHolder = (SkillTemplateAdapter.ItemViewHolder) holder;
            SkillTemplate template = itemHolder.getTemplate();
            Pair<View, String> img = Pair.create((View) itemHolder.binding.skillImg, "avatar");
            Pair<View, String> title = Pair.create((View) itemHolder.binding.skillName, "title");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, img, title);
            intent.putExtra("template", template);
            ActivityCompat.startActivityForResult(this, intent, ActivityProxyController.REQUEST_CREATE_SKILL, options.toBundle());
        } else {
            startActivityForResult(intent, ActivityProxyController.REQUEST_CREATE_SKILL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityProxyController.REQUEST_CREATE_SKILL) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void onClick(SkillTemplateAdapter.ViewHolder holder, View clickItem, int postion) {

    }
}
