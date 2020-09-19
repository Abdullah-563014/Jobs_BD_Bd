package com.mominur77.Jobs_BD;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marquee.dingrui.marqueeviewlib.MarqueeView;
import com.mominur77.Jobs_BD.model.continuepage.ImageNotice;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwitchCompat governmentJobSwitch, nonGovernmentSwith, notificationSoundSwitch,breakingNewsSwitch;
    private Button
            advanceMenuButton,
            ageCalculatorButton,
            necessaryInformationButton,
            favouriteListButton,
            jororiProyojonaButton,
            giveFiveStarButton,
            jobLogoButton,
            studyButton,
            governmentJobButton,
            nonGovernmentJobButton,
            allButton,
            examAndResultButton,
            toEndButton,
            dateExpiredButton,
            closeAppButton,
            videoButton;
    private TextView adminInformationTextView, adminTitleTextView;
    private DatabaseReference databaseReference;
    private Class targetActivity;
    private String url;
    private ScrollView mainActivityRootLayout;
    private LinearLayout welcomeRootLayout,advanceMenuLayout;
    private ProgressBar welcomeProgressBar;
    private WelcomeThread welcomeThread;
    private int counter = 0;
//    private InterstitialAd admobInsterstitialAd;
//    private com.facebook.ads.InterstitialAd facebookInterstitialAd;
//    private boolean willLoadAdmobInterstitial=true;
    private RadioGroup menuSelectionRadioGroup;
    private MarqueeView adminNoticeMarqueeView;
    private ImageView adminNoticeImageView;
    private ImageNotice imageNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAll();

        checkUpdate();

        navigationView.setNavigationItemSelectedListener(this);

        handleSwitchListener();

        retrieveSwitchStatus();

        initAdminTitle();

        startWelcomeProgress();

        initAdminNoticeMarqueeView();

        loadAdminImageNotice();

        showAdminNoticeImage();

//        initAdmobInterstitialAd();

