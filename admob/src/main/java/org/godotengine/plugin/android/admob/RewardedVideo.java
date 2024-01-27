package org.godotengine.plugin.android.admob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

interface RewardedVideoListener {
    void onRewardedVideoLoaded();
    void onRewardedVideoFailedToLoad(int errorCode);
    void onRewardedVideoFailedToShow(int errorCode);
    void onRewardedVideoOpened();
    void onRewardedVideoClosed();
    void onRewarded(String type, int amount);
    void onRewardedClicked();
    void onRewardedAdImpression();
}

public class RewardedVideo {
    private static final String CLASS_NAME = RewardedVideo.class.getSimpleName();
    private static final String LOG_TAG = "godot::" + GodotAndroidAdmobPlugin.CLASS_NAME + "::" + CLASS_NAME;
    private RewardedAd rewardedAd = null;
    private final Activity activity;
    private final RewardedVideoListener listener;

    public RewardedVideo(Activity activity, final RewardedVideoListener listener) {
        this.activity = activity;
        this.listener = listener;
        MobileAds.initialize(activity);
    }

    public void load(final String id, AdRequest adRequest) {

        RewardedAd.load(activity, id, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                setAd(rewardedAd);
                Log.i(LOG_TAG, "rewarded video ad loaded");
                listener.onRewardedVideoLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                // safety
                setAd(null);
                Log.e(LOG_TAG, "rewarded video ad failed to load. errorCode: " + loadAdError.getCode());
                listener.onRewardedVideoFailedToLoad(loadAdError.getCode());
            }
        });
    }

    public void show() {
        if (rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> {
                Log.i(LOG_TAG, String.format("rewarded video ad reward received! currency: %s amount: %d", rewardItem.getType(), rewardItem.getAmount()));
                listener.onRewarded(rewardItem.getType(), rewardItem.getAmount());
            });
        }
    }

    private void setAd(RewardedAd rewardedAd) {
        // Avoid memory leaks.
        if (this.rewardedAd != null)
            this.rewardedAd.setFullScreenContentCallback(null);

        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Log.i(LOG_TAG, "rewarded video ad clicked");
                    listener.onRewardedClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.w(LOG_TAG, "rewarded video ad dismissed full screen content");
                    listener.onRewardedVideoClosed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e(LOG_TAG, "rewarded video ad failed to show full screen content");
                    listener.onRewardedVideoFailedToShow(adError.getCode());
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.i(LOG_TAG, "rewarded video ad impression");
                    listener.onRewardedAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.i(LOG_TAG, "rewarded video ad showed full screen content");
                    listener.onRewardedVideoOpened();
                }
            });
        }
        this.rewardedAd = rewardedAd;
    }
}
