package com.example.goldenmedicare.View;

import android.content.Intent;
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

import com.example.goldenmedicare.Controller.Utilities;
import com.example.goldenmedicare.Controller.WebHandler;
import com.example.goldenmedicare.Model.Appointment;
import com.example.goldenmedicare.Model.AppointmentListAdapter;
import com.example.goldenmedicare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DoctorAppointmentsActivity extends AppCompatActivity {

    private ListView appointmentListView;
    private AppointmentListAdapter appointmentListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private final String query = "SELECT a.appointment_id, a.appointment_status, a.appointment_desc, " +
            "a.appointment_time, c.category_title, u.full_name, u.user_token " +
            "FROM tbl_appointment a JOIN tbl_category c ON (a.category_id = c.category_id) " +
            "JOIN tbl_user u ON (a.user_id = u.user_id)";
    private final String url = "http://192.168.1.132/golden_medicare/mysql_get_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

        appointmentListView = findViewById(R.id.appointments_list);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_appointment);
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
                ArrayList<Appointment> appointmentsList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int appointmentId = jsonObject.getInt("appointment_id");
                        String status = jsonObject.getString("appointment_status");
                        String category = jsonObject.getString("category_title");
                        String desc = jsonObject.getString("appointment_desc");
                        String scheduleDate = jsonObject.getString("appointment_time");
                        String user = jsonObject.getString("full_name");
                        String token = jsonObject.getString("user_token");

                        Appointment appointment = new Appointment(appointmentId, status, category, desc, scheduleDate, user, token);
                        appointmentsList.add(appointment);
                    } catch(JSONException | NullPointerException e){
                    }
                }

                appointmentListAdapter = new AppointmentListAdapter(appointmentsList, getApplicationContext());
                appointmentListView.setAdapter(appointmentListAdapter);
            } catch (JSONException e) {
                Toast.makeText(DoctorAppointmentsActivity.this, "Oops!! An error occurred while fetching data.", Toast.LENGTH_LONG).show();
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