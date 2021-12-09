package com.xxx.games.mahjong;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xxx.games.R;
import com.xxx.games.addPlayer.TagBean;
import com.xxx.games.databinding.ItemPlayerBinding;
import com.xxx.games.databinding.ItemTagsBinding;

public class PlayerAdapter extends BaseQuickAdapter<PlayerBean, BaseViewHolder> {
    public PlayerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayerBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemPlayerBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.isSelect()) {
            binding.llItem.setBackground(getContext().getResources().getDrawable(R.drawable.btn_pink_sx));
        } else {
            binding.llItem.setBackground(getContext().getResources().getDrawable(R.drawable.btn_pink_2));
        }


    }
}
