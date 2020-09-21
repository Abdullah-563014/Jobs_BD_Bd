package com.mominur77.Jobs_BD;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.ads.*;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import com.mominur77.Jobs_BD.database.DatabaseClient;
import com.mominur77.Jobs_BD.database.Page;
import com.mominur77.Jobs_BD.database_connection.ApiInterface;
import com.mominur77.Jobs_BD.database_connection.RetrofitClient;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewActivity extends Activity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar, centerProgressBar;
    private TextView addToFavouriteTextView;
    private String currentUrl, currentPageTitle,currentCountryName;
    private BroadcastReceiver onDownloadComplete;
    private long downloadID;
    private String url, downloadUrl, userAgent, contentDisposition, mimeType;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private int INPUT_FILE_REQUEST_CODE = 121, timeDiff=0;
    private boolean exitStatus = false, showFirstFacebookInterstitialAds = true, willLoadFacebookInterstitial = true, willLoadFirstInterstitialAds=true, willLoadFirstBannerAds=true;
    private InterstitialAd facebookInterstitialAd;
    private AlertDialog progressAlertDialog,permissionAlertDialog;
    private AdsThread adsThread;
    private AdView facebookBannerAdView;
    private MoPubInterstitial moPubInterstitial;
    private MoPubView moPubBannerAdsView;
    private View unityBannerView;
    private com.google.android.gms.ads.AdView admobBannerAdsView;
    public com.google.android.gms.ads.InterstitialAd admobInsterstitialAd;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);


        initBroadCastReceiver();

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        initializeAll();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("Url", null);
            willLoadFirstInterstitialAds=bundle.getBoolean("WillShowInterstitialAds",true);
            willLoadFirstBannerAds=bundle.getBoolean("WillShowInterstitialAds",true);
        }
        if (url != null) {
            openWebPage(url);
        } else {
            Toast.makeText(this, "Your web url is not valid", Toast.LENGTH_SHORT).show();
        }


        initializeSwipeRefreshLayout();

        initializeDownloadManager();

        addToFavouriteTextView.setOnClickListener(view -> bookmarkAlertDialog());

        loadAdsPlatformNameFromDatabase();



    }


    private void loadAdsPlatformNameFromDatabase() {
        databaseReference.child("Ads").child("adsPlatformName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue()!=null) {
                    String platform=dataSnapshot.getValue(String.class);
                    Utils.saveStringToStorage(WebViewActivity.this,Constants.platformNameKey,platform);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class AdsThread extends Thread {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHideCustomProgressBar(true);
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHideCustomProgressBar(false);
                    showFacebookInterstitialAds();
                }
            });
        }
    }

    private void initFacebookInterstitialAds() {
        if (facebookInterstitialAd != null) {
            facebookInterstitialAd.destroy();
            facebookInterstitialAd=null;
        }
//        facebookInterstitialAd = new InterstitialAd(this, getResources().getString(R.string.web_view_activity_facebook_interstitial_ads_code));
        facebookInterstitialAd = new InterstitialAd(this, Constants.facebookInterstitialAdsCode);
        facebookInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (facebookInterstitialAd != null) {
                    facebookInterstitialAd.destroy();
                    facebookInterstitialAd=null;
                }
                Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (willLoadFacebookInterstitial && showFirstFacebookInterstitialAds) {
                    facebookInterstitialAd.loadAd();
                }
                willLoadFacebookInterstitial = false;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (showFirstFacebookInterstitialAds && currentCountryName!=null && currentCountryName.equalsIgnoreCase("bangladesh")) {
                    if (adsThread != null) {
                        adsThread.interrupt();
                        adsThread = null;
                    }
                    adsThread = new AdsThread();
                    adsThread.start();
                }
                showFirstFacebookInterstitialAds = false;
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        facebookInterstitialAd.loadAd();
    }

    private void initFacebookBannerAdView() {
        if (facebookBannerAdView!=null){
            facebookBannerAdView.destroy();
            facebookBannerAdView=null;
        }
        facebookBannerAdView = new AdView(this, Constants.facebookBannerAdsCode, AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.webViewActivityFacebookBannerAdsContainerId);
        adContainer.setVisibility(View.VISIBLE);
        adContainer.addView(facebookBannerAdView);
        facebookBannerAdView.loadAd();
    }

    private void showFacebookInterstitialAds() {
        if (facebookInterstitialAd == null || !facebookInterstitialAd.isAdLoaded()) {
            return;
        }
        if (facebookInterstitialAd.isAdInvalidated()) {
            return;
        }
        facebookInterstitialAd.show();
    }

    private void initMoPubInterstitialAds() {
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(Constants.moPubInterstitialAdsCode)
                .withLogLevel(MoPubLog.LogLevel.NONE)
                .build();
        MoPub.initializeSdk(this, sdkConfiguration, () -> {
            Log.d("Mopub", "SDK initialized");
            moPubInterstitial = new MoPubInterstitial(WebViewActivity.this, Constants.moPubInterstitialAdsCode);
            moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                @Override
                public void onInterstitialLoaded(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                    Toast.makeText(WebViewActivity.this, "ads loading failed for " + errorCode.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onInterstitialShown(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialClicked(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                    Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
                }
            });
            moPubInterstitial.load();
            initMoPubBannerAds();
        });
    }

    private void initMoPubBannerAds() {
        moPubBannerAdsView = (MoPubView) findViewById(R.id.webViewActivityMoPubBannerAdsContainerId);
        moPubBannerAdsView.setVisibility(View.VISIBLE);
        moPubBannerAdsView.setAdUnitId(Constants.moPubBannerAdsCode);
        moPubBannerAdsView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {

            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {

            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {

            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {

            }
        });
        moPubBannerAdsView.setAutorefreshEnabled(true);
        moPubBannerAdsView.loadAd();
    }

    private void initUnityInterstitialAds() {
        UnityAds.initialize(WebViewActivity.this, Constants.unityAdsGameId, Constants.unityAdTestMode);
        UnityAds.addListener(new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {
                if (unityBannerView == null && UnityAds.isInitialized()) {
                    initUnityBannerAds();
                }
            }

            @Override
            public void onUnityAdsStart(String s) {

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
//                Toast.makeText(WebViewActivity.this, "ads loading failed for " + unityAdsError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        UnityAds.load(Constants.unityInterstitialAdsCode);
    }

    private void initUnityBannerAds() {
        UnityBanners.setBannerListener(new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {
                unityBannerView = view;
                ViewGroup viewGroup = ((ViewGroup) findViewById(R.id.webViewActivityBannerAdsContainerId));
                viewGroup.removeAllViews();
                viewGroup.addView(view);
            }

            @Override
            public void onUnityBannerUnloaded(String s) {
                unityBannerView = null;
            }

            @Override
            public void onUnityBannerShow(String s) {
                Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }

            @Override
            public void onUnityBannerClick(String s) {

            }

            @Override
            public void onUnityBannerHide(String s) {

            }

            @Override
            public void onUnityBannerError(String s) {
                unityBannerView = null;
//                Toast.makeText(WebViewActivity.this, "banner loaded failed for " + s, Toast.LENGTH_LONG).show();
            }
        });
        UnityBanners.destroy();
        if (unityBannerView == null) {
            UnityBanners.setBannerPosition(BannerPosition.BOTTOM_CENTER);
            UnityBanners.loadBanner(WebViewActivity.this, Constants.unityBannerAdsCode);
        }
    }

    private void initAdmobInterstitialAd() {
        admobInsterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        admobInsterstitialAd.setAdUnitId(Constants.adMobInterstitialAdsCode);
        admobInsterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }
        });
        admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void initAdMobBannerAds() {
        admobBannerAdsView = new com.google.android.gms.ads.AdView(WebViewActivity.this);
        admobBannerAdsView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobBannerAdsView.setAdUnitId(Constants.admobBannerAdsCode);
        RelativeLayout rootLayout = findViewById(R.id.webViewActivityBannerAdsContainerId);
        rootLayout.removeAllViews();
        rootLayout.addView(admobBannerAdsView);
        admobBannerAdsView.loadAd(new AdRequest.Builder().build());
    }

    public void showInterstitialAds() {
        if (Constants.platformName.equalsIgnoreCase("mopub")) {
            if (moPubInterstitial.isReady()) {
                moPubInterstitial.show();
            }
        } else if (Constants.platformName.equalsIgnoreCase("unity")) {
            if (UnityAds.isReady(Constants.unityInterstitialAdsCode)) {
                UnityAds.show(WebViewActivity.this, Constants.unityInterstitialAdsCode);
            }
        } else if (Constants.platformName.equalsIgnoreCase("admob")) {
            if (admobInsterstitialAd.isLoaded()) {
                admobInsterstitialAd.show();
            }
        }
    }




//    private void inflateFacebookNativeBannerAd(NativeBannerAd nativeBannerAd) {
//        // Unregister last ad
//        nativeBannerAd.unregisterView();
//
//        // Add the Ad view into the ad container.
//        facebookNativeBannerAdLayout = findViewById(R.id.webViewActivityFacebookNativeBannerAdsContainerId);
//        LayoutInflater inflater = LayoutInflater.from(WebViewActivity.this);
//        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
//        facebookNativeBannerAdView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_unit, facebookNativeBannerAdLayout, false);
//        facebookNativeBannerAdLayout.addView(facebookNativeBannerAdView);
//
//        // Add the AdChoices icon
//        RelativeLayout adChoicesContainer = facebookNativeBannerAdView.findViewById(R.id.ad_choices_container);
//        AdOptionsView adOptionsView = new AdOptionsView(WebViewActivity.this, nativeBannerAd, facebookNativeBannerAdLayout);
//        adChoicesContainer.removeAllViews();
//        adChoicesContainer.addView(adOptionsView, 0);
//
//        // Create native UI using the ad metadata.
//        TextView nativeAdTitle = facebookNativeBannerAdView.findViewById(R.id.native_ad_title);
//        TextView nativeAdSocialContext = facebookNativeBannerAdView.findViewById(R.id.native_ad_social_context);
//        TextView sponsoredLabel = facebookNativeBannerAdView.findViewById(R.id.native_ad_sponsored_label);
//        AdIconView nativeAdIconView = facebookNativeBannerAdView.findViewById(R.id.native_icon_view);
//        Button nativeAdCallToAction = facebookNativeBannerAdView.findViewById(R.id.native_ad_call_to_action);
//
//        // Set the Text.
//        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
//        nativeAdCallToAction.setVisibility(
//                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
//        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
//        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());
//
//        // Register the Title and CTA button to listen for clicks.
//        List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(nativeAdTitle);
//        clickableViews.add(nativeAdCallToAction);
//        nativeBannerAd.registerViewForInteraction(facebookNativeBannerAdView, nativeAdIconView, clickableViews);
//    }


    private void getCurrentIpInfo() {
        String lastLoadedTime=Utils.retrieveStringFromStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey);
        if (lastLoadedTime!=null) {
            String diff=Utils.getTimeDifBetweenToTime(lastLoadedTime,Utils.getCurrentTime());
            if (diff!=null) {
                timeDiff=Integer.parseInt(diff);
            }
        } else {
            timeDiff=1;
        }
        ApiInterface apiInterface= RetrofitClient.getApiClient().create(ApiInterface.class);
        Call<JsonElement> call = apiInterface.getIpInfo();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            JSONObject rootObject=new JSONObject(response.body().toString());
                            currentCountryName=rootObject.getString("country");
                            if (currentCountryName.equalsIgnoreCase("bangladesh") && timeDiff>=1) {
                                if (willLoadFirstInterstitialAds && willLoadFirstBannerAds) {
                                    willLoadFirstInterstitialAds=false;
                                    willLoadFirstBannerAds=false;
                                    if (Constants.platformName.equalsIgnoreCase("facebook")) {
                                        initFacebookBannerAdView();
                                        willLoadFirstInterstitialAds=true;
                                    } else if (Constants.platformName.equalsIgnoreCase("mopub")) {
                                        initMoPubInterstitialAds();
                                    } else if (Constants.platformName.equalsIgnoreCase("unity")) {
                                        initUnityInterstitialAds();
                                    } else if (Constants.platformName.equalsIgnoreCase("admob")) {
                                        initAdmobInterstitialAd();
                                        initAdMobBannerAds();
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    private void initializeDownloadManager() {
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String currentUserAgent, String currentContentDisposition, String currentMimeType, long contentLength) {
                downloadUrl = url;
                userAgent = currentUserAgent;
                contentDisposition = currentContentDisposition;
                mimeType = currentMimeType;

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermission(WebViewActivity.this, permissions)) {
                        showPermissionAlertDialog();
                    } else {
//                        if (admobInterstitialAds.isLoaded()) {
//                            admobInterstitialAds.show();
//                        } else {
                            startDownload();
//                        }
                    }
                } else {
//                    if (admobInterstitialAds.isLoaded()) {
//                        admobInterstitialAds.show();
//                    } else {
                        startDownload();
//                    }
                }
            }
        });
    }

    private void showPermissionAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(WebViewActivity.this)
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.read_write_permission_details))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int PERMISSION_ALL = 1;
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(WebViewActivity.this, permissions, PERMISSION_ALL);
                    }
                });
        permissionAlertDialog=builder.create();
        if (!isFinishing()){
            permissionAlertDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                Toast.makeText(this, "Permission denied, Please accept permission to download this file in your storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Utils.haveInternet(getApplicationContext())) {
                    Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                    intent.putExtra("url", webView.getUrl());
                    startActivity(intent);
                    finish();
                } else {
                    webView.reload();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initializeAll() {
        databaseReference= FirebaseDatabase.getInstance().getReference();

        webView = findViewById(R.id.myWebViewId);
        swipeRefreshLayout = findViewById(R.id.webViewActivitySwipeRefreshLayoutId);
        progressBar = findViewById(R.id.webActivityProgressBarId);
        centerProgressBar = findViewById(R.id.spin_kit);
        Sprite fadingCircle = new FadingCircle();
        centerProgressBar.setIndeterminateDrawable(fadingCircle);
        centerProgressBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        addToFavouriteTextView = findViewById(R.id.addToFavouriteListTextViewId);

        initCustomProgressBar();
    }

    public void openWebPage(String myUrl) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        webView.setLongClickable(false);
//        webView.setHapticFeedbackEnabled(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        if (myUrl.contains("https://play.google.com/")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(myUrl));
            startActivity(intent);
            finish();
        } else {
            webView.loadUrl(myUrl);
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        @Override
        public void onHideCustomView() {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = view;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = callback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result)
        {
            Log.e("alert triggered", message);
            return true;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
//            WebView newWebView = new WebView(view.getContext());
//            newWebView.setWebViewClient(new MyWebViewClient());

//            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//            transport.setWebView(webView);
//            resultMsg.sendToTarget();

            WebView.HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            chooseBrowserToOpenUrl(data);

            return false;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {

                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;
        }
    }

    private void chooseBrowserToOpenUrl(String data) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
        startActivity(Intent.createChooser(browserIntent,"Please choose a browser."));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    class MyWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(".html") && timeDiff>=1) {
                showInterstitialAds();
            }

            if (url.contains("play.google.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            } else if (url.contains("facebook.com")) {
                return openFacebookApp(url);
            } else {
                return false;
            }
        }

        @RequiresApi(24)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri url = request.getUrl();
            if (url.toString().contains(".html") && timeDiff>=1) {
                showInterstitialAds();
            }

            if (url.toString().contains("play.google.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(url);
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            } else if (url.toString().contains("facebook.com")) {
                return openFacebookApp(url.toString());
            } else {
                return false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            centerProgressBar.setVisibility(View.GONE);
            currentUrl = webView.getUrl();
            currentPageTitle = view.getTitle();
            exitStatus = true;
            if (willLoadFirstInterstitialAds && Constants.platformName.equalsIgnoreCase("facebook") && timeDiff>=1){
                initFacebookInterstitialAds();
                willLoadFirstInterstitialAds=false;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            centerProgressBar.setVisibility(View.VISIBLE);
            exitStatus = false;
            if (!Utils.haveInternet(WebViewActivity.this)) {
                try {
                    webView.stopLoading();
                } catch (Exception e) {

                }
                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                startActivity(intent);
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (error.getErrorCode() == ERROR_CONNECT || error.getErrorCode() == ERROR_TIMEOUT || error.getErrorCode() == ERROR_BAD_URL || error.getErrorCode() == ERROR_IO || error.getErrorCode() == ERROR_PROXY_AUTHENTICATION || error.getErrorCode() == ERROR_UNSAFE_RESOURCE || error.getErrorCode() == ERROR_UNSUPPORTED_AUTH_SCHEME || error.getErrorCode() == ERROR_UNSUPPORTED_SCHEME || error.getErrorCode() == SAFE_BROWSING_THREAT_UNKNOWN || error.getErrorCode() == ERROR_AUTHENTICATION) {
//                ERROR_HOST_LOOKUP removed temporary.
                try {
                    webView.stopLoading();
                } catch (Exception e) {

                }
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                Log.d(Constants.TAG,"error is:- "+error.getErrorCode());
                exitStatus = true;
                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT || errorCode == ERROR_BAD_URL || errorCode == ERROR_IO || errorCode == ERROR_PROXY_AUTHENTICATION || errorCode == ERROR_UNSAFE_RESOURCE || errorCode == ERROR_UNSUPPORTED_AUTH_SCHEME || errorCode == ERROR_UNSUPPORTED_SCHEME || errorCode == SAFE_BROWSING_THREAT_UNKNOWN || errorCode == ERROR_AUTHENTICATION) {
//                ERROR_HOST_LOOKUP removed temporary.
                try {
                    webView.stopLoading();
                } catch (Exception e) {

                }
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                Log.d(Constants.TAG,"error code is:- "+errorCode);
                exitStatus = true;
                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }
        }
    }

    private boolean openFacebookApp(String url) {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info=pm.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
            int versionCode = info.versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                url= "fb://facewebmodal/f?href=" + url;
            }
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setPackage("com.facebook.katana");
            intent.setData(Uri.parse(url));
            startActivity(Intent.createChooser(intent,"Please select a browser"));
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            try {
                PackageManager pm = getPackageManager();
                PackageInfo info=pm.getPackageInfo("com.facebook.lite", PackageManager.GET_ACTIVITIES);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setPackage("com.facebook.lite");
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            catch (PackageManager.NameNotFoundException ex) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException exc) {
                    return false;
                }
            }
        }
    }

    private void bookmarkAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("ফেভারিট লিস্ট এ অ্যাড করুন")
                .setMessage("আপনি কি এই পেজটি আপনার ফেভারিট লিস্ট এ যোগ করতে চান ?")
                .setCancelable(false)
                .setNegativeButton("না", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (currentPageTitle != null && currentUrl != null) {
                            BookmarkAsyncTask bookmarkAsyncTask = new BookmarkAsyncTask();
                            bookmarkAsyncTask.execute();
                        } else {
                            Toast.makeText(WebViewActivity.this, "Current page is not loading successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    class BookmarkAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Page page = new Page();
            page.setTitle(currentPageTitle);
            page.setUrl(currentUrl);
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .pageDao()
                    .insert(page);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(WebViewActivity.this, "সফলভাবে যোগ হয়েছে", Toast.LENGTH_SHORT).show();
        }
    }

    private void initBroadCastReceiver() {
        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    Toast.makeText(WebViewActivity.this, "ডাউনলোড শেষ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void startDownload() {
        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadUrl));
        if (Build.VERSION.SDK_INT < 29) {
            downloadRequest.allowScanningByMediaScanner();
        }
        downloadRequest.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(downloadUrl);
        downloadRequest.addRequestHeader("cookie", cookies);
        downloadRequest.addRequestHeader("User-Agent", userAgent);
//        downloadRequest.setTitle(URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType));
        downloadRequest.setTitle("Jobs Bd");
        String guessFileName = URLUtil.guessFileName(null, "JobsBd", mimeType);
        String fileName = guessFileName.replace("downloadfile", "JobsBd" + System.currentTimeMillis());
//        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(downloadRequest);
            Toast.makeText(WebViewActivity.this, "ডাউনলোড হচ্ছে…", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initCustomProgressBar() {
        View view = getLayoutInflater().inflate(R.layout.custom_progress_bar, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view);
        progressAlertDialog = builder.create();
    }

    private void showHideCustomProgressBar(boolean command) {
        if (command && !isFinishing()) {
            progressAlertDialog.show();
        } else {
            progressAlertDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

    @Override
    public void onBackPressed() {
        if (exitStatus) {
            if (isTaskRoot()) {
                Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            Toast.makeText(this, "অনুগ্রহ করে পেজ লোড শেষ হওয়া পর্যন্ত অপেক্ষা করুন|", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        if (admobBannerAdsView != null) {
            admobBannerAdsView.pause();
        }
        super.onPause();
        MoPub.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MoPub.onStop(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(onDownloadComplete);
        if (adsThread != null) {
            adsThread.interrupt();
            adsThread = null;
        }
        if (facebookInterstitialAd != null) {
            facebookInterstitialAd.destroy();
            facebookInterstitialAd=null;
        }
        if (facebookBannerAdView!=null){
            facebookBannerAdView.destroy();
            facebookBannerAdView=null;
        }
        if (moPubInterstitial != null) {
            moPubInterstitial.destroy();
            moPubInterstitial = null;
        }
        if (moPubBannerAdsView != null) {
            moPubBannerAdsView.destroy();
            moPubBannerAdsView = null;
        }
        if (unityBannerView != null) {
            UnityBanners.destroy();
            unityBannerView = null;
        }
        if (admobBannerAdsView != null) {
            admobBannerAdsView.destroy();
            admobBannerAdsView=null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoPub.onResume(this);
        if (admobBannerAdsView != null) {
            admobBannerAdsView.resume();
        }
        if (!Utils.haveInternet(getApplicationContext())) {
            Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
            startActivity(intent);
        } else {
            getCurrentIpInfo();
        }
    }
}
