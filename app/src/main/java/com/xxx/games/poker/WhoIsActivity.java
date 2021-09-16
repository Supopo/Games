package com.xxx.games.poker;

import android.os.Bundle;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.databinding.ActivityWhoIsBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Supopo. on 2021/9/16.
 * 谁是卧底
 */
public class WhoIsActivity extends BaseActivity<ActivityWhoIsBinding, BaseViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_who_is;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();

        setCameraDistance();
        binding.imageViewBack.setOnClickListener(lis ->{
            cardTurnover();
        });
        binding.rlFront.setOnClickListener(lis ->{
            cardTurnover();
        });
    }

    /**
     * 翻牌
     */
    public void cardTurnover() {
        if (View.VISIBLE == binding.imageViewBack.getVisibility()) {
            ViewHelper.setRotationY(binding.rlFront, 180f);//先翻转180，转回来时就不是反转的了
            Rotatable rotatable = new Rotatable.Builder(binding.rlCardRoot)
                    .sides(R.id.imageView_back, R.id.rl_front)
                    .direction(Rotatable.ROTATE_Y)
                    .rotationCount(1)
                    .build();
            rotatable.setTouchEnable(false);
            rotatable.rotate(Rotatable.ROTATE_Y, -180, 1500);
        } else if (View.VISIBLE == binding.rlFront.getVisibility()) {
            Rotatable rotatable = new Rotatable.Builder(binding.rlCardRoot)
                    .sides(R.id.imageView_back, R.id.rl_front)
                    .direction(Rotatable.ROTATE_Y)
                    .rotationCount(1)
                    .build();
            rotatable.setTouchEnable(false);
            rotatable.rotate(Rotatable.ROTATE_Y, 0, 1500);
        }
    }

    /**
     * 改变视角距离, 贴近屏幕
     */
    private void setCameraDistance() {
        int distance = 10000;
        float scale = getResources().getDisplayMetrics().density * distance;
        binding.rlCardRoot.setCameraDistance(scale);
    }
}
