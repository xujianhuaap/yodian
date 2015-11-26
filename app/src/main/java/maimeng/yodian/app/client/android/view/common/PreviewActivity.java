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

/**
 * Created by xujianhua on 9/22/15.
 */
public class PreviewActivity extends AppCompatActivity {

    private User mUser;
    private ImageView mPreview;


    public static void show(Context context, User user) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_pic);
        mUser = getIntent().getParcelableExtra("user");
        mPreview = (ImageView) findViewById(R.id.preview_pic);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new ImageLoaderManager.Loader(mPreview,
                Uri.parse(mUser.getAvatar())).callback(new ImageCallBackProxy()).start(this);

    }


    private final class ImageCallBackProxy implements ImageLoaderManager.Callback {
        @Override
        public void onLoadFaild() {
            Toast.makeText(PreviewActivity.this, "查看大图失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoadEnd() {

        }

        @Override
        public void onImageLoaded(Bitmap bitmap) {
            mPreview.setImageBitmap(bitmap);
        }
    }


}