//        initFacebookInterstitialAds();

        if (Utils.retrieveBooleanFromStorage(getApplicationContext(),"IsEasyMenu")){
            setUpEasyMenu();
            menuSelectionRadioGroup.check(R.id.mainActivityEasyMenuRadioButtonId);
        }else {
            setUpAdvanceMenu();
            menuSelectionRadioGroup.check(R.id.mainActivityAdvanceMenuRadioButtonId);
        }

        menuSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.mainActivityAdvanceMenuRadioButtonId:
                        setUpAdvanceMenu();
                        break;

                    case R.id.mainActivityEasyMenuRadioButtonId:
                        setUpEasyMenu();
                        break;
                }
            }
        });



    }


    private void loadAdminImageNotice() {
        databaseReference.child("adminMessage").child("image_notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ImageNotice> imageNoticeList=new ArrayList<>();
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        imageNoticeList.add(snapshot.getValue(ImageNotice.class));
                    }
                }
                if (imageNoticeList.size()>0){
                    saveImageUrlToStorage(imageNoticeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveImageUrlToStorage(List<ImageNotice> list) {
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeImageOneUrlKey,list.get(0).getImageUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeTargetOneUrlKey,list.get(0).getTargetUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeImageTwoUrlKey,list.get(1).getImageUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeTargetTwoUrlKey,list.get(1).getTargetUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeImageThreeUrlKey,list.get(2).getImageUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeTargetThreeUrlKey,list.get(2).getTargetUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeImageFourUrlKey,list.get(3).getImageUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeTargetFourUrlKey,list.get(3).getTargetUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeImageFiveUrlKey,list.get(4).getImageUrl());
        Utils.saveStringToStorage(MainActivity.this,Constants.adminNoticeTargetFiveUrlKey,list.get(4).getTargetUrl());
    }

    private void showAdminNoticeImage() {
        imageNotice =new ImageNotice();
        Random random=new Random();
        int value=random.nextInt(5);
        if (value==0){
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeImageOneUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeTargetOneUrlKey));
        }else if (value==1){
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeImageTwoUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeTargetTwoUrlKey));
        }else if (value==2){
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeImageThreeUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeTargetThreeUrlKey));
        }else if (value==3){
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeImageFourUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeTargetFourUrlKey));
        }else if (value==4){
            imageNotice.setImageUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeImageFiveUrlKey));
            imageNotice.setTargetUrl(Utils.retrieveStringFromStorage(MainActivity.this,Constants.adminNoticeTargetFiveUrlKey));
        }

        if (imageNotice.getImageUrl()!=null){
            adminNoticeImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(imageNotice.getImageUrl()).error(R.drawable.new_logo).transform(new Utils.PicassoTransform(getDisplayWidthInPixel())).into(adminNoticeImageView);
        }else {
            Picasso.get().load(R.drawable.new_logo).into(adminNoticeImageView);
        }
    }

    private int getDisplayWidthInPixel() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void initAdminNoticeMarqueeView() {
        String temporaryNotice="এপটি ব্যবহারে কোনো ধরণের সমস্যা বা জটিলতা মনে হলে অথবা, কোনো পরামর্শ থাকলে আমাদেরকে লিখে জানান, এই ইমেইলে easysoft247@gmail.com অথবা, এই 01303 628 419 নাম্বারে। হোম পেজের বাটনগুলোতে, এজ ক্যালকুলেটরের বয়স বের করার সময় ও ডাউনলোডের সময় আমাদের স্পন্সরদের এড আসতে পারে, এডে ক্লিক করতে না চাইলে কিছুক্ষণ পরে ক্রস চিহ্নতে টাচ করে এডটি কেটে দিন। অনুগ্রহ করে বিরক্ত না হয়ে আমাদেরকে সহযোগীতা করুন।";
        adminNoticeMarqueeView.setContent(temporaryNotice);
    }

    private void setUpAdvanceMenu() {
        advanceMenuLayout.setVisibility(View.VISIBLE);
        Utils.saveBooleanToStorage(getApplicationContext(),"IsEasyMenu",false);
    }

    private void setUpEasyMenu() {
        advanceMenuLayout.setVisibility(View.GONE);
        Utils.saveBooleanToStorage(getApplicationContext(),"IsEasyMenu",true);
    }

    private void startWelcomeProgress() {
        if (welcomeThread != null) {
            welcomeThread.interrupt();
            welcomeThread = null;
        }
        counter = 0;
        welcomeThread = new WelcomeThread();
        welcomeThread.start();
    }

    private void initAdminTitle() {
        databaseReference.child("adminMessage").child("title")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            adminNoticeMarqueeView.setContent(dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        adminNoticeMarqueeView.setContent("ইন্টারনেট সংযোগ / স্পিড চেক করুন");
                    }
                });
    }

    private void initializeAll() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        advanceMenuButton = findViewById(R.id.advanceMenuButtonId);
        ageCalculatorButton = findViewById(R.id.ageCalculatorMenuButtonId);
        governmentJobSwitch = findViewById(R.id.governmentCircularSwitchId);
        nonGovernmentSwith = findViewById(R.id.noGovernmentCircularSwitchId);
        notificationSoundSwitch = findViewById(R.id.notificationSoundSwitchId);
