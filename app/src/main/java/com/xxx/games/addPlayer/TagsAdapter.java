package com.xxx.games.addPlayer;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xxx.games.databinding.ItemTagsBinding;

import me.goldze.mvvmhabit.utils.ColorsUtils;

public class TagsAdapter extends BaseQuickAdapter<TagBean, BaseViewHolder> {
    public TagsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, TagBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemTagsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
