package org.godotengine.plugin.android.admob;

import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GodotAndroidAdmobPlugin extends GodotPlugin {
    static final String CLASS_NAME = GodotAndroidAdmobPlugin.class.getSimpleName();
    private static final String LOG_TAG = "godot::" + CLASS_NAME;

    private static final String SIGNAL_NAME_BANNER_LOADED = "banner_loaded";
    private static final String SIGNAL_NAME_BANNER_FAILED_TO_LOAD = "banner_failed_to_load";
    private static final String SIGNAL_NAME_INTERSTITIAL_LOADED = "interstitial_loaded";
    private static final String SIGNAL_NAME_INTERSTITIAL_OPENED = "interstitial_opened";
    private static final String SIGNAL_NAME_INTERSTITIAL_CLOSED = "interstitial_closed";
    private static final String SIGNAL_NAME_INTERSTITIAL_CLICKED = "interstitial_clicked";
    private static final String SIGNAL_NAME_INTERSTITIAL_IMPRESSION = "interstitial_impression";
    private static final String SIGNAL_NAME_INTERSTITIAL_FAILED_TO_LOAD = "interstitial_failed_to_load";
    private static final String SIGNAL_NAME_INTERSTITIAL_FAILED_TO_SHOW = "interstitial_failed_to_show";
    private static final String SIGNAL_NAME_REWARDED_VIDEO_OPENED = "rewarded_video_opened";
    private static final String SIGNAL_NAME_REWARDED_VIDEO_LOADED = "rewarded_video_loaded";
    private static final String SIGNAL_NAME_REWARDED_VIDEO_CLOSED = "rewarded_video_closed";
    private static final String SIGNAL_NAME_REWARDED_VIDEO_FAILED_TO_LOAD = "rewarded_video_failed_to_load";
    private static final String SIGNAL_NAME_REWARDED_VIDEO_FAILED_TO_SHOW = "rewarded_video_failed_to_show";
    private static final String SIGNAL_NAME_REWARDED_INTERSTITIAL_OPENED = "rewarded_interstitial_opened";
    private static final String SIGNAL_NAME_REWARDED_INTERSTITIAL_LOADED = "rewarded_interstitial_loaded";
    private static final String SIGNAL_NAME_REWARDED_INTERSTITIAL_CLOSED = "rewarded_interstitial_closed";
    private static final String SIGNAL_NAME_REWARDED_INTERSTITIAL_FAILED_TO_LOAD = "rewarded_interstitial_failed_to_load";
    private static final String SIGNAL_NAME_REWARDED_INTERSTITIAL_FAILED_TO_SHOW = "rewarded_interstitial_failed_to_show";
    private static final String SIGNAL_NAME_REWARDED = "rewarded";
    private static final String SIGNAL_NAME_REWARDED_CLICKED = "rewarded_clicked";
    private static final String SIGNAL_NAME_REWARDED_IMPRESSION = "rewarded_impression";

    private Activity activity;

    /**
     * Whether app is being tested (isReal=false) or app is in production (isReal=true)
     */
    private boolean isReal = false;


    private boolean isForChildDirectedTreatment = false;

    /**
     * Ads are personalized by default, GDPR compliance within the European Economic Area may require disabling of personalization.
     */
    private boolean isPersonalized = true;
    private String maxAdContentRating = "";
    private Bundle extras = null;

    private FrameLayout layout = null;

    private RewardedVideo rewardedVideo = null;
    private RewardedInterstitial rewardedInterstitial = null;
    private Interstitial interstitial = null;
    private Banner banner = null;

    public GodotAndroidAdmobPlugin(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return CLASS_NAME;
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo(SIGNAL_NAME_BANNER_LOADED));
        signals.add(new SignalInfo(SIGNAL_NAME_BANNER_FAILED_TO_LOAD, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_LOADED));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_OPENED));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_CLOSED));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_CLICKED));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_IMPRESSION));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_FAILED_TO_LOAD, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_INTERSTITIAL_FAILED_TO_SHOW, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_VIDEO_OPENED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_VIDEO_LOADED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_VIDEO_CLOSED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_VIDEO_FAILED_TO_LOAD, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_VIDEO_FAILED_TO_SHOW, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_INTERSTITIAL_OPENED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_INTERSTITIAL_LOADED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_INTERSTITIAL_CLOSED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_INTERSTITIAL_FAILED_TO_LOAD, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_INTERSTITIAL_FAILED_TO_SHOW, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED, String.class, Integer.class));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_CLICKED));
        signals.add(new SignalInfo(SIGNAL_NAME_REWARDED_IMPRESSION));

        return signals;
    }

    @Override
    public View onMainCreate(Activity activity) {
        this.activity = activity;
        this.layout = new FrameLayout(activity); // create and add a new layout to Godot
        return layout;
    }

    @UsedByGodot
    public void init(boolean isReal) {
        this.initWithContentRating(isReal, isForChildDirectedTreatment, isPersonalized, maxAdContentRating);
    }

    /**
     * Initialize with additional content rating options
     *
     * @param isReal                      Tell if the environment is for real or test
     * @param isForChildDirectedTreatment Target audience is children.
     * @param isPersonalized              If ads should be personalized or not.
     *                                    GDPR compliance within the European Economic Area requires that you
     *                                    disable ad personalization if the user does not wish to opt into
     *                                    ad personalization.
     * @param maxAdContentRating          must be "G", "PG", "T" or "MA"
     * @see <a href="https://developers.google.com/admob/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk">EU consent info</a>
     */
    @UsedByGodot
    public void initWithContentRating(
            boolean isReal,
            boolean isForChildDirectedTreatment,
            boolean isPersonalized,
            String maxAdContentRating) {

        this.isReal = isReal;
        this.isForChildDirectedTreatment = isForChildDirectedTreatment;
        this.isPersonalized = isPersonalized;
        this.maxAdContentRating = maxAdContentRating;

        this.setRequestConfigurations();

        if (!isPersonalized) {
            if (extras == null) {
                extras = new Bundle();
            }
            extras.putString("npa", "1");
        }

        Log.d(LOG_TAG, "init AdMob with content rating options");
    }


    private void setRequestConfigurations() {
        if (!this.isReal) {
            @SuppressLint("VisibleForTests") List<String> testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR, getAdMobDeviceId());
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setTestDeviceIds(testDeviceIds)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }

        if (this.isForChildDirectedTreatment) {
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }

        if (this.maxAdContentRating != null && !this.maxAdContentRating.isEmpty()) {
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setMaxAdContentRating(this.maxAdContentRating)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }
    }


    /**
     * Returns AdRequest object constructed considering the extras.
     *
     * @return AdRequest object
     */
    private AdRequest getAdRequest() {
        AdRequest.Builder adBuilder = new AdRequest.Builder();
        AdRequest adRequest;
        if (!this.isForChildDirectedTreatment && extras != null) {
            adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        adRequest = adBuilder.build();
        return adRequest;
    }


    /**
     * Load an AdMob rewarded video ad
     *
     * @param id AdMob Rewarded video ID
     */
    @UsedByGodot
    public void loadRewardedVideo(final String id) {
        activity.runOnUiThread(() -> {
            rewardedVideo = new RewardedVideo(activity, new RewardedVideoListener() {
                @Override
                public void onRewardedVideoLoaded() {
                    emitSignal(SIGNAL_NAME_REWARDED_VIDEO_LOADED);
                }

                @Override
                public void onRewardedVideoFailedToLoad(int errorCode) {
                    emitSignal(SIGNAL_NAME_REWARDED_VIDEO_FAILED_TO_LOAD, errorCode);
                }

                @Override
                public void onRewardedVideoFailedToShow(int errorCode) {
                    emitSignal(SIGNAL_NAME_REWARDED_VIDEO_FAILED_TO_SHOW, errorCode);
                }

                @Override
                public void onRewardedVideoOpened() {
                    emitSignal(SIGNAL_NAME_REWARDED_VIDEO_OPENED);
                }

                @Override
                public void onRewardedVideoClosed() {
                    emitSignal(SIGNAL_NAME_REWARDED_VIDEO_CLOSED);
                }

                @Override
                public void onRewarded(String type, int amount) {
                    emitSignal(SIGNAL_NAME_REWARDED, type, amount);
                }

                @Override
                public void onRewardedClicked() {
                    emitSignal(SIGNAL_NAME_REWARDED_CLICKED);
                }

                @Override
                public void onRewardedAdImpression() {
                    emitSignal(SIGNAL_NAME_REWARDED_IMPRESSION);
                }
            });
            rewardedVideo.load(id, getAdRequest());
        });
    }

    @UsedByGodot
    public void showRewardedVideo() {
        activity.runOnUiThread(() -> {
            if (rewardedVideo == null) {
                return;
            }
            rewardedVideo.show();
        });
    }

    /**
     * Load an AdMob rewarded interstitial
     *
     * @param id app's AdMob ID for rewarded interstitial ads
     */
    @UsedByGodot
    public void loadRewardedInterstitial(final String id) {
        activity.runOnUiThread(() -> {
            rewardedInterstitial = new RewardedInterstitial(activity, new RewardedInterstitialListener() {
                @Override
                public void onRewardedInterstitialLoaded() {
                    emitSignal(SIGNAL_NAME_REWARDED_INTERSTITIAL_LOADED);
                }

                @Override
                public void onRewardedInterstitialOpened() {
                    emitSignal(SIGNAL_NAME_REWARDED_INTERSTITIAL_OPENED);
                }

                @Override
                public void onRewardedInterstitialClosed() {
                    emitSignal(SIGNAL_NAME_REWARDED_INTERSTITIAL_CLOSED);
                }

                @Override
                public void onRewardedInterstitialFailedToLoad(int errorCode) {
                    emitSignal(SIGNAL_NAME_REWARDED_INTERSTITIAL_FAILED_TO_LOAD, errorCode);
                }

                @Override
                public void onRewardedInterstitialFailedToShow(int errorCode) {
                    emitSignal(SIGNAL_NAME_REWARDED_INTERSTITIAL_FAILED_TO_SHOW, errorCode);
                }

                @Override
                public void onRewarded(String type, int amount) {
                    emitSignal(SIGNAL_NAME_REWARDED, type, amount);
                }

                @Override
                public void onRewardedClicked() {
                    emitSignal(SIGNAL_NAME_REWARDED_CLICKED);
                }

                @Override
                public void onRewardedAdImpression() {
                    emitSignal(SIGNAL_NAME_REWARDED_IMPRESSION);
                }
            });
            rewardedInterstitial.load(id, getAdRequest());
        });
    }

    @UsedByGodot
    public void showRewardedInterstitial() {
        activity.runOnUiThread(() -> {
            if (rewardedInterstitial == null) {
                return;
            }
            rewardedInterstitial.show();
        });
    }

    /**
     * Load an AdMob banner ad
     *
     * @param id      AdMod Banner ID
     * @param isOnTop To made the banner top or bottom
     */
    @UsedByGodot
    public void loadBanner(final String id, final boolean isOnTop, final String bannerSize) {
        activity.runOnUiThread(() -> {
            if (banner != null) banner.remove();
            banner = new Banner(id, getAdRequest(), activity, new BannerListener() {
                @Override
                public void onBannerLoaded() {
                    emitSignal(SIGNAL_NAME_BANNER_LOADED);
                }

                @Override
                public void onBannerFailedToLoad(int errorCode) {
                    emitSignal(SIGNAL_NAME_BANNER_FAILED_TO_LOAD, errorCode);
                }
            }, isOnTop, layout, bannerSize);
        });
    }

    @UsedByGodot
    public void showBanner() {
        activity.runOnUiThread(() -> {
            if (banner != null) {
                banner.show();
            }
        });
    }

    /**
     * Change banner's layer level
     * @param isOnTop whether the banner is on top of all layers
     */
    @UsedByGodot
    public void moveBanner(final boolean isOnTop) {
        activity.runOnUiThread(() -> {
            if (banner != null) {
                banner.move(isOnTop);
            }
        });
    }

    @UsedByGodot
    public void resizeBanner() {
        activity.runOnUiThread(() -> {
            if (banner != null) {
                banner.resize();
            }
        });
    }

    @UsedByGodot
    public void hideBanner() {
        activity.runOnUiThread(() -> {
            if (banner != null) {
                banner.hide();
            }
        });
    }

    @UsedByGodot
    public int getBannerWidth() {
        if (banner != null) {
            return banner.getWidth();
        }
        return 0;
    }

    @UsedByGodot
    public int getBannerHeight() {
        if (banner != null) {
            return banner.getHeight();
        }
        return 0;
    }

    /**
     * Load an interstitial ad
     *
     * @param id AdMob interstitial ad ID
     */
    @UsedByGodot
    public void loadInterstitial(final String id) {
        activity.runOnUiThread(() -> interstitial = new Interstitial(id, getAdRequest(), activity, new InterstitialListener() {
            @Override
            public void onInterstitialLoaded() {
                emitSignal(SIGNAL_NAME_INTERSTITIAL_LOADED);
            }

            @Override
            public void onInterstitialFailedToLoad(int errorCode) {
                emitSignal(SIGNAL_NAME_INTERSTITIAL_FAILED_TO_LOAD, errorCode);
            }

            @Override
            public void onInterstitialFailedToShow(int errorCode) {
                emitSignal(SIGNAL_NAME_INTERSTITIAL_FAILED_TO_SHOW, errorCode);
            }

            @Override
            public void onInterstitialOpened() {
                // Not Implemented
                emitSignal(SIGNAL_NAME_INTERSTITIAL_OPENED);
            }

            @Override
            public void onInterstitialClosed() {
                emitSignal(SIGNAL_NAME_INTERSTITIAL_CLOSED);
            }

            @Override
            public void onInterstitialClicked() {
                emitSignal(SIGNAL_NAME_INTERSTITIAL_CLICKED);
            }

            @Override
            public void onInterstitialImpression() {
                emitSignal(SIGNAL_NAME_INTERSTITIAL_IMPRESSION);
            }
        }));
    }

    @UsedByGodot
    public void showInterstitial() {
        activity.runOnUiThread(() -> {
            if (interstitial != null) {
                interstitial.show();
            }
        });
    }

    /**
     * Generate MD5 for the deviceID
     *
     * @param s The string for which to generate the MD5
     * @return String The generated MD5
     */
    private static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & b));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "md5() - no such algorithm");
        }
        return "";
    }

    /**
     * Get the Device ID for AdMob
     *
     * @return String Device ID
     */
    private String getAdMobDeviceId() {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return md5(androidId).toUpperCase(Locale.US);
    }
}
