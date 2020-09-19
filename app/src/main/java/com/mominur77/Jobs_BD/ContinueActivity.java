package com.mominur77.Jobs_BD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import com.mominur77.Jobs_BD.database_connection.ApiInterface;
import com.mominur77.Jobs_BD.database_connection.RetrofitClient;
import com.mominur77.Jobs_BD.model.continuepage.ImageNotice;
import com.mominur77.Jobs_BD.model.continuepage.StringNotice;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.UnityBanners;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContinueActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView continueCardView;
    private ProgressBar progressBar;
    private LinearLayout welcomeRoot, mainViewRoot;
    private ContinueThread continueThread;
    private int counter = 0, timeDiff=0;
    private String url, currentCountryName;
    private ImageView adminNoticeImageView;
    private TextView adminNoticeTextView;
    private DatabaseReference databaseReference;
    private ImageNotice imageNotice;
    private StringNotice stringNotice;
    private InterstitialAd admobInsterstitialAd;
    private MoPubInterstitial moPubInterstitial;
    private boolean willLoadFirstInterstitialAds=true, willLoadFirstBannerAds=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue);


        getNotificationUrl();

        initAll();

        initThread();

        welcomeRoot.setVisibility(View.VISIBLE);
        mainViewRoot.setVisibility(View.GONE);


        loadAdminImageNotice();

        loadAdminStringNotice();

        showAdminNoticeImage();

    }


    private void initMoPubInterstitialAds() {
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(Constants.moPubInterstitialAdsCode)
                .withLogLevel(MoPubLog.LogLevel.NONE)
                .build();
        MoPub.initializeSdk(this, sdkConfiguration, () -> {
            moPubInterstitial = new MoPubInterstitial(ContinueActivity.this, Constants.moPubInterstitialAdsCode);
            moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                @Override
                public void onInterstitialLoaded(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                    Toast.makeText(ContinueActivity.this, "ads loading failed for " + errorCode.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onInterstitialShown(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialClicked(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                    getNotificationUrl();
                    gotoWebViewActivity();
                    Utils.saveStringToStorage(ContinueActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
                }
            });
            moPubInterstitial.load();
        });
    }

    private void initUnityInterstitialAds() {
        UnityAds.initialize(ContinueActivity.this, Constants.unityAdsGameId, Constants.unityAdTestMode);
        UnityAds.addListener(new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {

            }

            @Override
            public void onUnityAdsStart(String s) {

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                getNotificationUrl();
                gotoWebViewActivity();
                Utils.saveStringToStorage(ContinueActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
//                Toast.makeText(WebViewActivity.this, "ads loading failed for " + unityAdsError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        UnityAds.load(Constants.unityInterstitialAdsCode);
    }

    private void initAdmobInterstitialAd() {
        admobInsterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        admobInsterstitialAd.setAdUnitId(Constants.adMobInterstitialAdsCode);
        admobInsterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                getNotificationUrl();
                gotoWebViewActivity();
                Utils.saveStringToStorage(ContinueActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
            }
        });
        admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void showInterstitialAds() {
        if (Constants.platformName.equalsIgnoreCase("mopub")) {
            if (moPubInterstitial.isReady()) {
                moPubInterstitial.show();
            }
        } else if (Constants.platformName.equalsIgnoreCase("unity")) {
            if (UnityAds.isReady(Constants.unityInterstitialAdsCode)) {
                UnityAds.show(ContinueActivity.this, Constants.unityInterstitialAdsCode);
            }
        } else if (Constants.platformName.equalsIgnoreCase("admob")) {
            if (admobInsterstitialAd.isLoaded()) {
                admobInsterstitialAd.show();
            }
        }
    }

    private void getCurrentIpInfo() {
        String lastLoadedTime=Utils.retrieveStringFromStorage(ContinueActivity.this,Constants.lastAdsLoadedTimeKey);
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
                                    if (Constants.platformName.equalsIgnoreCase("mopub")) {
                                        initMoPubInterstitialAds();
                                    } else if (Constants.platformName.equalsIgnoreCase("unity")) {
                                        initUnityInterstitialAds();
                                    } else if (Constants.platformName.equalsIgnoreCase("admob")) {
                                        initAdmobInterstitialAd();
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

    private void getNotificationUrl() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("Url");
        }
    }

    private void loadAdminImageNotice() {
        databaseReference.child("notice").child("continue_page_notice").child("image_notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ImageNotice> imageNoticeList = new ArrayList<>();
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        imageNoticeList.add(snapshot.getValue(ImageNotice.class));
                    }
                }
                if (imageNoticeList.size() > 0) {
                    saveImageUrlToStorage(imageNoticeList);
                    Log.d(Constants.TAG, "image link tracked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Constants.TAG, "image link not tracked for " + databaseError.getMessage());
            }
        });
    }

    private void saveImageUrlToStorage(List<ImageNotice> list) {
        Utils.saveStringToStorage(ContinueActivity.this, Constants.imageOneUrlKey, list.get(0).getImageUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.targetOneUrlKey, list.get(0).getTargetUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.imageTwoUrlKey, list.get(1).getImageUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.targetTwoUrlKey, list.get(1).getTargetUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.imageThreeUrlKey, list.get(2).getImageUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.targetThreeUrlKey, list.get(2).getTargetUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.imageFourUrlKey, list.get(3).getImageUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.targetFourUrlKey, list.get(3).getTargetUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.imageFiveUrlKey, list.get(4).getImageUrl());
        Utils.saveStringToStorage(ContinueActivity.this, Constants.targetFiveUrlKey, list.get(4).getTargetUrl());
    }

    private void showAdminNoticeImage() {
        imageNotice = new ImageNotice();
        Random random = new Random();
        int value = random.nextInt(5);
        if (value == 0) {
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.imageOneUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.targetOneUrlKey));
        } else if (value == 1) {
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.imageTwoUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.targetTwoUrlKey));
        } else if (value == 2) {
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.imageThreeUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.targetThreeUrlKey));
        } else if (value == 3) {
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.imageFourUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.targetFourUrlKey));
        } else if (value == 4) {
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.imageFiveUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(ContinueActivity.this, Constants.targetFiveUrlKey));
        }

        if (imageNotice.getImageUrl() != null) {
            adminNoticeImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(imageNotice.getImageUrl()).fit().into(adminNoticeImageView);
            Log.d(Constants.TAG, "trying to show image notice.");
        }
    }

    private void loadAdminStringNotice() {
        databaseReference.child("notice").child("continue_page_notice").child("string_notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    stringNotice = dataSnapshot.getValue(StringNotice.class);
                }
                if (stringNotice != null && stringNotice.getShortNotice() != null) {
                    adminNoticeTextView.setVisibility(View.VISIBLE);
                    adminNoticeTextView.setText(stringNotice.getShortNotice());
                }
                Log.d(Constants.TAG, "string tracked");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Constants.TAG, "string not tracked for " + databaseError.getMessage());
            }
        });
    }

    private void initThread() {
        if (continueThread != null) {
            continueThread.interrupt();
            continueThread = null;
        }
        counter = 0;
        continueThread = new ContinueThread();
        continueThread.start();
    }

    private void initAll() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        continueCardView = findViewById(R.id.continueActivityContinueCardViewId);
        progressBar = findViewById(R.id.continueActivityProgressBarId);
        welcomeRoot = findViewById(R.id.continueActivityWelcomeRootLayoutId);
        mainViewRoot = findViewById(R.id.continueActivityMainViewRootId);
        adminNoticeTextView = findViewById(R.id.continueActivityAdminNoticeTextViewId);
        adminNoticeImageView = findViewById(R.id.continueActivityAdminNoticeImageViewId);

        adminNoticeTextView.setOnClickListener(this);
        adminNoticeImageView.setOnClickListener(this);
        continueCardView.setOnClickListener(this);

    }

    private void gotoWebViewActivity() {
        Intent intent = new Intent(ContinueActivity.this, WebViewActivity.class);
        intent.putExtra("Url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("ব্যাক বাটন ক্লিক করা হয়েছে")
                .setIcon(R.drawable.ic_warning_red)
                .setMessage("আপনি কি এই এ্যাপ হতে বাহির হতে চাচ্ছেন?")
                .setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("না", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueActivityContinueCardViewId:
                if (timeDiff>=1) {
                    showInterstitialAds();
                } else {
                    getNotificationUrl();
                    gotoWebViewActivity();
                }
                break;

            case R.id.continueActivityAdminNoticeTextViewId:
                if (stringNotice != null && stringNotice.getTargetUrl() != null) {
                    url = stringNotice.getTargetUrl();
                    gotoWebViewActivity();
                } else {
                    Toast.makeText(this, "Target url not detected yet.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.continueActivityAdminNoticeImageViewId:
                if (imageNotice != null && imageNotice.getTargetUrl() != null) {
                    url = imageNotice.getTargetUrl();
                    gotoWebViewActivity();
                } else {
                    Toast.makeText(this, "Target url not detected yet.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class ContinueThread extends Thread {
        @Override
        public void run() {
            while (counter < 100 && !isInterrupted()) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(counter);
                    }
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    welcomeRoot.setVisibility(View.GONE);
                    mainViewRoot.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MoPub.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MoPub.onStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoPub.onResume(this);
        if (!Utils.haveInternet(getApplicationContext())) {
            Intent intent = new Intent(ContinueActivity.this, NoInternetActivity.class);
            startActivity(intent);
        } else {
            getCurrentIpInfo();
        }
    }

    @Override
    protected void onDestroy() {
        if (continueThread != null) {
            continueThread.interrupt();
            continueThread = null;
        }
        if (moPubInterstitial != null) {
            moPubInterstitial.destroy();
            moPubInterstitial = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showExitAlertDialog();
    }

}
