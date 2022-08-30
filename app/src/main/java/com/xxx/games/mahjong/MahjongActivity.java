package com.xxx.games.mahjong;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.bean.MahjongBean;
import com.xxx.games.bean.PlayerBean;
import com.xxx.games.databinding.ActivityMahjongBinding;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 记牌器
 */
public class MahjongActivity extends BaseActivity<ActivityMahjongBinding, BaseViewModel> {

    private MahjongAdapter adapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_mahjong;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    private String[] mahjongs = {
            "一\n万", "二\n万", "三\n万", "四\n万", "五\n万", "六\n万", "七\n万", "八\n万", "九\n万",
            "一\n条", "二\n条", "三\n条", "四\n条", "五\n条", "六\n条", "七\n条", "八\n条", "九\n条",
            "一\n筒", "二\n筒", "三\n筒", "四\n筒", "五\n筒", "六\n筒", "七\n筒", "八\n筒", "九\n筒",
    };
    private List<MahjongBean> allMahs = new ArrayList<>();
    private List<PlayerBean> allNames = new ArrayList<>();
    private String[] names = {"ME", "左", "前", "右",};

    @Override
    public void initData() {
        super.initData();
        initNames();
        initMahjong();
        //出牌
        binding.btn1.setOnClickListener((view) -> {
            if (nowMahjongPos < 0) {
                ToastUtils.showShort("请先选择牌");
                return;
            }
            if (allMahs.get(nowMahjongPos).getCount() > 0) {
                allMahs.get(nowMahjongPos).setCount(allMahs.get(nowMahjongPos).getCount() - 1);
                adapter.notifyItemChanged(nowMahjongPos);
            }
        });
        //碰
        binding.btn2.setOnClickListener((view) -> {
            if (nowMahjongPos < 0) {
                ToastUtils.showShort("请先选择牌");
                return;
            }
            if (allMahs.get(nowMahjongPos).getCount() > 0) {
                allMahs.get(nowMahjongPos).setCount(allMahs.get(nowMahjongPos).getCount() - 2);
                adapter.notifyItemChanged(nowMahjongPos);
            }
        });
        //杠
        binding.btn3.setOnClickListener((view) -> {
            if (nowMahjongPos < 0) {
                ToastUtils.showShort("请先选择牌");
                return;
            }
            if (allMahs.get(nowMahjongPos).getCount() > 0) {
                allMahs.get(nowMahjongPos).setCount(allMahs.get(nowMahjongPos).getCount() - 3);
                adapter.notifyItemChanged(nowMahjongPos);
            }
        });
    }

    private int nowPlayerPos = 0;
    private int nowMahjongPos = -1;

    private void initNames() {
        for (String name : names) {
            allNames.add(new PlayerBean(name));
        }
        binding.rvNames.setLayoutManager(new GridLayoutManager(this, 4));
        PlayerAdapter playerAdapter = new PlayerAdapter(R.layout.item_player);
        binding.rvNames.setAdapter(playerAdapter);
        allNames.get(nowPlayerPos).setSelect(true);
        playerAdapter.setNewInstance(allNames);

        playerAdapter.setOnItemClickListener(((adapter, view, position) -> {
            allNames.get(nowPlayerPos).setSelect(false);
            nowPlayerPos = position;
            allNames.get(nowPlayerPos).setSelect(true);
            //刷新
            playerAdapter.notifyDataSetChanged();

        }));

    }

    private void initMahjong() {
        //初始化所有麻将
        for (String name : mahjongs) {
            allMahs.add(new MahjongBean(name));
        }

        binding.rv1.setLayoutManager(new GridLayoutManager(this, 9));
        adapter = new MahjongAdapter(R.layout.item_mahjong);
        binding.rv1.setAdapter(adapter);
        adapter.setNewInstance(allMahs);

        adapter.setOnItemClickListener(((adapter, view, position) -> {
            if (nowMahjongPos != -1) {
                allMahs.get(nowMahjongPos).setSelect(false);
                adapter.notifyItemChanged(nowMahjongPos);
            }
            nowMahjongPos = position;
            allMahs.get(nowMahjongPos).setSelect(true);
            adapter.notifyItemChanged(nowMahjongPos);
        }));
    }
}