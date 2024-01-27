package org.godotengine.plugin.android.admob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

interface InterstitialListener {
    void onInterstitialLoaded();
    void onInterstitialFailedToLoad(int errorCode);
    void onInterstitialFailedToShow(int errorCode);
    void onInterstitialOpened();
    void onInterstitialClosed();
    void onInterstitialClicked();
    void onInterstitialImpression();
}

public class Interstitial {
    private static final String CLASS_NAME = Interstitial.class.getSimpleName();
    private static final String LOG_TAG = "godot::" + GodotAndroidAdmobPlugin.CLASS_NAME + "::" + CLASS_NAME;
    private final String id;
    private final AdRequest adRequest;
    private InterstitialAd interstitialAd = null;
    private final Activity activity;
    private final InterstitialListener listener;

    public Interstitial(final String id, final AdRequest adRequest, final Activity activity, final InterstitialListener listener) {
        this.activity = activity;
        this.id = id;
        this.adRequest = adRequest;
        this.listener = listener;
        load();
    }

    public void show() {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        } else {
            Log.w(LOG_TAG, "show ad - interstitial not loaded");
        }
    }

    private void setAd(InterstitialAd interstitialAd) {
        if (interstitialAd == this.interstitialAd)
            return;

        // Avoid memory leaks
        if (this.interstitialAd != null) {
            this.interstitialAd.setFullScreenContentCallback(null);
            this.interstitialAd.setOnPaidEventListener(null);
        }

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Log.i(LOG_TAG, "interstitial ad clicked");
                    listener.onInterstitialClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    setAd(null);
                    Log.w(LOG_TAG, "interstitial ad dismissed full screen consent");
                    listener.onInterstitialClosed();
                    load();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e(LOG_TAG, "interstitial ad failed to show full screen content");
                    listener.onInterstitialFailedToShow(adError.getCode());
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.i(LOG_TAG, "interstitial ad showed full screen content");
                    listener.onInterstitialOpened();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.i(LOG_TAG, "interstitial ad impression");
                    listener.onInterstitialImpression();
                }
            });
        }
        this.interstitialAd = interstitialAd;
    }

    private void load() {
        InterstitialAd.load(activity, id, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                setAd(interstitialAd);
                Log.i(LOG_TAG, "interstitial ad loaded");
                listener.onInterstitialLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                // safety
                setAd(null);
                Log.e(LOG_TAG, "interstitial ad failed to load - error code: " + loadAdError.getCode());
                listener.onInterstitialFailedToLoad(loadAdError.getCode());
            }
        });
    }
}
