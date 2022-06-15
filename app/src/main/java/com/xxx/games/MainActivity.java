package com.xxx.games;

import android.os.Bundle;

import com.xxx.games.angryUncle.AngryUncleActivity;
import com.xxx.games.call.CallActivity;
import com.xxx.games.databinding.ActivityMainBinding;
import com.xxx.games.mahjong.MahjongActivity;
import com.xxx.games.parks.ParksActivity;
import com.xxx.games.poker.WhoIsActivity;
import com.xxx.games.randomHero.RandomHeroActivity;
import com.xxx.games.randomTeam.RandomActivity;
import com.xxx.games.truthOrDare.TruthOrDareActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Supopo. on 2021/9/10.a
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
        binding.btnGame2.setOnClickListener(lis -> {
            startActivity(AngryUncleActivity.class);
        });
        binding.btnGame3.setOnClickListener(lis -> {
            startActivity(WhoIsActivity.class);
        });
        binding.btnGame4.setOnClickListener(lis -> {
            startActivity(ParksActivity.class);
        });
        binding.btnGame5.setOnClickListener(lis -> {
            startActivity(MahjongActivity.class);
        });
        binding.btnGame6.setOnClickListener(lis -> {
            startActivity(RandomActivity.class);
        });
        binding.btnGame7.setOnClickListener(lis -> {
//            startActivity(RandomHeroActivity.class);
            startActivity(CallActivity.class);
        });
    }
}
