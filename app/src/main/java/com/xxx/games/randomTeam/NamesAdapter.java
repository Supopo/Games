package com.xxx.games.randomTeam;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xxx.games.bean.TagBean;
import com.xxx.games.databinding.ItemNameBinding;

public class NamesAdapter extends BaseQuickAdapter<TagBean, BaseViewHolder> {
    public NamesAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, TagBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemNameBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
