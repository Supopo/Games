package com.xxx.games.addPlayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.ColorsUtils;

/**
 * Created by Supopo. on 2021/9/10.
 */
public class TagBean implements Parcelable {
    private String tag;
    private int placeholderRes;

    protected TagBean(Parcel in) {
        tag = in.readString();
        placeholderRes = in.readInt();
        tags = in.createTypedArrayList(TagBean.CREATOR);
        quado = in.createTypedArrayList(TagBean.CREATOR);
    }

    public static final Creator<TagBean> CREATOR = new Creator<TagBean>() {
        @Override
        public TagBean createFromParcel(Parcel in) {
            return new TagBean(in);
        }

        @Override
        public TagBean[] newArray(int size) {
            return new TagBean[size];
        }
    };

    public List<TagBean> getTags() {
        return tags;
    }

    public void setTags(List<TagBean> tags) {
        this.tags = tags;
    }

    private List<TagBean> tags ;

    public List<TagBean> getQuado() {
        return quado;
    }

    public void setQuado(List<TagBean> quado) {
        this.quado = quado;
    }

    private List<TagBean> quado ;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tag);
        dest.writeInt(placeholderRes);
        dest.writeTypedList(tags);
        dest.writeTypedList(quado);
    }
}
