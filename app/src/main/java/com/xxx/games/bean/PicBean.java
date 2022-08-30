package com.xxx.games.bean;

import me.goldze.mvvmhabit.utils.ColorsUtils;

/**
 * Created by Supopo. on 2021/9/15.
 */
public class PicBean {

    public PicBean() {
    }

    public PicBean(int position) {
        this.position = position;
    }

    public int getPlaceholderRes() {
        return ColorsUtils.randomColor();
    }

    public void setPlaceholderRes(int placeholderRes) {
        this.placeholderRes = placeholderRes;
    }

    private int position;
    private int placeholderRes;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
