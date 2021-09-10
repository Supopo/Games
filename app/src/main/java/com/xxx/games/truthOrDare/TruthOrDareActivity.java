package com.xxx.games.truthOrDare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.addPlayer.AddPlayerActivity;
import com.xxx.games.addPlayer.TagBean;
import com.xxx.games.databinding.ActivityTruthOrDareBinding;
import com.xxx.games.widget.ITurntableListener;
import com.xxx.games.widget.ShowTruthOrDareDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ACache;

/**
 * 真心话大冒险
 */
public class TruthOrDareActivity extends BaseActivity<ActivityTruthOrDareBinding, BaseViewModel> {

    private TagBean players;
    private ShowTruthOrDareDialog dareDialog;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_truth_or_dare;
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
        setStatusBarTransparent();

        if (players != null && players.getTags() != null) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();
            for (TagBean tag : players.getTags()) {
                names.add(tag.getTag());
                colors.add(tag.getPlaceholderRes());
            }
            binding.turntable.setDatas(names, colors);
        } else {
            initViewData();
        }

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.tvAdd.setOnClickListener(lis -> {
            startActivityForResult(new Intent(TruthOrDareActivity.this, AddPlayerActivity.class), 100);
        });

        binding.ivNode.setOnClickListener(lis -> {
            binding.turntable.startRotate(new ITurntableListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onEnd(int position, String name) {
                    dareDialog = new ShowTruthOrDareDialog(TruthOrDareActivity.this, name);
                    dareDialog.showDialog();
                }
            });
        });
    }

    private void initViewData() {
        int num = 8;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            names.add((i + 1) + "号玩家");
        }
        binding.turntable.setDatas(num, names);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            //重新initViewData
            if (data != null) {
                TagBean tags = (TagBean) data.getSerializableExtra("tags");
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();
                if (tags != null && tags.getTags() != null) {
                    for (TagBean tag : tags.getTags()) {
                        names.add(tag.getTag());
                        colors.add(tag.getPlaceholderRes());
                    }
                }
                binding.turntable.setDatas(names, colors);
            }
        }
    }
}