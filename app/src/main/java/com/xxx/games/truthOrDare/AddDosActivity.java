package com.xxx.games.truthOrDare;

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
import com.xxx.games.addPlayer.TagBean;
import com.xxx.games.databinding.ActivityAddDosBinding;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Supopo. on 2021/9/10.
 * 添加大冒险
 */
public class AddDosActivity extends BaseActivity<ActivityAddDosBinding, BaseViewModel> {

    private TagsAdapter tagsAdapter;
    private List<TagBean> dos;
    private TagBean dosCache;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_add_dos;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        //获取本地存储的数据
        dosCache = mmkv.decodeParcelable("dos", TagBean.class);
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
            dos.add(new TagBean(binding.etTag.getText().toString()));
            tagsAdapter.addData(new TagBean(binding.etTag.getText().toString()));
            binding.etTag.setText("");
        });

        binding.tvSave.setOnClickListener(lis -> {
            showDialog("保存中...");

            Intent intent = getIntent();
            TagBean tagBean = new TagBean();
            tagBean.setQuado(tagsAdapter.getData());
            intent.putExtra("tags", tagBean);
            //储存到本地
            mmkv.encode("dos", tagBean);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    //添加真心话
                    setResult(103, intent);
                    finish();
                }
            }, 300);

        });
    }

    private void initAdapter() {
        tagsAdapter = new TagsAdapter(R.layout.item_tags);
        binding.rvNames.setAdapter(tagsAdapter);
        binding.rvNames.setLayoutManager(new LinearLayoutManager(this));

        dos = new ArrayList<>();
        if (dosCache != null && dosCache.getQuado() != null) {
            dos.addAll(dosCache.getQuado());
        }
        tagsAdapter.setList(dos);

        tagsAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                tagsAdapter.remove(position);
                return false;
            }
        });
    }
}
