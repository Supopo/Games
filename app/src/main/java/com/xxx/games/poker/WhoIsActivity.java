package com.xxx.games.poker;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.nineoldandroids.view.ViewHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.xxx.games.BR;
import com.xxx.games.R;
import com.xxx.games.angryUncle.PicBean;
import com.xxx.games.databinding.ActivityWhoIsBinding;
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
 * 谁是卧底
 */
public class WhoIsActivity extends BaseActivity<ActivityWhoIsBinding, BaseViewModel> {

    private Handler handler;
    private Animation pokerOutAnim;
    private boolean canClick = true;
    private PokersAdapter mAdapter;
    private List<String> words;
    private int randomWords;
    private int userNum = 6;
    private int randomUser;
    private String[] strings;
    private AssetFileDescriptor rePoker;
    private MediaPlayer player;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_who_is;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        handler = new Handler();
        initWords();
        initView();
        initOneWord();
    }

    private void initOneWord() {
        randomWords = BaseUtils.getRandom(words.size());
        String tempWards = words.get(randomWords);
        strings = tempWards.split("--");
        randomUser = BaseUtils.getRandom(userNum);
    }

    private void initWords() {
        showDialog();
        try {
            InputStream inputStream = getAssets().open("words.txt");
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
        binding.tvMenu2.setText("人数 " + userNum);

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
        binding.tvMenu2.setOnClickListener(lis -> {
            QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(WhoIsActivity.this);
            builder.setTitle("人数")
                    .setPlaceholder("在此输入人数")
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            CharSequence text = builder.getEditText().getText();
                            if (text != null && text.length() > 0) {
                                userNum = Integer.parseInt(text.toString());
                                binding.tvMenu2.setText("人数 " + userNum);
                                toReset();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "请输入人数", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        });

        binding.tvMenu1.setOnClickListener(lis -> {
            toReset();
        });

        binding.tvPass.setOnClickListener(lis -> {
            if (canClick) {
                binding.rlCardRoot.setVisibility(View.VISIBLE);
                cardTurnover();
            }
        });

        mAdapter = new PokersAdapter(R.layout.item_poker);
        binding.rvAll.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvAll.setAdapter(mAdapter);
        mAdapter.setList(new ArrayList<>());

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if ((position) == randomUser) {
                    Toast.makeText(WhoIsActivity.this, strings[0], Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WhoIsActivity.this, strings[1], Toast.LENGTH_SHORT).show();
                }
                return false;
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

        pos = 0;
        binding.tvPass.setVisibility(View.VISIBLE);
        //重新抽词
        initOneWord();
        //清空rv数据
        mAdapter.setList(new ArrayList<>());
    }

    private int pos;//当前已抽牌人数

    /**
     * 翻牌
     */
    public void cardTurnover() {

        if (View.VISIBLE == binding.imageViewBack.getVisibility()) {
            //打开
            if (pos == randomUser) {
                binding.tvWord.setText(strings[0]);
            } else {
                binding.tvWord.setText(strings[1]);
            }

            pos++;
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
                }
            }, 1250);
        } else if (View.VISIBLE == binding.rlFront.getVisibility()) {
            //关闭
            if (pos == userNum) {
                binding.tvPass.setVisibility(View.INVISIBLE);
            }

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
                    pokerOutAnim = AnimationUtils.loadAnimation(WhoIsActivity.this, R.anim.poker_out);
                    binding.rlCardRoot.setAnimation(pokerOutAnim);

                    pokerOutAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            binding.rlCardRoot.setVisibility(View.INVISIBLE);
                            ViewHelper.setTranslationY(binding.rlCardRoot, 0);
                            mAdapter.addData(new PicBean(pos));
                            binding.rvAll.scrollToPosition(mAdapter.getData().size() - 1);
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
