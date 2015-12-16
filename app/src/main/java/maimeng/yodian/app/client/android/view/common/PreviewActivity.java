package maimeng.yodian.app.client.android.view.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.widget.YDView;

/**
 * Created by xujianhua on 9/22/15.
 */
public class PreviewActivity extends AbstractActivity {

    private User mUser;
    private YDView mPreview;


    public static void show(Context context, User user) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_pic, false);
        mUser = get("user");
        mPreview = (YDView) findViewById(R.id.preview_pic);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPreview.setImageURI(Uri.parse(mUser.getAvatar()));

    }

}
