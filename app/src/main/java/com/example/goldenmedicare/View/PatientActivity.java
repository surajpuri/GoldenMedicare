package com.example.goldenmedicare.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.Controller.Utilities;
import com.example.goldenmedicare.R;

public class PatientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        findViewById(R.id.activity_patient_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.activity_patient_appointments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientActivity.this, RequestAppointment.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.activity_patient_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientActivity.this, PatientReportActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Using switch statement
        switch (item.getItemId()) {
            case R.id.logout:
                // Call or crate object of LoginActivity when log out
                Utilities.handleLogout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}