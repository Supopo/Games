package com.xxx.games.randomHero;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.bean.TagBean;
import com.xxx.games.databinding.ActivityRandomHeroBinding;
import com.xxx.games.randomTeam.NamesAdapter;
import com.xxx.games.utils.AutoLineLayoutManager;
import com.xxx.games.utils.BaseUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * 随机英雄
 * 1.所有随机
 * 2.分类随机
 */
public class RandomHeroActivity extends BaseActivity<ActivityRandomHeroBinding, BaseViewModel> {

    private List<TagBean> names1;
    private List<TagBean> names2;
    private List<TagBean> names3;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_random_hero;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    private String[] type1Heros = {
            "廉颇", "程咬金", "项羽", "刘邦", "牛魔", "白起", "张飞", "苏烈", "猪八戒", "阿古多",
    };
    private String[] type2Heros = {
            "凯", "哪吒", "雅典娜", "赵云", "钟无艳", "老夫子", "夏侯惇", "李信", "吕布", "露娜",
            "孙策", "亚瑟", "云缨", "狂铁", "曹操", "达摩", "典韦", "宫本武藏", "关羽", "刘备",
            "杨戬", "梦奇", "盘古", "夏洛特", "曜", "花木兰", "司空震", "马超", "蒙恬",
    };
    private String[] type3Heros = {
            "兰陵王", "孙悟空", "阿轲", "娜可露露", "李白", "韩信", "橘右京", "百里玄策", "裴擒虎", "云中君",
            "元歌", "镜", "澜",
    };
    private String[] type4Heros = {
            "王昭君", "甄姬", "司马懿", "安琪拉", "诸葛亮", "上官婉儿", "墨子", "芈月", "扁鹊", "妲己",
            "沈梦溪", "米莱狄", "高渐离", "周瑜", "不知火舞", "张良", "小乔", "武则天", "杨玉环", "姜子牙",
            "女娲", "貂蝉", "金蝉", "嬴政", "干将莫邪", "嫦娥", "西施", "弈星",
    };
    private String[] type5Heros = {
            "李元芳", "虞姬", "狄仁杰", "蒙犽", "后羿", "伽罗", "公孙离", "黄忠", "马可波罗", "孙尚香",
            "鲁班七号", "百里守约", "艾琳", "成吉思汗",
    };
    private String[] type6Heros = {
            "钟馗", "东皇太一", "刘禅", "庄周", "鬼谷子", "明世隐", "孙膑", "盾山", "大乔", "瑶",
            "蔡文姬", "太乙真人", "鲁班大师",
    };
    private String[] allHeros = {
            "廉颇", "程咬金", "项羽", "刘邦", "牛魔", "白起", "张飞", "苏烈", "猪八戒", "阿古多",
            "兰陵王", "孙悟空", "阿轲", "娜可露露", "李白", "韩信", "橘右京", "百里玄策", "裴擒虎", "云中君",
            "元歌", "镜", "澜",
            "凯", "哪吒", "雅典娜", "赵云", "钟无艳", "老夫子", "夏侯惇", "李信", "吕布", "露娜",
            "孙策", "亚瑟", "云缨", "狂铁", "曹操", "达摩", "典韦", "宫本武藏", "关羽", "刘备",
            "杨戬", "梦奇", "盘古", "夏洛特", "曜", "花木兰", "司空震", "马超", "蒙恬",
            "王昭君", "甄姬", "司马懿", "安琪拉", "诸葛亮", "上官婉儿", "墨子", "芈月", "扁鹊", "妲己",
            "沈梦溪", "米莱狄", "高渐离", "周瑜", "不知火舞", "张良", "小乔", "武则天", "杨玉环", "姜子牙",
            "女娲", "貂蝉", "金蝉", "嬴政", "干将莫邪", "嫦娥", "西施", "弈星",
            "李元芳", "虞姬", "狄仁杰", "蒙犽", "后羿", "伽罗", "公孙离", "黄忠", "马可波罗", "孙尚香",
            "鲁班七号", "百里守约", "艾琳", "成吉思汗",
            "钟馗", "东皇太一", "刘禅", "庄周", "鬼谷子", "明世隐", "孙膑", "盾山", "大乔", "瑶",
            "蔡文姬", "太乙真人", "鲁班大师",
    };

