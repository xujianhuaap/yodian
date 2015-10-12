package maimeng.yodian.app.client.android.view;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by android on 15-10-10.
 */
public abstract class BaseFragment extends Fragment {
    protected <T extends View> T findViewById(@IdRes int id) {
        return (T) getView().findViewById(id);
    }
}
