package maimeng.yodian.app.client.android.view;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import org.parceler.Parcels;

/**
 * Created by android on 15-10-10.
 */
public abstract class BaseFragment extends Fragment {
    public static final int REQUEST_CREATE_SKILL = 0x2403;//新建技能
    public static final int REQUEST_EDIT_SKILL = 0x2404;//编辑技能

    protected <T extends View> T findViewById(@IdRes int id) {
        return (T) getView().findViewById(id);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    protected <T> T get(String key) {
        return Parcels.unwrap(getArguments().getParcelable(key));
    }
}
