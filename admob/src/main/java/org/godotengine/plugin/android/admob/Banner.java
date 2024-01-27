package org.godotengine.plugin.android.admob;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

interface BannerListener {
    void onBannerLoaded();
    void onBannerFailedToLoad(int errorCode);
}

public class Banner {
    private static final String CLASS_NAME = Banner.class.getSimpleName();
    private static final String LOG_TAG = "godot::" + GodotAndroidAdmobPlugin.CLASS_NAME + "::" + CLASS_NAME;
    private AdView adView = null; // Banner view
    private final FrameLayout layout;
    private FrameLayout.LayoutParams adParams = null;
    private final AdRequest adRequest;
    private final Activity activity;
    private final String bannerSize;


    public Banner(final String id, final AdRequest adRequest, final Activity activity, final BannerListener listener, final boolean isOnTop, final FrameLayout layout, final String bannerSize) {
        this.activity = activity;
        this.layout = layout;
        this.adRequest = adRequest;
        this.bannerSize = bannerSize;

        addBanner(id, (isOnTop ? Gravity.TOP : Gravity.BOTTOM), getAdSize(bannerSize), new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i(LOG_TAG, "banner ad loaded");
                listener.onBannerLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.e(LOG_TAG, "banner ad failed to load. errorCode: " + adError.getCode());
                listener.onBannerFailedToLoad(adError.getCode());
            }
        });
    }

    public void show() {
        if (adView == null) {
            Log.w(LOG_TAG, "show ad - banner not loaded");
            return;
        }

        if (adView.getVisibility() == View.VISIBLE) {
            return;
        }

        adView.setVisibility(View.VISIBLE);
        adView.resume();
        Log.d(LOG_TAG, "show banner ad");
    }

    public void move(final boolean isOnTop)
    {
        if (layout == null || adView == null || adParams == null) {
            return;
        }

        layout.removeView(adView); // Remove the old view

        AdListener adListener = adView.getAdListener();
        String id = adView.getAdUnitId();
        addBanner(id, (isOnTop ? Gravity.TOP : Gravity.BOTTOM), adView.getAdSize(), adListener);

        Log.d(LOG_TAG, "banner ad moved");
    }

    public void resize() {
        if (layout == null || adView == null || adParams == null) {
            return;
        }

        layout.removeView(adView); // Remove the old view

        AdListener adListener = adView.getAdListener();
        String id = adView.getAdUnitId();
        addBanner(id, adParams.gravity, getAdSize(bannerSize), adListener);

        Log.d(LOG_TAG, "banner ad resized");
    }

    private void addBanner(final String id, final int gravity, final AdSize size, final AdListener listener) {
        adParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        adParams.gravity = gravity;

        // Create new view & set old params
        adView = new AdView(activity);
        adView.setAdUnitId(id);
        adView.setBackgroundColor(Color.TRANSPARENT);
        adView.setAdSize(size);
        adView.setAdListener(listener);

        // Add to layout and load ad
        layout.addView(adView, adParams);

        // Request
        adView.loadAd(adRequest);
    }

    public void remove() {
        if (adView != null) {
            layout.removeView(adView); // Remove the old view
        }
    }

    public void hide() {
        if (adView.getVisibility() == View.GONE) return;
        adView.setVisibility(View.GONE);
        adView.pause();
        Log.d(LOG_TAG, "hide banner ad");
    }

    private AdSize getAdaptiveAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        int widthPixels;
        float density;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            widthPixels = activity.getWindowManager().getCurrentWindowMetrics().getBounds().width();
            density = activity.getResources().getConfiguration().densityDpi;
        } else {
            display.getMetrics(outMetrics);
            widthPixels = outMetrics.widthPixels;
            density = outMetrics.density;
        }

        int adWidth = 50;
        if (density == 0) {
            Log.e(LOG_TAG, "Cannot detect display density.");
        } else {
            adWidth = (int) (widthPixels / density);
        }

        // Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    private AdSize getAdSize(final String bannerSize) {
        switch (bannerSize) {
            case "BANNER":
                return AdSize.BANNER;
            case "LARGE_BANNER":
                return AdSize.LARGE_BANNER;
            case "MEDIUM_RECTANGLE":
                return AdSize.MEDIUM_RECTANGLE;
            case "FULL_BANNER":
                return AdSize.FULL_BANNER;
            case "LEADERBOARD":
                return AdSize.LEADERBOARD;
            default:
                return getAdaptiveAdSize();
        }
    }

    public int getWidth() {
        return getAdSize(bannerSize).getWidthInPixels(activity);
    }

    public int getHeight() {
        return getAdSize(bannerSize).getHeightInPixels(activity);
    }
}
