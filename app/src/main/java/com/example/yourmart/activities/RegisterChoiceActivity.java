package com.example.yourmart.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.yourmart.R;

public class RegisterChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_choice);
    }

    public void useronclick(View view) {
        startActivity(new Intent(RegisterChoiceActivity.this,RegisterUserActivity.class));
        finish();
    }

    public void selleronclick(View view) {
        startActivity(new Intent(RegisterChoiceActivity.this,RegisterSellerActivity.class));
        finish();
    }
}