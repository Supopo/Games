package com.xxx.games.addPlayer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.ColorsUtils;

/**
 * Created by Supopo. on 2021/9/10.
 */
public class TagBean implements Serializable {
    private String tag;
    private int placeholderRes;

    public List<TagBean> getTags() {
        return tags;
    }

    public void setTags(List<TagBean> tags) {
        this.tags = tags;
    }

    private List<TagBean> tags ;

    public int getPlaceholderRes() {
        return placeholderRes;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public TagBean() {
    }

    public TagBean(String tag) {
        this.tag = tag;
        this.placeholderRes = ColorsUtils.randomColor();
    }
}
