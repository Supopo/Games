package com.xxx.games.addPlayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.databinding.ActivityAddPlayerBinding;
import com.xxx.games.truthOrDare.TagsAdapter;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Supopo. on 2021/9/10.
 * 添加玩家
 */
public class AddPlayerActivity extends BaseActivity<ActivityAddPlayerBinding, BaseViewModel> {

    private TagsAdapter tagsAdapter;
    private List<TagBean> names;
    private TagBean players;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_add_player;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        //获取本地存储的数据
        players = mmkv.decodeParcelable("players", TagBean.class);
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();


        showDialog("加载中...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initAdapter();
                dismissDialog();
            }
        }, 300);

        binding.tvBack.setOnClickListener(lis -> {
            finish();
        });
        binding.tvClear.setOnClickListener(lis -> {
            List<TagBean> temp = new ArrayList<>();
            tagsAdapter.setList(temp);
        });
        binding.btnAdd.setOnClickListener(lis -> {
            if (TextUtils.isEmpty(binding.etTag.getText().toString().trim())) {
                return;
            }
            names.add(new TagBean(binding.etTag.getText().toString()));
            tagsAdapter.addData(new TagBean(binding.etTag.getText().toString()));
            binding.etTag.setText("");
        });

        binding.tvSave.setOnClickListener(lis -> {
            showDialog("保存中...");

            Intent intent = getIntent();
            TagBean tagBean = new TagBean();
            tagBean.setTags(tagsAdapter.getData());
            intent.putExtra("tags", tagBean);
            //储存到本地
            mmkv.encode("players", tagBean);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    //添加真心话
                    setResult(102, intent);
                    finish();
                }
            }, 300);
            setResult(101, intent);
            finish();
        });
    }

    private void initAdapter() {
        tagsAdapter = new TagsAdapter(R.layout.item_tags);
        binding.rvNames.setAdapter(tagsAdapter);
        binding.rvNames.setLayoutManager(new LinearLayoutManager(this));

        if (players != null && players.getTags() != null) {
            names = players.getTags();
        } else {
            names = new ArrayList<>();
        }
        tagsAdapter.setList(names);


        tagsAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                tagsAdapter.remove(position);
                return false;
            }
        });
    }
}
