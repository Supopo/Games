package com.xxx.games.angryUncle;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.databinding.ActivityAngryUncleBinding;
import com.xxx.games.utils.BaseUtils;
import com.xxx.games.widget.ShowAngryPkqDialog;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Supopo. on 2021/9/15.
 * 愤怒的大叔
 */
public class AngryUncleActivity extends BaseActivity<ActivityAngryUncleBinding, BaseViewModel> {

    private PicsAdapter mAdapter;
    private int pos;
    private ShowAngryPkqDialog angryPkqDialog;
    private List<PicBean> pics;
    private int maxNum = 8;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_angry_uncle;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        angryPkqDialog = new ShowAngryPkqDialog(this);
        angryPkqDialog.setCenterBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新来一局
                pos = BaseUtils.getRandom(maxNum);
                mAdapter.setList(pics);
                angryPkqDialog.dismissDialog();
            }
        });

        binding.tvBack.setOnClickListener(lis -> {
            finish();
        });
        binding.tvMenu1.setOnClickListener(lis -> {
            maxNum = 4 * 7;
            resetData();
        });
        binding.tvMenu2.setOnClickListener(lis -> {
            maxNum = 4 * 5;
            resetData();
        });
        binding.tvMenu3.setOnClickListener(lis -> {
            maxNum = 4 * 3;
            resetData();
        });
        binding.tvMenu4.setOnClickListener(lis -> {
            maxNum = 4 * 2;
            resetData();
        });

        mAdapter = new PicsAdapter(R.layout.item_pics);
        binding.rvContent.setLayoutManager(new GridLayoutManager(this, 4));
        binding.rvContent.setAdapter(mAdapter);

        resetData();

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (angryPkqDialog.isShowing()) {
                return;
            }

            if (mAdapter.getData().get(position).getPosition() == pos) {
                angryPkqDialog.showDialog();
            }
            mAdapter.remove(position);
        }));


    }

    private void resetData() {
        pics = new ArrayList<>();
        pos = BaseUtils.getRandom(maxNum);

        for (int i = 0; i < maxNum; i++) {
            pics.add(new PicBean(i));
        }
        mAdapter.setList(pics);
    }
}
