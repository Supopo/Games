package com.xxx.games.mahjong;

/**
 * Created by Supopo. on 2021/12/9.
 */
public class PlayerBean {
    public PlayerBean() {
    }

    public PlayerBean(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect;
}
