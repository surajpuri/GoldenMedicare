package com.example.goldenmedicare.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Model.Report;
import com.example.goldenmedicare.Model.ReportListAdapter;
import com.example.goldenmedicare.Controller.Utilities;
import com.example.goldenmedicare.Controller.WebHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatientReportActivity extends AppCompatActivity {

    private ListView reportListView;
    private ReportListAdapter reportListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String query;
    private final String url = "http://192.168.1.132/golden_medicare/mysql_get_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_report);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.loginPreferences), MODE_PRIVATE);
        query = "SELECT * FROM tbl_report WHERE user_id = " + preferences.getInt(getString(R.string.preferencesUserId), 0);
        reportListView = findViewById(R.id.report_list);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_report);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTable().execute(query, url);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        new AsyncTable().execute(query, url);
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
        Intent intent = new Intent(this, PatientActivity.class);
        startActivity(intent);
        finish();
    }

    private class AsyncTable extends AsyncTask<String, String , String> {
        ProgressBar progressBar = findViewById(R.id.activity_patient_progressBar);

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            try {
                return WebHandler.doPostRequest(params[0], params[1]).body().string();
            } catch (Exception e) {
                return "exception";
            }
        }

        @Override
        protected void onPostExecute(String result){
            progressBar.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<Report> reportList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int reportId = jsonObject.getInt("report_id");
                        String reportTitle = jsonObject.getString("report_title");
                        String reportSummary = jsonObject.getString("report_summary");
                        String reportRemarks = jsonObject.getString("report_remarks");
                        String reportDate = jsonObject.getString("report_date");

                        Report report = new Report(reportId, reportTitle, reportSummary, reportRemarks, reportDate);
                        reportList.add(report);
                    } catch(JSONException | NullPointerException e){
                    }
                }

                reportListAdapter = new ReportListAdapter(reportList, getApplicationContext());
                reportListView.setAdapter(reportListAdapter);
            } catch (JSONException e) {
                Toast.makeText(PatientReportActivity.this, "Oops!! An error occurred while fetching data.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(28)
    private static class OnUnhandledKeyEventListenerWrapper implements View.OnUnhandledKeyEventListener {
        private ViewCompat.OnUnhandledKeyEventListenerCompat mCompatListener;

        OnUnhandledKeyEventListenerWrapper(ViewCompat.OnUnhandledKeyEventListenerCompat listener) {
            this.mCompatListener = listener;
        }

        public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
            return this.mCompatListener.onUnhandledKeyEvent(v, event);
        }
    }
}