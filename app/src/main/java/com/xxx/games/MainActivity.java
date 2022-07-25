package com.xxx.games;

import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.xxx.games.angryUncle.AngryUncleActivity;
import com.xxx.games.app.Constant;
import com.xxx.games.call.CallSettingActivity;
import com.xxx.games.databinding.ActivityMainBinding;
import com.xxx.games.mahjong.MahjongActivity;
import com.xxx.games.parks.ParksActivity;
import com.xxx.games.poker.WhoIsActivity;
import com.xxx.games.randomHero.RandomHeroActivity;
import com.xxx.games.randomTeam.RandomActivity;
import com.xxx.games.truthOrDare.TruthOrDareActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Supopo. on 2021/9/10.a
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private final String TAG = "MainActivity";
    private AdSlot adSlot;
    private TTAdNative mTTAdNative;
    private boolean mIsLoaded;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        initAd();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.btnGame1.setOnClickListener(lis -> {
            startActivity(TruthOrDareActivity.class);
        });
        binding.btnGame2.setOnClickListener(lis -> {
            startActivity(AngryUncleActivity.class);
        });
        binding.btnGame3.setOnClickListener(lis -> {
            startActivity(WhoIsActivity.class);
        });
        binding.btnGame4.setOnClickListener(lis -> {
            startActivity(ParksActivity.class);
        });
        binding.btnGame5.setOnClickListener(lis -> {
            startActivity(CallSettingActivity.class);
        });
        binding.btnGame6.setOnClickListener(lis -> {
            startActivity(RandomActivity.class);
        });
        binding.btnGame7.setOnClickListener(lis -> {
            startActivity(RandomHeroActivity.class);
        });
        binding.btnGame8.setOnClickListener(lis -> {
            startActivity(MahjongActivity.class);
        });
        binding.btnGame9.setOnClickListener(lis -> {
            loadAd();
        });
    }

    public void initAd() {
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this);
        adSlot = new AdSlot.Builder()
                .setCodeId(Constant.AD_REWARD_ID)//广告位ID
                .setUserID("tag123")//tag_id
                .setMediaExtra("media_extra") //附加参数
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(TTAdLoadType.LOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();
    }

    public void loadAd() {
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "loadCallback --> onError: " + i + ", " + String.valueOf(s));
            }

            //广告加载完成的回调
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {

            }

            //广告视频本地加载完成的回调，可以在这个回调后直接播放本地视频
            @Override
            public void onRewardVideoCached() {
                Log.e(TAG, "loadCallback --> onRewardVideoCached");
                mIsLoaded = true;
            }

            //广告视频本地加载完成的回调，可以在这个回调后直接播放本地视频，直接使用参数对象来播放
            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                Log.e(TAG, "loadCallback --> onRewardVideoCached");
                mIsLoaded = true;
                if (ttRewardVideoAd != null) {
                    ttRewardVideoAd.showRewardVideoAd(MainActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");

                    ttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                        //视频广告展示回调
                        @Override
                        public void onAdShow() {
                            Log.e(TAG, "showCallback --> onAdShow");
                        }

                        //广告的下载bar点击回调
                        @Override
                        public void onAdVideoBarClick() {
                            Log.e(TAG, "showCallback --> onAdVideoBarClick");

                        }

                        //视频广告关闭回调
                        @Override
                        public void onAdClose() {
                            Log.e(TAG, "showCallback --> onAdClose");
                        }

                        //视频播放完成回调
                        @Override
                        public void onVideoComplete() {
                            Log.e(TAG, "showCallback --> onVideoComplete");
                        }

                        //视频广告播放错误回调
                        @Override
                        public void onVideoError() {
                            Log.e(TAG, "showCallback --> onVideoError");
                        }

                        //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称，code：错误码，msg：错误信息
                        @Override
                        public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                            Log.e(TAG, "showCallback --> onRewardVerify");
                        }

                        /**
                         * 激励视频播放完毕，验证是否有效发放奖励的回调 4400版本新增
                         *
                         * @param isRewardValid 奖励有效
                         * @param rewardType 奖励类型，0:基础奖励 >0:进阶奖励
                         * @param extraInfo 奖励的额外参数
                         */
                        @Override
                        public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                            Log.e(TAG, "showCallback --> onRewardArrived");
                        }

                        //视频广告跳过回调
                        @Override
                        public void onSkippedVideo() {
                            Log.e(TAG, "showCallback --> onSkippedVideo");
                        }
                    });
                    ttRewardVideoAd = null;
                }
            }

        });
    }
}