//        breakingNewsSwitch = findViewById(R.id.breakingNewsSwitchId);
        necessaryInformationButton = findViewById(R.id.necessaryInformationButtonId);
        favouriteListButton = findViewById(R.id.favouritListButtonId);
        adminNoticeImageView=findViewById(R.id.mainActivityAdminNoticeImageViewId);
        jororiProyojonaButton = findViewById(R.id.jororiProyojonaButtonId);
        giveFiveStarButton = findViewById(R.id.giveFiveStarButtonId);
        jobLogoButton = findViewById(R.id.jobLogoButtonId);
        studyButton = findViewById(R.id.studyButtonId);
        governmentJobButton = findViewById(R.id.governmentButtonId);
        nonGovernmentJobButton = findViewById(R.id.nonGovernmentButtonId);
        allButton = findViewById(R.id.allButtonId);
        examAndResultButton = findViewById(R.id.examAndResultButtonId);
        toEndButton = findViewById(R.id.toEndButtonId);
        dateExpiredButton = findViewById(R.id.expiredDateButtonId);
        closeAppButton = findViewById(R.id.mainActivityCloseApplicationButtonId);
        advanceMenuLayout=findViewById(R.id.mainActivityAdvanceMenuLayoutId);
        menuSelectionRadioGroup=findViewById(R.id.mainActivityMenuSelectionRadioGroupId);
        adminNoticeMarqueeView=findViewById(R.id.mainActivityAdminNoticeMarqueeViewId);
        videoButton=findViewById(R.id.videoButtonId);

        advanceMenuButton.setOnClickListener(this);
        ageCalculatorButton.setOnClickListener(this);
        necessaryInformationButton.setOnClickListener(this);
        favouriteListButton.setOnClickListener(this);
        jororiProyojonaButton.setOnClickListener(this);
        giveFiveStarButton.setOnClickListener(this);
        jobLogoButton.setOnClickListener(this);
        studyButton.setOnClickListener(this);
        governmentJobButton.setOnClickListener(this);
        nonGovernmentJobButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
        examAndResultButton.setOnClickListener(this);
        toEndButton.setOnClickListener(this);
        dateExpiredButton.setOnClickListener(this);
        closeAppButton.setOnClickListener(this);
        adminNoticeImageView.setOnClickListener(this);
        videoButton.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        mainActivityRootLayout = findViewById(R.id.mainActivityRootLayoutId);
        welcomeRootLayout = findViewById(R.id.welcomeLayoutRootLayoutId);
        welcomeProgressBar = findViewById(R.id.welcomeLayoutProgressBarId);
        mainActivityRootLayout.setVisibility(View.GONE);
        welcomeRootLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showExitAlertDialog();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;

            case R.id.nav_jogajog:
                intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_notice_board:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%A8%E0%A7%8B%E0%A6%9F%E0%A6%BF%E0%A6%B6%20%E0%A6%AC%E0%A7%8B%E0%A6%B0%E0%A7%8D%E0%A6%A1";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;
            case R.id.nav_exam_date:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AA%E0%A6%B0%E0%A7%80%E0%A6%95%E0%A7%8D%E0%A6%B7%E0%A6%BE";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;
            case R.id.nav_result:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AB%E0%A6%B2%E0%A6%BE%E0%A6%AB%E0%A6%B2";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;
            case R.id.nav_model_test:
                url = "https://ioiyrtyuicvbmnsdfgiopkvxdew.blogspot.com/2020/04/model-test-home.html";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_something_more:
                url = "https://ryojgfdscvbkjgfwwqsokjgfdzcvbbngffd.blogspot.com/2020/04/something-more-home-page.html";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;


            case R.id.nav_shamprotik_tottho:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%B8%E0%A6%BE%E0%A6%AE%E0%A7%8D%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%A4%E0%A6%BF%E0%A6%95%20%E0%A6%A4%E0%A6%A5%E0%A7%8D%E0%A6%AF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_songram_o_sofolota:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%B8%E0%A6%82%E0%A6%97%E0%A7%8D%E0%A6%B0%E0%A6%BE%E0%A6%AE%20%E0%A6%93%20%E0%A6%B8%E0%A6%AB%E0%A6%B2%E0%A6%A4%E0%A6%BE";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_onubad_chorcha:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%85%E0%A6%A8%E0%A7%81%E0%A6%AC%E0%A6%BE%E0%A6%A6%20%E0%A6%9A%E0%A6%B0%E0%A7%8D%E0%A6%9A%E0%A6%BE";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_prosno_bank:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%B6%E0%A7%8D%E0%A6%A8%20%E0%A6%AC%E0%A7%8D%E0%A6%AF%E0%A6%BE%E0%A6%82%E0%A6%95";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_interview_tips:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%87%E0%A6%A8%E0%A7%8D%E0%A6%9F%E0%A6%BE%E0%A6%B0%E0%A6%AD%E0%A6%BF%E0%A6%89%20%E0%A6%9F%E0%A6%BF%E0%A6%AA%E0%A7%8D%E0%A6%B8";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_viva_ovigghota:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AD%E0%A6%BE%E0%A6%87%E0%A6%AD%E0%A6%BE%20%E0%A6%85%E0%A6%AD%E0%A6%BF%E0%A6%9C%E0%A7%8D%E0%A6%9E%E0%A6%A4%E0%A6%BE";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_bcs_prosno_o_shomadhan:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AC%E0%A6%BF%E0%A6%B8%E0%A6%BF%E0%A6%8F%E0%A6%B8%20%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%B6%E0%A7%8D%E0%A6%A8%20%E0%A6%93%20%E0%A6%B8%E0%A6%AE%E0%A6%BE%E0%A6%A7%E0%A6%BE%E0%A6%A8";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_osthom_sreny_pasha_chakri:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A7%AE%E0%A6%AE%20%E0%A6%B6%E0%A7%8D%E0%A6%B0%E0%A7%87%E0%A6%A3%E0%A6%BF%20%E0%A6%AA%E0%A6%BE%E0%A6%B6%E0%A7%87%20%E0%A6%9A%E0%A6%BE%E0%A6%95%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_ssc_pash_a_chakri:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/SSC%20%E0%A6%AA%E0%A6%BE%E0%A6%B6%E0%A7%87%20%E0%A6%9A%E0%A6%BE%E0%A6%95%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_hsc_pash_a_chakri:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/HSC%20%E0%A6%AA%E0%A6%BE%E0%A6%B6%E0%A7%87%20%E0%A6%9A%E0%A6%BE%E0%A6%95%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_shorkari:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%B8%E0%A6%B0%E0%A6%95%E0%A6%BE%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_be_shorkari:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AC%E0%A7%87%E0%A6%B8%E0%A6%B0%E0%A6%95%E0%A6%BE%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_bank:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AC%E0%A7%8D%E0%A6%AF%E0%A6%BE%E0%A6%82%E0%A6%95";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_angio:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%8F%E0%A6%A8%E0%A6%9C%E0%A6%BF%E0%A6%93";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_shikkha_protisthan:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%B6%E0%A6%BF%E0%A6%95%E0%A7%8D%E0%A6%B7%E0%A6%BE%20%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%A4%E0%A6%BF%E0%A6%B7%E0%A7%8D%E0%A6%A0%E0%A6%BE%E0%A6%A8";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_sales_ba_marketing:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%B8%E0%A7%87%E0%A6%B2%E0%A7%8D%E0%A6%B8%20%2F%20%E0%A6%AE%E0%A6%BE%E0%A6%B0%E0%A7%8D%E0%A6%95%E0%A7%87%E0%A6%9F%E0%A6%BF%E0%A6%82";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_difence:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%A1%E0%A6%BF%E0%A6%AB%E0%A7%87%E0%A6%A8%E0%A7%8D%E0%A6%B8";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_onnonno_beshorkari:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%85%E0%A6%A8%E0%A7%8D%E0%A6%AF%E0%A6%BE%E0%A6%A8%E0%A7%8D%E0%A6%AF%20%E0%A6%AC%E0%A7%87%E0%A6%B8%E0%A6%B0%E0%A6%95%E0%A6%BE%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_chakrir_potrika:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%9A%E0%A6%BE%E0%A6%95%E0%A6%B0%E0%A6%BF%E0%A6%B0%20%E0%A6%AA%E0%A6%A4%E0%A7%8D%E0%A6%B0%E0%A6%BF%E0%A6%95%E0%A6%BE";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_proyojonio_tottho:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A7%9F%E0%A7%8B%E0%A6%9C%E0%A6%A8%E0%A7%80%E0%A7%9F%20%E0%A6%A4%E0%A6%A5%E0%A7%8D%E0%A6%AF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_ei_app_ti_jevaba_bebohar_korben:
                url = "https://appguidevideo.blogspot.com/2020/08/blog-post.html";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nav_shomossaw_hola:
                intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_aro_apps:
                url = "https://play.google.com/store/apps/developer?id=Easy+Soft+BD";
                targetActivity = WebViewActivity.class;
                intent = new Intent(MainActivity.this, targetActivity);
                intent.putExtra("Url", url);
                startActivity(intent);
                break;
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.advanceMenuButtonId:
                advanceMenuMethod();
                break;

            case R.id.ageCalculatorMenuButtonId:
                targetActivity=AgeCalculatorActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(false);
                break;

            case R.id.necessaryInformationButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A7%9F%E0%A7%8B%E0%A6%9C%E0%A6%A8%E0%A7%80%E0%A7%9F%20%E0%A6%A4%E0%A6%A5%E0%A7%8D%E0%A6%AF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.favouritListButtonId:
                targetActivity = FavouriteListActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(false);
                break;

            case R.id.jororiProyojonaButtonId:
                url = "https://play.google.com/store/apps/developer?id=Easy+Soft+BD";
                targetActivity = WebViewActivity.class;
                intent = new Intent(MainActivity.this, targetActivity);
                intent.putExtra("Url", url);
                startActivity(intent);
                break;

            case R.id.giveFiveStarButtonId:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("Url", "https://play.google.com/store/apps/details?id=com.mominur77.Jobs_BD");
                startActivity(intent);
                break;

            case R.id.jobLogoButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.studyButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AA%E0%A7%9C%E0%A6%BE%E0%A6%B2%E0%A7%87%E0%A6%96%E0%A6%BE";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.governmentButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%B8%E0%A6%B0%E0%A6%95%E0%A6%BE%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.nonGovernmentButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AC%E0%A7%87%E0%A6%B8%E0%A6%B0%E0%A6%95%E0%A6%BE%E0%A6%B0%E0%A6%BF";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.allButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.examAndResultButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AA%E0%A6%B0%E0%A7%80%E0%A6%95%E0%A7%8D%E0%A6%B7%E0%A6%BE%20%E0%A6%93%20%E0%A6%AB%E0%A6%B2%E0%A6%BE%E0%A6%AB%E0%A6%B2";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.toEndButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/2020/07/blog-post_794.html";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.expiredDateButtonId:
                url = "https://jhgrfsdcfghbopojnbhgfdrtedvkfghvnjhe.blogspot.com/search/label/%E0%A6%AE%E0%A7%87%E0%A7%9F%E0%A6%BE%E0%A6%A6%20%E0%A6%B6%E0%A7%87%E0%A6%B7";
                targetActivity = WebViewActivity.class;
