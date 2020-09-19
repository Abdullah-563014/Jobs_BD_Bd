package com.mominur77.Jobs_BD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.ads.*;
//import com.google.android.gms.ads.AdRequest;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AgeCalculatorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner birthDateSpinner, birthMonthSpinner, birthYearSpinner, todayDateSpiner, todayMonthSpinner, todayYearSpinner;
    private Button calculateButton, backButton;
    private TextView yearsTextView, monthsTextView, daysTextView;
    private String todayDateMonthYear, birthDateMonthYear;
    private int todayDate, todayMonth, todayYear, birthDate, birthMonth, birthYear;
    private int birthDateForValidityCheck, birthMonthForValidityCheck, currentDateForValidityCheck, currentMonthForValidityCheck;
//    private com.google.android.gms.ads.AdView admobAdview;
//    private com.google.android.gms.ads.InterstitialAd admobInterstitialAds;
    private boolean
            loadAdsAgain = true,
            exitStatus=false;
    private ExitThread exitThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_calculator);
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        initializeAll();

        initAllSpinnerListener();

//        initAdmobBannerAd();
//
//        initAdmobInterstitialAd();

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAgeMethod();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        startExitThread();
    }

    private void startExitThread() {
        if (exitThread!=null){
            exitThread.interrupt();
            exitThread=null;
        }
        exitStatus=false;
        exitThread=new ExitThread();
        exitThread.start();
    }

