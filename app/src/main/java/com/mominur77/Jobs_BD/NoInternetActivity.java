package com.mominur77.Jobs_BD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NoInternetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button copyEmailButton,copyNumberButton,refreshButton,closeButton,backButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ClipboardManager clipboardManager;
    private String url;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            url=bundle.getString("url",null);
            activity=bundle.getString("activity");
        }

        initAll();

        initializeSwipeRefreshLayout();
    }

    private void initAll() {
        copyEmailButton=findViewById(R.id.noInternetActivityCopyEmailButtonId);
        copyNumberButton=findViewById(R.id.noInternetActivityCopyNumberButtonId);
        refreshButton=findViewById(R.id.noInternetActivityRefreshButtonId);
        closeButton=findViewById(R.id.noInternetActivityCloseButtonId);
        backButton=findViewById(R.id.noInternetActivityBackButtonId);
        swipeRefreshLayout=findViewById(R.id.noInternetActivitySwipeRefreshLayoutId);

        copyEmailButton.setOnClickListener(this);
        copyNumberButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        swipeRefreshLayout.setOnClickListener(this);

        clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.noInternetActivityCopyEmailButtonId:
                copyEmail();
                break;

            case R.id.noInternetActivityCopyNumberButtonId:
                copyNumber();
                break;

            case R.id.noInternetActivityRefreshButtonId:
                checkInternet();
                break;

            case R.id.noInternetActivityCloseButtonId:
                NoInternetActivity.this.finishAffinity();
                break;

            case R.id.noInternetActivityBackButtonId:
                Intent intent=new Intent(NoInternetActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void copyEmail(){
        ClipData clipData=ClipData.newPlainText("AdminEmail","easysoft247@gmail.com");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(NoInternetActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void copyNumber(){
        ClipData clipData=ClipData.newPlainText("AdminNumber","01303628419");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(NoInternetActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void checkInternet(){
        if (Utils.haveInternet(getApplicationContext())){
            if (isTaskRoot()){
                startActivity(new Intent(NoInternetActivity.this,MainActivity.class));
            }
            finish();
        }else {
            Toast.makeText(this, "Yet, You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternet();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