//                if (admobInsterstitialAd.isLoaded()){
//                    admobInsterstitialAd.show();
//                }else {
//                    startTargetActivity();
//                }
//                showFacebookInterstitialAds();
                startTargetActivity(true);
                break;

            case R.id.mainActivityAdminNoticeImageViewId:
                if (imageNotice!=null && imageNotice.getTargetUrl()!=null){
                    url=imageNotice.getTargetUrl();
                    targetActivity=WebViewActivity.class;
                    startTargetActivity(false);
                }else {
                    Toast.makeText(this, "টার্গেট লিংক টি সনাক্ত করা যাচ্ছে না", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.videoButtonId:
                url="https://appguidevideo.blogspot.com/2020/08/blog-post.html";
                targetActivity=WebViewActivity.class;
                startTargetActivity(false);
                break;

            case R.id.mainActivityCloseApplicationButtonId:
                finishAffinity();
                break;
        }
    }

    private void handleSwitchListener() {
        governmentJobSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Utils.saveBooleanToStorage(getApplicationContext(), "GovernmentJobStatus", b);
            }
        });

        nonGovernmentSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Utils.saveBooleanToStorage(getApplicationContext(), "NonGovernmentJobStatus", b);
            }
        });

        notificationSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Utils.saveBooleanToStorage(getApplicationContext(), "NotificationSoundJobStatus", b);
            }
        });

