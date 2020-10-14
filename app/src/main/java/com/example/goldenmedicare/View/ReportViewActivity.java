package com.example.goldenmedicare.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.Model.Report;
import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Controller.Utilities;

public class ReportViewActivity extends AppCompatActivity {

    private TextView txtTitle, txtSummary, txtRemarks, txtDate;
    Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);

        txtTitle = findViewById(R.id.activity_report_view_include_title);
        txtSummary = findViewById(R.id.activity_report_view_include_summary2);
        txtRemarks = findViewById(R.id.activity_report_view_include_remarks2);
        txtDate = findViewById(R.id.activity_report_view_include_date2);

        report = (Report) getIntent().getParcelableExtra("report");

        loadData();
    }

    private void loadData() {
        txtDate.setText(report.getReportDate());
        txtTitle.setText(report.getTitle());
        txtSummary.setText(report.getSummary());
        txtRemarks.setText(report.getRemarks());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PatientReportActivity.class);
        startActivity(intent);
        finish();
    }
}
