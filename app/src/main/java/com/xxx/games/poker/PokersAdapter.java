package com.xxx.games.poker;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xxx.games.angryUncle.PicBean;
import com.xxx.games.databinding.ItemPicsBinding;
import com.xxx.games.databinding.ItemPokerBinding;

public class PokersAdapter extends BaseQuickAdapter<PicBean, BaseViewHolder> {
    public PokersAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PicBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemPokerBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
