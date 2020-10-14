package com.example.goldenmedicare.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.goldenmedicare.Model.Patient;
import com.example.goldenmedicare.Model.PatientListAdapter;
import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Controller.Utilities;
import com.example.goldenmedicare.Controller.WebHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DoctorPatientsActivity extends AppCompatActivity {

    private ListView patientListView;
    private PatientListAdapter patientListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private final String query = "SELECT user_id, full_name, user_email, user_token FROM tbl_user " +
            "WHERE user_type = 2 ORDER BY user_id DESC;";
    private final String url = "http://192.168.1.132/golden_medicare/mysql_get_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patients);

        patientListView = findViewById(R.id.patients_list);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_patient);
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
        Intent intent = new Intent(this, DoctorActivity.class);
        startActivity(intent);
        finish();
    }

    private class AsyncTable extends AsyncTask<String, String , String> {
        ProgressBar progressBar = findViewById(R.id.activity_doctor_progressBar);

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
                ArrayList<Patient> patientsList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int patientId = jsonObject.getInt("user_id");
                        String patientName = jsonObject.getString("full_name");
                        String patientEmail = jsonObject.getString("user_email");
                        String patientToken = jsonObject.getString("user_token");

                        Patient patient = new Patient(patientId, patientName, patientEmail, patientToken);
                        patientsList.add(patient);
                    } catch(JSONException | NullPointerException e){
                    }
                }

                patientListAdapter = new PatientListAdapter(patientsList, getApplicationContext());
                patientListView.setAdapter(patientListAdapter);
            } catch (JSONException e) {
                Toast.makeText(DoctorPatientsActivity.this, "Oops!! An error occurred while fetching data.", Toast.LENGTH_LONG).show();
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