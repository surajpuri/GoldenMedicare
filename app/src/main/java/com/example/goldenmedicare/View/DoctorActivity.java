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

public class DoctorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.loginPreferences), MODE_PRIVATE);
        if (!preferences.getBoolean(getString(R.string.loggedInPreference), false)) {
            startActivity(new Intent(DoctorActivity.this, LoginActivity.class));
            finish();
        }
        if (preferences.getInt(getString(R.string.preferencesUserType), 1)!=1){
            Intent intent = new Intent(DoctorActivity.this, PatientActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.activity_doctor_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.activity_doctor_appointments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this, DoctorAppointmentsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.activity_doctor_patients).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this, DoctorPatientsActivity.class);
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