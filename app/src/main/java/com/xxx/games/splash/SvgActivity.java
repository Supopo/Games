package com.xxx.games.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xxx.games.BR;
import com.xxx.games.MainActivity;
import com.xxx.games.R;
import com.xxx.games.databinding.ActivitySvgBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Supopo. on 2021/9/14.
 * 皮卡丘svg欢迎页页
 */
public class SvgActivity extends BaseActivity<ActivitySvgBinding, BaseViewModel> {

    private Handler mHandler;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_svg;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        setSvg(ModelSVG.values()[4]);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.class);
            }
        }, 2000);
    }

    private void setSvg(ModelSVG modelSvg) {
        binding.svgView.setGlyphStrings(modelSvg.glyphs);
        binding.svgView.setFillColors(modelSvg.colors);
        binding.svgView.setViewportSize(modelSvg.width, modelSvg.height);
        binding.svgView.setTraceResidueColor(0x32000000);
        binding.svgView.setTraceColors(modelSvg.colors);
        binding.svgView.rebuildGlyphData();
        binding.svgView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
