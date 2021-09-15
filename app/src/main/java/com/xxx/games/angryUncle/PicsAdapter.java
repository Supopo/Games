package com.xxx.games.angryUncle;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xxx.games.addPlayer.TagBean;
import com.xxx.games.databinding.ItemPicsBinding;
import com.xxx.games.databinding.ItemTagsBinding;

public class PicsAdapter extends BaseQuickAdapter<PicBean, BaseViewHolder> {
    public PicsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PicBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemPicsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
