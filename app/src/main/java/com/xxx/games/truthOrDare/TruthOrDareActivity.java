package com.xxx.games.truthOrDare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.addPlayer.AddPlayerActivity;
import com.xxx.games.addPlayer.TagBean;
import com.xxx.games.databinding.ActivityTruthOrDareBinding;
import com.xxx.games.widget.ITurntableListener;
import com.xxx.games.widget.ShowTruthOrDareDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ACache;

/**
 * 真心话大冒险
 */
public class TruthOrDareActivity extends BaseActivity<ActivityTruthOrDareBinding, BaseViewModel> {

    private TagBean players;
    private ShowTruthOrDareDialog dareDialog;
    private List<String> quas;
    private List<String> dos;
    private TagBean quasAcache;
    private TagBean dosAcache;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_truth_or_dare;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        //获取本地存储的数据
        players = (TagBean) ACache.get(this).getAsObject("players");
        //初始化数据
        initQua();
        initDo();
    }

    public void initQua() {
        quasAcache = (TagBean) ACache.get(this).getAsObject("quas");
        if (quasAcache != null && quasAcache.getQuado() != null && quasAcache.getQuado().size() > 0) {
            return;
        }

        try {
            InputStream inputStream = getAssets().open("qua.txt");
            String str = getString(inputStream);
            quas = Arrays.asList(str.split("\\?"));
            List<TagBean> temp = new ArrayList<>();
            for (String qua : quas) {
                temp.add(new TagBean(qua));
            }
            TagBean tagBean = new TagBean();
            tagBean.setQuado(temp);
            //储存到本地
            ACache.get(this).put("quas", tagBean);
            quasAcache = tagBean;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void initDo() {
        dosAcache = (TagBean) ACache.get(this).getAsObject("dos");
        if (dosAcache != null && dosAcache.getQuado() != null && dosAcache.getQuado().size() > 0) {
            return;
        }
        try {
            InputStream inputStream = getAssets().open("do.txt");
            String str = getString(inputStream);
            dos = Arrays.asList(str.split("~"));
            List<TagBean> temp = new ArrayList<>();
            for (String todo : dos) {
                temp.add(new TagBean(todo));
            }
            TagBean tagBean = new TagBean();
            tagBean.setQuado(temp);
            //储存到本地
            ACache.get(this).put("dos", tagBean);
            dosAcache = tagBean;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();

        if (players != null && players.getTags() != null) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();
            for (TagBean tag : players.getTags()) {
                names.add(tag.getTag());
                colors.add(tag.getPlaceholderRes());
            }
            binding.turntable.setDatas(names, colors);
        } else {
            initViewData();
        }

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.tvBack.setOnClickListener(lis -> {
            finish();
        });
        binding.tvAdd.setOnClickListener(lis -> {
            startActivityForResult(new Intent(TruthOrDareActivity.this, AddPlayerActivity.class), 100);
        });
        binding.tvAddQua.setOnClickListener(lis -> {
            startActivityForResult(new Intent(TruthOrDareActivity.this, AddQuasActivity.class), 100);
        });
        binding.tvAddDoit.setOnClickListener(lis -> {
            startActivityForResult(new Intent(TruthOrDareActivity.this, AddDosActivity.class), 100);
        });

        binding.ivNode.setOnClickListener(lis -> {
            binding.turntable.startRotate(new ITurntableListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onEnd(int position, String name) {
                    dareDialog = new ShowTruthOrDareDialog(TruthOrDareActivity.this, name, quasAcache.getQuado(), dosAcache.getQuado());
                    dareDialog.showDialog();
                }
            });
        });
    }

    private void initViewData() {
        int num = 8;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            names.add((i + 1) + "号玩家");
        }
        binding.turntable.setDatas(num, names);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            //重新initViewData
            if (data != null) {
                TagBean tags = (TagBean) data.getSerializableExtra("tags");
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();
                if (tags != null && tags.getTags() != null) {
                    for (TagBean tag : tags.getTags()) {
                        names.add(tag.getTag());
                        colors.add(tag.getPlaceholderRes());
                    }
                }
                binding.turntable.setDatas(names, colors);
            }
        } else if (resultCode == 102) {
            //重新去取
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    quasAcache = (TagBean) ACache.get(TruthOrDareActivity.this).getAsObject("quas");
                }
            }, 300);
        } else if (resultCode == 103) {
            //重新去取
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dosAcache = (TagBean) ACache.get(TruthOrDareActivity.this).getAsObject("dos");
                }
            }, 300);
        }
    }
}