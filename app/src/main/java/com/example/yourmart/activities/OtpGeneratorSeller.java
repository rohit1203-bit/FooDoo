package com.example.yourmart.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yourmart.R;
import com.hbb20.CountryCodePicker;

public class OtpGeneratorSeller extends AppCompatActivity {
    CountryCodePicker ccp2;
    EditText ts1;
    Button bs1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_generator_seller);

        ts1 = (EditText) findViewById(R.id.tSeller1);
        ccp2 = (CountryCodePicker) findViewById(R.id.ccpSeller2);
        ccp2.registerCarrierNumberEditText(ts1);
        bs1 = (Button) findViewById(R.id.bSeller1);

        bs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtpGeneratorSeller.this, ManageOtpSeller.class);
                intent.putExtra("mobile", ccp2.getFullNumberWithPlus().replace(" ", ""));
                startActivity(intent);
            }
        });
    }
}