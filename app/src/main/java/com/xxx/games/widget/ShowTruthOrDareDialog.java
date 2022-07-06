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
import com.xxx.games.addPlayer.TagBean;

import java.util.List;
import java.util.Random;

/**
 * Created by Supopo. on 2021/9/10.
 */
public class ShowTruthOrDareDialog {
    private Dialog centerDialog;
    private TextView tvLeft, tvRight, tvContent, tvTip, tvPass, tvTitle, tvCenter;
    private String name;
    private Context context;
    private List<TagBean> quas;
    private List<TagBean> dos;
    private View.OnClickListener onClickListener;
    private int type;//1-真心话 2-大冒险
    private int num;

    public ShowTruthOrDareDialog(Context context) {
        this.context = context;
        init(context);
    }

    public ShowTruthOrDareDialog(Context context, String name, List<TagBean> quas, List<TagBean> dos) {
        this.context = context;
        this.name = name;
        this.quas = quas;
        this.dos = dos;
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
        tvTitle = view.findViewById(R.id.tv_title);
        tvTip = view.findViewById(R.id.tv_tip);
        tvPass = view.findViewById(R.id.tv_pass);
        tvCenter = view.findViewById(R.id.tv_center);

        tvContent.setText(name);

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvLeft.getText().toString().equals("拒绝")) {
                    tvTitle.setText("拒绝游戏");
                    tvPass.setVisibility(View.INVISIBLE);
                    tvTip.setText("自己的选择，跪着也要完成！");
                    tvContent.setText("喝酒酒吧");
                    tvContent.setVisibility(View.VISIBLE);
                    tvLeft.setVisibility(View.GONE);
                    tvRight.setVisibility(View.GONE);
                    tvCenter.setVisibility(View.VISIBLE);
                } else if (tvLeft.getText().toString().equals("大冒险")) {
                    type = 2;
                    tvTitle.setText(tvLeft.getText().toString());
                    selectOr();
                }
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvRight.getText().toString().equals("同意")) {
                    //关闭Dialog
                    dismissDialog();
                } else if (tvRight.getText().toString().equals("真心话")) {
                    type = 1;
                    tvTitle.setText(tvRight.getText().toString());
                    selectOr();
                }
            }
        });

        tvCenter.setOnClickListener(lis -> {
            dismissDialog();
        });

        tvPass.setOnClickListener(lis -> {
            if (tvPass.getText().toString().contains("换一题")) {
                //随机刷题
                tvTip.setText(type == 1 ? getQua() : getDo());
                num++;
                tvPass.setText("换一题 " + num);
            }

        });
    }

    private void selectOr() {
        tvContent.setVisibility(View.INVISIBLE);
        tvPass.setVisibility(View.VISIBLE);
        tvLeft.setText("拒绝");
        tvRight.setText("同意");
        tvPass.setText("换一题");
        //随机刷出一道大冒险题
        tvTip.setText(type == 1 ? getQua() : getDo());
    }


    public int getRandom(int num) {
        Random random = new Random();
        int s = random.nextInt(num);
        return s;
    }

    public String getQua() {
        if (quas == null || quas.size() == 0) {
            return "";
        }
        int temp = getRandom(quas.size());
        return quas.get(temp).getTag();
    }

    public String getDo() {
        if (dos == null || dos.size() == 0) {
            return "";
        }
        int temp = getRandom(dos.size());
        return dos.get(temp).getTag();
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