//    private void initAdmobBannerAd() {
//        admobAdview = findViewById(R.id.ageCalculatorActivityAdmobBannerAdsContainerId);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        admobAdview.loadAd(adRequest);
//    }
//
//    private void initAdmobInterstitialAd() {
//        admobInterstitialAds = new com.google.android.gms.ads.InterstitialAd(this);
////        admobInterstitialAds.setAdUnitId(getResources().getString(R.string.age_calculator_activity_admob_interstitial_ads_code));
//        admobInterstitialAds.setAdUnitId("ca-app-pub-8280128094192030/7377029974");
//        admobInterstitialAds.setAdListener(new com.google.android.gms.ads.AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                calculateAgeMethod();
//                admobInterstitialAds.loadAd(new AdRequest.Builder().build());
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                if (loadAdsAgain) {
//                    admobInterstitialAds.loadAd(new AdRequest.Builder().build());
//                }
//                loadAdsAgain = false;
//            }
//        });
//        admobInterstitialAds.loadAd(new AdRequest.Builder().build());
//    }

    private void initAllSpinnerListener() {
        Calendar calendar = Calendar.getInstance();
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = (calendar.get(Calendar.MONTH) + 1);
        todayDate = calendar.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<String> dateArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.date_array));
        dateArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        birthDateSpinner.setAdapter(dateArrayAdapter);
        todayDateSpiner.setAdapter(dateArrayAdapter);

        ArrayAdapter<String> monthArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.month_array));
        monthArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        birthMonthSpinner.setAdapter(monthArrayAdapter);
        todayMonthSpinner.setAdapter(monthArrayAdapter);

        ArrayAdapter<String> yearArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.year_array));
        yearArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        birthYearSpinner.setAdapter(yearArrayAdapter);
        todayYearSpinner.setAdapter(yearArrayAdapter);

        List<String> dateList = Arrays.asList(getResources().getStringArray(R.array.date_array));
        int datePosition;
        if (todayDate < 10) {
            datePosition = dateList.indexOf("0" + todayDate);
        } else {
            datePosition = dateList.indexOf(String.valueOf(todayDate));
        }
        todayDateSpiner.setSelection(datePosition);


        List<String> monthList = Arrays.asList(getResources().getStringArray(R.array.month_array));
        int monthPosition;
        if (todayMonth < 10) {
            monthPosition = monthList.indexOf("0" + todayMonth);
        } else {
            monthPosition = monthList.indexOf(String.valueOf(todayMonth));
        }
        todayMonthSpinner.setSelection(monthPosition);


        List<String> yearList = Arrays.asList(getResources().getStringArray(R.array.year_array));
        int yearPosition = yearList.indexOf(String.valueOf(todayYear));
        todayYearSpinner.setSelection(yearPosition);

        birthDateSpinner.setSelection(0);
        birthMonthSpinner.setSelection(0);
        birthYearSpinner.setSelection(80);
    }

    private void retrieveTodayInformation() {
        int date = todayDateSpiner.getSelectedItemPosition();
        int month = todayMonthSpinner.getSelectedItemPosition();
        int year = todayYearSpinner.getSelectedItemPosition();

        List<String> dateList = Arrays.asList(getResources().getStringArray(R.array.date_array));
        List<String> monthList = Arrays.asList(getResources().getStringArray(R.array.month_array));
        List<String> yearList = Arrays.asList(getResources().getStringArray(R.array.year_array));
        todayDateMonthYear = dateList.get(date) + "/" + monthList.get(month) + "/" + yearList.get(year);

        currentDateForValidityCheck = Integer.parseInt(dateList.get(date));
        currentMonthForValidityCheck = Integer.parseInt(dateList.get(month));
    }

    private void retrieveBirthInformation() {
        int date = birthDateSpinner.getSelectedItemPosition();
        int month = birthMonthSpinner.getSelectedItemPosition();
        int year = birthYearSpinner.getSelectedItemPosition();

        List<String> dateList = Arrays.asList(getResources().getStringArray(R.array.date_array));
        List<String> monthList = Arrays.asList(getResources().getStringArray(R.array.month_array));
        List<String> yearList = Arrays.asList(getResources().getStringArray(R.array.year_array));
        birthDateMonthYear = dateList.get(date) + "/" + monthList.get(month) + "/" + yearList.get(year);

        birthDateForValidityCheck = Integer.parseInt(dateList.get(date));
        birthMonthForValidityCheck = Integer.parseInt(dateList.get(month));
    }

    private void initializeAll() {
        calculateButton = findViewById(R.id.calculateBirthDateButtonId);
        backButton = findViewById(R.id.ageCalculatorActivityBackButtonId);
        yearsTextView = findViewById(R.id.yearsTextViewId);
        monthsTextView = findViewById(R.id.monthsTextViewId);
        daysTextView = findViewById(R.id.daysTextViewId);
        todayDateSpiner = findViewById(R.id.todayDateSpinnerId);
        todayMonthSpinner = findViewById(R.id.todayMonthSpinnerId);
        todayYearSpinner = findViewById(R.id.todayYearSpinnerId);
        birthDateSpinner = findViewById(R.id.birthDateSpinnerId);
        birthMonthSpinner = findViewById(R.id.birthMonthSpinnerId);
        birthYearSpinner = findViewById(R.id.birthYearSpinnerId);
    }

    private void calculateAgeMethod() {
        retrieveBirthInformation();
        retrieveTodayInformation();

        if ((birthMonthForValidityCheck == 2 && birthDateForValidityCheck > 29) || (currentMonthForValidityCheck == 2 && currentDateForValidityCheck > 29)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AgeCalculatorActivity.this)
                    .setMessage("আপনার তারিখ অথবা মাস নির্বাচন করা ভুল হয়েসে| ফেব্রূয়ারি মাস ২৯ দিনের বেশি হবে না| অনুগ্রহ করে সঠিক ভাবে নির্বাচন করুন|")
                    .setCancelable(true)
                    .setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            if (!isFinishing()) {
                alertDialog.show();
            }
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date dateOne = simpleDateFormat.parse(birthDateMonthYear);
                Date dateTwo = simpleDateFormat.parse(todayDateMonthYear);
                long startDate = 0;
                long endDate = 0;
                if (dateOne != null) {
                    startDate = dateOne.getTime();
                }
                if (dateTwo != null) {
                    endDate = dateTwo.getTime();
                }

                if (startDate <= endDate) {
//                    if (admobInterstitialAds.isLoaded()) {
//                        admobInterstitialAds.show();
//                    }
                    Period period = new Period(startDate, endDate, PeriodType.yearMonthDay());

                    yearsTextView.setText("বছর:- " + period.getYears());
                    monthsTextView.setText("মাস:- " + period.getMonths());
                    daysTextView.setText("দিন:- " + period.getDays());
                } else {
                    Toast.makeText(this, "আপনার জন্ম বছর, সার্কুলারে উল্লেখিত বছর হতে বেশি হতে পারবে না |", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    class ExitThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exitStatus=true;
        }
    }

    @Override
    public void onBackPressed() {
        if (exitStatus){
            super.onBackPressed();
        }else {
            Toast.makeText(this, "Please wait a moment and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (exitThread!=null){
            exitThread.interrupt();
            exitThread=null;
        }
        super.onDestroy();
    }
}
