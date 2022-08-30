package com.xxx.games.mahjong;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xxx.games.R;
import com.xxx.games.bean.MahjongBean;
import com.xxx.games.databinding.ItemMahjongBinding;

public class MahjongAdapter extends BaseQuickAdapter<MahjongBean, BaseViewHolder> {
    public MahjongAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MahjongBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMahjongBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.isSelect()) {
            binding.tvName.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.tvName.setBackgroundColor(getContext().getResources().getColor(R.color.pink));
        } else {
            binding.tvName.setTextColor(getContext().getResources().getColor(R.color.black));
            binding.tvName.setBackgroundColor(getContext().getResources().getColor(R.color.yellow));
        }
    }
}
