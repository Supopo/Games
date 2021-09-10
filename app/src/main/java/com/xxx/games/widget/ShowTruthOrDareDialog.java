package com.xxx.games.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xxx.games.R;

/**
 * Created by Supopo. on 2021/9/10.
 */
public class ShowTruthOrDareDialog {
    private Dialog centerDialog;
    private TextView tvLeft, tvRight, tvContent;
    private String name;
    private Context context;
    private View.OnClickListener onClickListener;

    public ShowTruthOrDareDialog(Context context) {
        this.context = context;
        init(context);
    }

    public ShowTruthOrDareDialog(Context context, String name) {
        this.context = context;
        this.name = name;
        init(context);
    }

    public void init(Context context) {
        centerDialog = new Dialog(context, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_truth_or_dare, null);
        //点击外部不可dismiss
        centerDialog.setCancelable(false);
        //将布局设置给Dialog
        centerDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = centerDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //设置弹出动画
        //        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);


        //初始化控件
        tvLeft = view.findViewById(R.id.tv_left);
        tvRight = view.findViewById(R.id.tv_right);
        tvContent = view.findViewById(R.id.tv_content);

        tvContent.setText(name);

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷题
            }
        });


    }


    public void setRightBtnClickListener(View.OnClickListener onClickListener) {
        tvRight.setOnClickListener(onClickListener);
    }

    public void setLeftBtnClickListener(View.OnClickListener onClickListener) {
        tvLeft.setOnClickListener(onClickListener);
    }

    public void showDialog() {
        if (centerDialog != null && centerDialog.isShowing()) {
            return;
        }
        centerDialog.show();

    }

    public void dismissDialog() {
        if (centerDialog != null) {
            centerDialog.dismiss();
        }

    }
}
