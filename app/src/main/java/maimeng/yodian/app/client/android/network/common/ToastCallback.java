package maimeng.yodian.app.client.android.network.common;

import android.content.Context;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.ketie.app.client.android.network2.response.ToastResponse;
import maimeng.ketie.app.client.android.network2.utils.ErrorUtils;

public class ToastCallback implements Callback<ToastResponse> {
    private final Context mContext;
    public ToastCallback(Context context){
        this.mContext=context;
    }
    @Override
    public void start() {

    }

    @Override
    public void success(ToastResponse toastResponse, Response response) {
        Toast.makeText(mContext, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(mContext,hNetError);
    }

    @Override
    public void end() {

    }
}
