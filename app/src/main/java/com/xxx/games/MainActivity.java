package com.xxx.games;

import android.os.Bundle;

import com.xxx.games.databinding.ActivityMainBinding;
import com.xxx.games.truthOrDare.TruthOrDareActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Supopo. on 2021/9/10.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.btnGame1.setOnClickListener(lis -> {
            startActivity(TruthOrDareActivity.class);
        });
    }
}
