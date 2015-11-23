package maimeng.yodian.app.client.android.view.dialog;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.lang.reflect.Field;

/**
 * Created by apple on 15/11/23.
 */
public class DialogFragment extends android.app.DialogFragment {

    @Override
    public final int show(FragmentTransaction transaction, String tag) {
        try {
            Field mDismissed = android.app.DialogFragment.class.getDeclaredField("mDismissed");
            Field mShownByMe = android.app.DialogFragment.class.getDeclaredField("mShownByMe");
            Field mViewDestroyed = android.app.DialogFragment.class.getDeclaredField("mViewDestroyed");
            Field mBackStackId = android.app.DialogFragment.class.getDeclaredField("mBackStackId");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mViewDestroyed.setAccessible(true);
            mBackStackId.setAccessible(true);

            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
            transaction.add(this, tag);
            mViewDestroyed.setBoolean(this, false);
            int backid = transaction.commitAllowingStateLoss();
            mBackStackId.setInt(this, backid);
            return backid;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return super.show(transaction, tag);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return super.show(transaction, tag);
        }
    }

    @Override
    public final void show(FragmentManager manager, String tag) {
        show(manager.beginTransaction(), tag);

    }
}
