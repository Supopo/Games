package com.xxx.games.call;

import android.os.Bundle;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.databinding.ActivityCallBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class CallActivity extends BaseActivity<ActivityCallBinding, BaseViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_call;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.marqueeView.setText("求加微信~");
    }
}
