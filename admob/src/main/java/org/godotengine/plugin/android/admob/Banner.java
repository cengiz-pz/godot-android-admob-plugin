//
// © 2024-present https://github.com/cengiz-pz
//

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

import org.godotengine.godot.Dictionary;

interface BannerListener {
	void onAdLoaded(String adId);
	void onAdFailedToLoad(String adId, LoadAdError loadAdError);
}


public class Banner {
	private static final String CLASS_NAME = Banner.class.getSimpleName();
	private static final String LOG_TAG = "godot::" + AdmobPlugin.CLASS_NAME + "::" + CLASS_NAME;

	private final String adId;
	private final String adUnitId;
	private final AdRequest adRequest;
	private final Activity activity;
	private final FrameLayout layout;
	private final String bannerSize;
	private AdView adView; // Banner view
	private FrameLayout.LayoutParams adParams;


	public Banner(final String adId, final String adUnitId, final Dictionary adData, final AdRequest adRequest,
				  final Activity activity, final FrameLayout layout, BannerListener listener) {
		this.adId = adId;
		this.adUnitId = adUnitId;

		if (adData.containsKey("banner_size")) {
			this.bannerSize = (String) adData.get("banner_size");
		}
		else {
			this.bannerSize = "ADAPTIVE";
			Log.e(LOG_TAG, "Error: Banner size is required!");
		}

		boolean isOnTop = false;
		if (adData.containsKey("is_on_top")) {
			isOnTop = (boolean) adData.get("is_on_top");
		}
		else {
			Log.w(LOG_TAG, "Warning: Ad position not specified.");
		}

		this.adRequest = adRequest;
		this.activity = activity;
		this.layout = layout;
		this.adView = null;
		this.adParams = null;

		addBanner((isOnTop ? Gravity.TOP : Gravity.BOTTOM), getAdSize(bannerSize), new AdListener() {
			@Override
			public void onAdLoaded() {
				listener.onAdLoaded(adId);
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError error) {
				listener.onAdFailedToLoad(adId, error);
			}
		});
	}

	public void show() {
		if (adView == null) {
			Log.w(LOG_TAG, "show(): Warning: banner ad not loaded.");
		}
		else if (adView.getVisibility() == View.VISIBLE) {
			Log.w(LOG_TAG, "show(): Warning: banner ad already visible.");
		}
		else {
			Log.d(LOG_TAG, String.format("show(): %s", this.adId));
			adView.setVisibility(View.VISIBLE);
			adView.resume();
		}
	}

	public void move(final boolean isOnTop) {
		if (layout == null || adView == null || adParams == null) {
			Log.w(LOG_TAG, "move(): Warning: banner ad not loaded.");
		}
		else {
			Log.d(LOG_TAG, "banner ad moved");

			layout.removeView(adView); // Remove the old view

			AdListener adListener = adView.getAdListener();
			addBanner((isOnTop ? Gravity.TOP : Gravity.BOTTOM), adView.getAdSize(), adListener);
		}
	}

	public void resize() {
		if (layout == null || adView == null || adParams == null) {
			Log.w(LOG_TAG, "move(): Warning: banner ad not loaded.");
		}
		else {
			Log.d(LOG_TAG, String.format("resize(): %s", this.adId));

			layout.removeView(adView); // Remove the old view

			AdListener adListener = adView.getAdListener();
			addBanner(adParams.gravity, getAdSize(bannerSize), adListener);
		}
	}

	private void addBanner(final int gravity, final AdSize size, final AdListener listener) {
		adParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT
		);
		adParams.gravity = gravity;

		// Create new view & set old params
		adView = new AdView(activity);
		adView.setAdUnitId(adUnitId);
		adView.setBackgroundColor(Color.TRANSPARENT);
		adView.setAdSize(size);
		adView.setAdListener(listener);

		// Add to layout and load ad
		layout.addView(adView, adParams);

		// Request
		adView.loadAd(adRequest);
	}

	public void remove() {
		if (adView == null) {
			Log.w(LOG_TAG, "remove(): Warning: adView is null.");
		}
		else {
			layout.removeView(adView);
		}
	}

	public void hide() {
		if (adView.getVisibility() != View.GONE) {
			adView.setVisibility(View.GONE);
			adView.pause();
		}
		else {
			Log.e(LOG_TAG, "Error: can't hide banner ad. Ad is not visible.");
		}
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
		return getAdSize(bannerSize).getWidth();
	}

	public int getHeight() {
		return getAdSize(bannerSize).getHeight();
	}

	public int getWidthInPixels() {
		return getAdSize(bannerSize).getWidthInPixels(activity);
	}

	public int getHeightInPixels() {
		return getAdSize(bannerSize).getHeightInPixels(activity);
	}
}
