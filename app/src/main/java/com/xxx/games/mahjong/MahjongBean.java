package com.xxx.games.mahjong;

import me.goldze.mvvmhabit.utils.ColorsUtils;

/**
 * Created by Supopo. on 2021/9/15.
 */
public class MahjongBean {

    public MahjongBean() {
    }

    public MahjongBean(String name) {
        this.name = name;
        this.count = 4;
    }

    public MahjongBean(int position) {
        this.position = position;
    }

    public int getPlaceholderRes() {
        return ColorsUtils.randomColor();
    }

    public void setPlaceholderRes(int placeholderRes) {
        this.placeholderRes = placeholderRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private String name;
    private int count;
    private int position;
    private int placeholderRes;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
