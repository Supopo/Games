package com.xxx.games.randomTeam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.addPlayer.AddPlayerActivity;
import com.xxx.games.addPlayer.TagBean;
import com.xxx.games.databinding.ActivityRandomBinding;
import com.xxx.games.truthOrDare.TruthOrDareActivity;
import com.xxx.games.utils.AutoLineLayoutManager;
import com.xxx.games.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ACache;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 随机分队
 */
public class RandomActivity extends BaseActivity<ActivityRandomBinding, BaseViewModel> {

    private NamesAdapter namesAdapter;
    private NamesAdapter team1Adapter;
    private NamesAdapter team2Adapter;
    private TagBean players;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_random;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initParam() {
        super.initParam();
        //获取本地存储的数据
        players = (TagBean) ACache.get(this).getAsObject("players");
    }

    @Override
    public void initData() {
        super.initData();
        //初始化所有人
        initNames(players.getTags());

        team1Adapter = new NamesAdapter(R.layout.item_name);
        team2Adapter = new NamesAdapter(R.layout.item_name);

        binding.tvBack.setOnClickListener(lis -> {
            finish();
        });
        binding.tvMenu3.setOnClickListener(lis -> {
            startActivityForResult(new Intent(RandomActivity.this, AddPlayerActivity.class), 100);
        });
        binding.tvMenu1.setOnClickListener(lis -> {
            if (namesAdapter == null) {
                ToastUtils.showShort("请先添加玩家");
                return;
            }
            if (this.namesAdapter.getData().size() < 2) {
                return;
            }
            //分队
            showDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                }
            }, 500);
            //从namesAdapter得数据里随机取分2队
            List<TagBean> allNames = new ArrayList<>(this.namesAdapter.getData());
            int i = allNames.size() / 2;

            //队伍1
            List<TagBean> team1 = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                int r = BaseUtils.getRandom(allNames.size());
                team1.add(allNames.get(r));
                allNames.remove(r);
            }
            //刷新队伍1
            binding.tvTitle2.setVisibility(View.VISIBLE);
            binding.rvTeam1.setVisibility(View.VISIBLE);
            team1Adapter.setNewInstance(team1);
            binding.rvTeam1.setLayoutManager(new AutoLineLayoutManager());
            binding.rvTeam1.setAdapter(team1Adapter);
            //刷新队伍2
            binding.tvTitle3.setVisibility(View.VISIBLE);
            binding.rvTeam2.setVisibility(View.VISIBLE);
            team2Adapter.setNewInstance(allNames);
            binding.rvTeam2.setLayoutManager(new AutoLineLayoutManager());
            binding.rvTeam2.setAdapter(team2Adapter);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            //重新initViewData
            if (data != null) {
                TagBean tags = (TagBean) data.getSerializableExtra("tags");
                initNames(tags.getTags());
            }
        }
    }

    private void initNames(List names) {
        //刷新rvName
        binding.tvTitle1.setVisibility(View.VISIBLE);
        binding.rvNames.setVisibility(View.VISIBLE);
        namesAdapter = new NamesAdapter(R.layout.item_name);
        binding.rvNames.setLayoutManager(new AutoLineLayoutManager());
        namesAdapter.setNewInstance(names);
        binding.rvNames.setAdapter(namesAdapter);
    }
}