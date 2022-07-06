package com.xxx.games.call;

import android.os.Bundle;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.databinding.ActivityCallSettingBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class CallSettingActivity extends BaseActivity<ActivityCallSettingBinding, BaseViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_call_setting;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