    private int random;
    private int count = 1;

    @Override
    public void initData() {
        super.initData();
        binding.tvBack.setOnClickListener(lis ->{
            finish();
        });
        binding.tvMenu2.setOnClickListener(lis -> {
            if (count == 1) {
                count = 2;
                binding.tvMenu2.setText("x2");
                binding.tvTitle3.setVisibility(View.GONE);
                binding.rvTeam3.setVisibility(View.GONE);
            } else if (count == 2) {
                count = 3;
                binding.tvMenu2.setText("x3");
            } else {
                count = 1;
                binding.tvMenu2.setText("x1");
                binding.tvTitle2.setVisibility(View.GONE);
                binding.rvTeam2.setVisibility(View.GONE);
                binding.tvTitle3.setVisibility(View.GONE);
                binding.rvTeam3.setVisibility(View.GONE);
            }

        });
        binding.tvMenu1.setOnClickListener(lis -> {
            showMenuDialog();
        });
    }

    private void toRandom() {
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        }, 500);

        initTeam1();
        if (count > 1) {
            initTeam2();
        }
        if (count == 3) {
            initTeam3();
        }
    }


    private int randomType;

    private void showMenuDialog() {
        final String menus[] = new String[]{"随机所有", "随机坦克", "随机战士", "随机刺客",
                "随机法师", "随机射手", "随机辅助"};
        new QMUIDialog.MenuDialogBuilder(this)
                .addItems(menus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        randomType = which;
                        binding.tvMenu1.setText(menus[which]);
                        toRandom();
                    }
                }).show();
    }

    private void initTeam1() {
        NamesAdapter namesAdapter = new NamesAdapter(R.layout.item_name);
        HashSet<Integer> randoms = new HashSet<>();
        names1 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            toRandom(randoms, names1);
        }
        namesAdapter.setNewInstance(names1);
        binding.rvTeam1.setLayoutManager(new AutoLineLayoutManager());
        binding.rvTeam1.setAdapter(namesAdapter);
        binding.rvTeam1.setVisibility(View.VISIBLE);
        binding.tvTitle1.setVisibility(View.VISIBLE);
    }

    private void initTeam2() {
        NamesAdapter namesAdapter = new NamesAdapter(R.layout.item_name);
        HashSet<Integer> randoms = new HashSet<>();
        names2 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            toRandom(randoms, names2);
        }
        namesAdapter.setNewInstance(names2);
        binding.rvTeam2.setLayoutManager(new AutoLineLayoutManager());
        binding.rvTeam2.setAdapter(namesAdapter);
        binding.rvTeam2.setVisibility(View.VISIBLE);
        binding.tvTitle2.setVisibility(View.VISIBLE);
    }

    private void initTeam3() {
        NamesAdapter namesAdapter = new NamesAdapter(R.layout.item_name);
        HashSet<Integer> randoms = new HashSet<>();
        names3 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            toRandom(randoms, names3);
        }
        namesAdapter.setNewInstance(names3);
        binding.rvTeam3.setLayoutManager(new AutoLineLayoutManager());
        binding.rvTeam3.setAdapter(namesAdapter);
        binding.rvTeam3.setVisibility(View.VISIBLE);
        binding.tvTitle3.setVisibility(View.VISIBLE);
    }


    private void toRandom(HashSet<Integer> randoms, List<TagBean> names) {
        String[] tempHeros = allHeros;
        if (randomType == 0) {
            tempHeros = allHeros;
        } else if (randomType == 1) {
            tempHeros = type1Heros;
        } else if (randomType == 2) {
            tempHeros = type2Heros;
        } else if (randomType == 3) {
            tempHeros = type3Heros;
        } else if (randomType == 4) {
            tempHeros = type4Heros;
        } else if (randomType == 5) {
            tempHeros = type5Heros;
        } else if (randomType == 6) {
            tempHeros = type6Heros;
        }
        random = BaseUtils.getRandom(tempHeros.length);

        if (randoms.contains(random)) {
            toRandom(randoms, names);
        } else {
            randoms.add(random);
            names.add(new TagBean(tempHeros[random]));
        }
    }
}