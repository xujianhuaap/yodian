package maimeng.yodian.app.client.android.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;

/**
 * Created by xujianhua on 10/14/15.
 */
public class ViewDialog {



    public static final class Builder{
        private TextView tvTitle;
        private TextView tvMessage;
        private Button btnPositive;
        private Button btnNegtive;
        private Context context;

        private String title;
        private String message;
        private String btnPositiveName;
        private String btnNegtiveName;

        private IPositiveListener positiveListener;
        private INegativeListener negtiveListener;
        private AlertDialog.Builder builder;
        private View view;

        private boolean isPositive;
        private boolean isNegtive;

        public Builder(Context context) {
            this.context = context;
            builder= new AlertDialog.Builder(context);
            view= View.inflate(context, R.layout.alert_dialog,null);
            tvTitle=(TextView)view.findViewById(R.id.title);
            tvMessage=(TextView)view.findViewById(R.id.alert_message);
            btnPositive=(Button)view.findViewById(R.id.btn_positive);
            btnNegtive=(Button)view.findViewById(R.id.btn_cancel);
            builder.setView(view);
        }

        public Builder setTitle(String title){
            this.title=title;
            return this;
        }

        public Builder setMesage(String title){
            this.message=title;
            return this;
        }
        public Builder setPositiveListener(IPositiveListener listener,String btnName){
            isPositive=true;
            this.btnPositiveName=btnName;
            if(this.positiveListener==null){
                this.positiveListener=listener;
            }

            return this;
        }

        public Builder setNegtiveListener(INegativeListener listener,String btnName){
            isNegtive =true;
            this.btnNegtiveName=btnName;
            if(this.negtiveListener==null){
                this.negtiveListener=listener;
            }
            return this;
        }

        public AlertDialog create(){
            final AlertDialog alertDialog=builder.create();

            if(!TextUtils.isEmpty(title)){
                tvTitle.setText(title);
            }
            if(!TextUtils.isEmpty(message)){
                tvMessage.setText(message);
            }
            if(positiveListener!=null){


                if(TextUtils.isEmpty(btnPositiveName)){
                    btnPositiveName="确定";
                }
                btnPositive.setText(btnPositiveName);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveListener.positiveClick();
                        alertDialog.dismiss();
                    }
                });


            }

            if(negtiveListener!=null){

                btnNegtive.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(btnNegtiveName)){
                    btnNegtiveName="取消";
                }
                btnNegtive.setText(btnNegtiveName);
                btnNegtive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negtiveListener.negtiveClick();
                        alertDialog.dismiss();
                    }
                });

            }


            return alertDialog;
        }
    }
    /***
     * 事件监听
     */
    public interface IPositiveListener{
        void positiveClick();
    }

    public interface INegativeListener{
        void negtiveClick();
    }
}
