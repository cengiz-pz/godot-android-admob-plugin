package org.godotengine.plugin.android.admob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

interface RewardedInterstitialListener {
    void onRewardedInterstitialLoaded();
    void onRewardedInterstitialOpened();
    void onRewardedInterstitialClosed();
    void onRewardedInterstitialFailedToLoad(int errorCode);
    void onRewardedInterstitialFailedToShow(int errorCode);
    void onRewarded(String type, int amount);
    void onRewardedClicked();
    void onRewardedAdImpression();
}

public class RewardedInterstitial {
    private static final String CLASS_NAME = RewardedInterstitial.class.getSimpleName();
    private static final String LOG_TAG = "godot::" + GodotAndroidAdmobPlugin.CLASS_NAME + "::" + CLASS_NAME;
    private RewardedInterstitialAd rewardedAd = null;
    private final Activity activity;
    private final RewardedInterstitialListener listener;

    public RewardedInterstitial(Activity activity, final RewardedInterstitialListener listener) {
        this.activity = activity;
        this.listener = listener;
        MobileAds.initialize(activity);
    }

    public void load(final String id, AdRequest adRequest) {

        RewardedInterstitialAd.load(activity, id, adRequest, new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedInterstitialAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                setAd(rewardedAd);
                Log.i(LOG_TAG, "rewarded interstitial ad loaded");
                listener.onRewardedInterstitialLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                // safety
                setAd(null);
                Log.e(LOG_TAG, "rewarded interstitial ad failed to load. errorCode: " + loadAdError.getCode());
                listener.onRewardedInterstitialFailedToLoad(loadAdError.getCode());
            }
        });
    }

    public void show() {
        if (rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> {
                Log.i(LOG_TAG, String.format("rewarded interstitial ad rewarded! currency: %s amount: %d", rewardItem.getType(), rewardItem.getAmount()));
                listener.onRewarded(rewardItem.getType(), rewardItem.getAmount());
            });
        }
    }

    private void setAd(RewardedInterstitialAd rewardedAd) {
        // Avoid memory leaks.
        if (this.rewardedAd != null)
            this.rewardedAd.setFullScreenContentCallback(null);

        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Log.i(LOG_TAG, "rewarded interstitial ad clicked");
                    listener.onRewardedClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.w(LOG_TAG, "rewarded interstitial ad dismissed full screen content");
                    listener.onRewardedInterstitialClosed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e(LOG_TAG, "rewarded interstitial ad failed to show full screen content");
                    listener.onRewardedInterstitialFailedToShow(adError.getCode());
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.i(LOG_TAG, "rewarded interstitial ad impression");
                    listener.onRewardedAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.i(LOG_TAG, "rewarded interstitial ad showed full screen content");
                    listener.onRewardedInterstitialOpened();
                }
            });
        }
        this.rewardedAd = rewardedAd;
    }
}
