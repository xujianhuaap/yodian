package maimeng.yodian.app.client.android.view.skill;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.SkillTemplateAdapter;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillTemplateResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.viewentry.skill.AddButtonViewEntry;
import maimeng.yodian.app.client.android.viewentry.skill.ItemViewEntry;
import maimeng.yodian.app.client.android.viewentry.skill.ViewEntry;

public class SkillTemplateActivity extends AppCompatActivity implements Callback<SkillTemplateResponse>,AbstractAdapter.ViewHolderClickListener<SkillTemplateAdapter.ViewHolder> {
    private SkillService service;
    private RecyclerView mTemplateList;
    private SkillTemplateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=Network.getService(SkillService.class);
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
        adapter=new SkillTemplateAdapter(this,this);
        mTemplateList=(RecyclerView)findViewById(R.id.template_list);
        mTemplateList.setLayoutManager(new GridLayoutManager(this,2));
        mTemplateList.setAdapter(adapter);
        service.template(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(SkillTemplateResponse res, Response response) {
        if(res.isSuccess()){
            List<SkillTemplate> data = res.getData().getList();
            List<ViewEntry> list=new ArrayList<>(data.size()+1);
            for(SkillTemplate template:data){
                list.add(new ItemViewEntry(template));
            }
            list.add(new AddButtonViewEntry());
            adapter.reload(list, false);
            adapter.notifyDataSetChanged();
        }else{
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this,hNetError);
    }

    @Override
    public void end() {

    }

    @Override
    public void onItemClick(SkillTemplateAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(SkillTemplateAdapter.ViewHolder holder, View clickItem, int postion) {

    }
}
