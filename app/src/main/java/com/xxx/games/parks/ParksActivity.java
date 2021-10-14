package com.xxx.games.parks;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.nineoldandroids.view.ViewHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.angryUncle.PicBean;
import com.xxx.games.databinding.ActivityParksBinding;
import com.xxx.games.databinding.ActivityWhoIsBinding;
import com.xxx.games.poker.PokersAdapter;
import com.xxx.games.poker.Rotatable;
import com.xxx.games.utils.BaseUtils;

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

/**
 * Created by Supopo. on 2021/9/16.
 * 逛公园
 */
public class ParksActivity extends BaseActivity<ActivityParksBinding, BaseViewModel> {

    private Handler handler;
    private Animation pokerOutAnim;
    private boolean canClick = true;
    private List<String> words;
    private int randomWords;
    private AssetFileDescriptor rePoker;
    private MediaPlayer player;
    private int pos;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_parks;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        handler = new Handler();
        initWords();
        initView();
        initOneWord();
    }

    private boolean isRandom;

    private void initOneWord() {
        pos = 0;
        randomWords = isRandom ? BaseUtils.getRandom(words.size()) : pos;
    }

    private void initWords() {
        showDialog();
        try {
            InputStream inputStream = getAssets().open("parks.txt");
            String str = getString(inputStream);
            words = Arrays.asList(str.split(","));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        dismissDialog();
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

    private void initView() {
        setCameraDistance();

        AssetManager assetManager;
        assetManager = getResources().getAssets();

        try {
            rePoker = assetManager.openFd("poker_refresh.mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }


        binding.tvBack.setOnClickListener(lis -> {
            finish();
        });

        binding.tvMenu1.setOnClickListener(lis -> {
            toReset();
        });
        binding.tvMenu2.setOnClickListener(lis -> {
           isRandom = !isRandom;
           binding.tvMenu2.setText(isRandom ?"随机":"按序");
        });


        binding.tvPass.setOnClickListener(lis -> {
            if (canClick) {
                binding.rlCardRoot.setVisibility(View.VISIBLE);
                cardTurnover();
            }
        });

    }

    private void toReset() {

        try {
            player = new MediaPlayer();
            player.setDataSource(rePoker.getFileDescriptor(), rePoker.getStartOffset(), rePoker.getStartOffset());
            player.prepare();
            player.start();
            showDialog("正在洗牌...");

        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        }, 1000);

        binding.tvPass.setVisibility(View.VISIBLE);
        //重新抽词
        initOneWord();
    }


    /**
     * 翻牌
     */
    public void cardTurnover() {

        if (View.VISIBLE == binding.imageViewBack.getVisibility()) {

            binding.tvWord.setText(words.get(randomWords));

            canClick = false;

            ViewHelper.setTranslationY(binding.rlCardRoot, -50);
            ViewHelper.setRotationY(binding.rlFront, 180f);//先翻转180，转回来时就不是反转的了
            Rotatable rotatable = new Rotatable.Builder(binding.rlCardRoot)
                    .sides(R.id.imageView_back, R.id.rl_front)
                    .direction(Rotatable.ROTATE_Y)
                    .rotationCount(1)
                    .build();
            rotatable.setTouchEnable(false);
            rotatable.rotate(Rotatable.ROTATE_Y, -180, 1250);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.tvPass.setText("下一位");
                    canClick = true;

                    pos++;
                    randomWords = isRandom ? BaseUtils.getRandom(words.size()) : pos;
                }
            }, 1250);
        } else if (View.VISIBLE == binding.rlFront.getVisibility()) {

            canClick = false;
            Rotatable rotatable = new Rotatable.Builder(binding.rlCardRoot)
                    .sides(R.id.imageView_back, R.id.rl_front)
                    .direction(Rotatable.ROTATE_Y)
                    .rotationCount(1)
                    .build();
            rotatable.setTouchEnable(false);
            rotatable.rotate(Rotatable.ROTATE_Y, 0, 1000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //退场动画
                    binding.rlCardRoot.clearAnimation();
                    pokerOutAnim = AnimationUtils.loadAnimation(ParksActivity.this, R.anim.poker_out);
                    binding.rlCardRoot.setAnimation(pokerOutAnim);

                    pokerOutAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            binding.rlCardRoot.setVisibility(View.INVISIBLE);
                            ViewHelper.setTranslationY(binding.rlCardRoot, 0);
                            canClick = true;
                            binding.tvPass.setText("抽一张");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }, 1000);

        }
    }

    /**
     * 改变视角距离, 贴近屏幕
     */
    private void setCameraDistance() {
        int distance = 10000;
        float scale = getResources().getDisplayMetrics().density * distance;
        binding.rlCardRoot.setCameraDistance(scale);
    }
}
