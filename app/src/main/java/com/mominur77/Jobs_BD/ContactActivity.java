package com.mominur77.Jobs_BD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    private Button copyEmailButton,copyNumberButton,closeButton,backButton;
    private ClipboardManager clipboardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        initAll();
    }


    private void initAll() {
        copyEmailButton=findViewById(R.id.contactActivityCopyEmailButtonId);
        copyNumberButton=findViewById(R.id.contactActivityCopyNumberButtonId);
        closeButton=findViewById(R.id.contactActivityCloseButtonId);
        backButton=findViewById(R.id.contactActivityBackButtonId);

        copyEmailButton.setOnClickListener(this);
        copyNumberButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.contactActivityCopyEmailButtonId:
                copyEmail();
                break;

            case R.id.contactActivityCopyNumberButtonId:
                copyNumber();
                break;

            case R.id.contactActivityCloseButtonId:
                ContactActivity.this.finishAffinity();
                break;

            case R.id.contactActivityBackButtonId:
                Intent intent=new Intent(ContactActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void copyEmail(){
        ClipData clipData=ClipData.newPlainText("AdminEmail","easysoft247@gmail.com");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(ContactActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void copyNumber(){
        ClipData clipData=ClipData.newPlainText("AdminNumber","01303628419");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(ContactActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }
}