//        breakingNewsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Utils.saveBooleanToStorage(getApplicationContext(), "BreakingNewsStatus", b);
//            }
//        });
    }

    private void retrieveSwitchStatus() {
        boolean governmentJobStatus = Utils.retrieveBooleanFromStorage(getApplicationContext(), "GovernmentJobStatus");
        boolean nonGovernmentJobStatus = Utils.retrieveBooleanFromStorage(getApplicationContext(), "NonGovernmentJobStatus");
        boolean notificationSoundStatus = Utils.retrieveBooleanFromStorage(getApplicationContext(), "NotificationSoundJobStatus");
//        boolean breakingNewsStatus = Utils.retrieveBooleanFromStorage(getApplicationContext(), "BreakingNewsStatus");

        governmentJobSwitch.setChecked(governmentJobStatus);
        nonGovernmentSwith.setChecked(nonGovernmentJobStatus);
        notificationSoundSwitch.setChecked(notificationSoundStatus);
//        breakingNewsSwitch.setChecked(breakingNewsStatus);
    }

    private void advanceMenuMethod() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void checkUpdate() {
        DatabaseReference versionRef = FirebaseDatabase.getInstance().getReference().child("VersionCode");
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String version = null;
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    version = dataSnapshot.getValue(String.class);
                }

                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                int code = 0;
                if (info != null) {
                    code = info.versionCode;
                }

                if (version != null && code < Integer.parseInt(version)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("নতুন আপডেট পাওয়া গেছে");
                    builder.setMessage("আপনার আ্যাপটি আপডেট করা হয়নাই | আপনি কি আপডেট করতে চাচ্ছেন?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mominur77.Jobs_BD"));
                            startActivity(browserIntent);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "আপডেট চেক করতে ব্যার্থ হয়েসে", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkMandatoryUpdate() {
        DatabaseReference versionRef = FirebaseDatabase.getInstance().getReference().child("VersionName");
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double version = 1.0;
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    version = Double.parseDouble(dataSnapshot.getValue(String.class));
                }

                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                double versionName = 1.0;
                if (info != null) {
                    versionName = Double.parseDouble(info.versionName);
                }

                if (versionName < version) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("নতুন আপডেট পাওয়া গেছে");
                    builder.setMessage("আপনার আ্যাপটি আপডেট করা হয়নাই | অনুগ্রহ করে আপডেট করে নিন|");
                    builder.setCancelable(false);
                    builder.setPositiveButton("আপডেট করুন", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mominur77.Jobs_BD"));
                            startActivity(browserIntent);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    if (!isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "আপডেট চেক করতে ব্যার্থ হয়েসে", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTargetActivity(boolean willShowWebViewInterstitialAds) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        if (url != null) {
            intent.putExtra("Url", url);
        }
        intent.putExtra("WillShowInterstitialAds", willShowWebViewInterstitialAds);
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
                        finishAffinity();
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


//    private void initAdmobInterstitialAd() {
//        admobInsterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
////        admobInsterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        admobInsterstitialAd.setAdUnitId("ca-app-pub-8280128094192030/3820928347");
//        admobInsterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
//                willLoadAdmobInterstitial = true;
//                startTargetActivity();
//            }
//
////            @Override
////            public void onAdFailedToLoad(int i) {
////                super.onAdFailedToLoad(i);
////                if (willLoadAdmobInterstitial) {
////                    admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
////                }
////                willLoadAdmobInterstitial = false;
////            }
//        });
//        admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
//    }

//    private void initFacebookInterstitialAds(){
//        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this, "YOUR_PLACEMENT_ID");
//        facebookInterstitialAd.setAdListener(new InterstitialAdListener() {
//            @Override
//            public void onInterstitialDisplayed(Ad ad) {
//
//            }
//
//            @Override
//            public void onInterstitialDismissed(Ad ad) {
//                facebookInterstitialAd.loadAd();
//                willLoadAdmobInterstitial = true;
//                startTargetActivity();
//            }
//
//            @Override
//            public void onError(Ad ad, AdError adError) {
//
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//        });
//        facebookInterstitialAd.loadAd();
//    }

//    private void showFacebookInterstitialAds(){
//        if(facebookInterstitialAd == null || !facebookInterstitialAd.isAdLoaded()) {
//            startTargetActivity();
//            return;
//        }
//        if(facebookInterstitialAd.isAdInvalidated()) {
//            startTargetActivity();
//            return;
//        }
//        facebookInterstitialAd.show();
//    }


    class WelcomeThread extends Thread {
        @Override
        public void run() {
            while (counter < 80 && !isInterrupted()) {
                try {
                    Thread.sleep(35);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        welcomeProgressBar.setProgress(counter);
                    }
                });
                counter++;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    welcomeRootLayout.setVisibility(View.GONE);
                    mainActivityRootLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkMandatoryUpdate();

//        if (willLoadAdmobInterstitial && admobInsterstitialAd!=null && !admobInsterstitialAd.isLoaded() && !admobInsterstitialAd.isLoading()){
//            admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
//            willLoadAdmobInterstitial = false;
//        }

//        if (willLoadAdmobInterstitial && !facebookInterstitialAd.isAdLoaded()){
//            facebookInterstitialAd.loadAd();
//            willLoadAdmobInterstitial = false;
//        }
    }

    @Override
    protected void onDestroy() {
        if (welcomeThread != null) {
            welcomeThread.interrupt();
            welcomeThread = null;
        }

//        if (facebookInterstitialAd != null) {
//            facebookInterstitialAd.destroy();
//        }
        super.onDestroy();
    }


}