package com.example.goldenmedicare.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.Controller.Utilities;
import com.example.goldenmedicare.Controller.WebHandler;
import com.example.goldenmedicare.Model.Appointment;
import com.example.goldenmedicare.Model.Patient;
import com.example.goldenmedicare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewAppointmentActivity extends AppCompatActivity {

    private final Calendar myCalendar = Calendar.getInstance();
    private TextView lblCategory, lblDesc, lblPatient;
    private EditText txtSchedule;
    Appointment appointment;

    private int reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_include);

        lblCategory = findViewById(R.id.activity_view_appointment_include_category);
        lblDesc = findViewById(R.id.activity_view_appointment_include_desc);
        lblPatient = findViewById(R.id.activity_view_appointment_include_patient);
        txtSchedule = findViewById(R.id.activity_view_appointment_include_schedule);
        txtSchedule.setFocusable(false);

        appointment = (Appointment) getIntent().getParcelableExtra("appointment");

        lblCategory.setText(appointment.getCategory());
        lblDesc.setText(appointment.getDescription());
        lblPatient.setText(appointment.getUser());
        if(!appointment.getScheduleDate().equals("null")) {
            txtSchedule.setText(appointment.getScheduleDate());
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        txtSchedule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ViewAppointmentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        findViewById(R.id.activity_view_appointment_include_approveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = appointment.getStatus();
                if(status.equals("DECLINED")){
                    Toast.makeText(ViewAppointmentActivity.this, "Can't approve a declined appointment", Toast.LENGTH_LONG).show();
                } else if(status.equals("COMPLETED")){
                    Toast.makeText(ViewAppointmentActivity.this, "This appointment has already been completed", Toast.LENGTH_LONG).show();
                } else {
                    String schedule = txtSchedule.getText().toString().trim();
                    String message = "Your appointment has been scheduled for " + schedule;
                    String query = "UPDATE tbl_appointment SET appointment_time = '" + schedule
                            + "', appointment_status = 'APPROVED' WHERE appointment_id = " + appointment.getAppointmentId() + ";";
                    new AsyncAppointment().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php", "APPROVED", message);
                }
            }
        });

        findViewById(R.id.activity_view_appointment_include_declineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = appointment.getStatus();
                if(status.equals("DECLINED")){
                    Toast.makeText(ViewAppointmentActivity.this, "This appointment has already been declined", Toast.LENGTH_LONG).show();
                } else if(status.equals("COMPLETED")){
                    Toast.makeText(ViewAppointmentActivity.this, "This appointment has already been completed", Toast.LENGTH_LONG).show();
                } else {
                    String schedule = txtSchedule.getText().toString().trim();
                    String message = "Your appointment has been declined!";
                    String query = "UPDATE tbl_appointment SET appointment_status = 'DECLINED' WHERE appointment_id = " + appointment.getAppointmentId() + ";";
                    new AsyncAppointment().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php", "DECLINED", message);
                }
            }
        });

        findViewById(R.id.activity_view_appointment_include_completeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = appointment.getStatus();
                if(status.equals("DECLINED")){
                    Toast.makeText(ViewAppointmentActivity.this, "This appointment has already been declined", Toast.LENGTH_LONG).show();
                } else if(status.equals("COMPLETED")){
                    Toast.makeText(ViewAppointmentActivity.this, "This appointment has already been completed", Toast.LENGTH_LONG).show();
                } else if(status.equals("PENDING")){
                    Toast.makeText(ViewAppointmentActivity.this, "This appointment is yet to be approved", Toast.LENGTH_LONG).show();
                } else {
                    String schedule = txtSchedule.getText().toString().trim();
                    String message = "Thank you for choosing Golden Medicare";
                    String query = "UPDATE tbl_appointment SET appointment_status = 'COMPLETED' WHERE appointment_id = " + appointment.getAppointmentId() + ";";
                    new AsyncAppointment().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php", "COMPLETED", message);
                }
            }
        });

    }

    private void updateLabel() {
        String myFormat = "yy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        txtSchedule.setText(sdf.format(myCalendar.getTime()));
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
        Intent intent = new Intent(this, DoctorAppointmentsActivity.class);
        startActivity(intent);
        finish();
    }

    private class  AsyncAppointment extends AsyncTask<String, String , String>{

        String success, message;
        ProgressDialog progressDialog = new ProgressDialog(ViewAppointmentActivity.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params){
            success = params[2];
            message = params[3];
            try {
                return WebHandler.doPostRequest(params[0], params[1]).body().string();
            } catch (Exception e) {
                return "exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equalsIgnoreCase("exception")) {
                Toast.makeText(ViewAppointmentActivity.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else if (result.equals("1")) {
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", success);
                    jsonObject.put("message", message);
                    jsonObject.put("token", appointment.getUserToken());
                    jsonArray.put(jsonObject);
                    new AsyncNotify().execute(jsonArray.toString(), "http://192.168.1.132/golden_medicare/appointment_status_notification_pusher.php");
                } catch (JSONException e) { }
                Toast.makeText(ViewAppointmentActivity.this, "Appointment " + success, Toast.LENGTH_LONG).show();
                Intent intent =  new Intent(ViewAppointmentActivity.this, DoctorAppointmentsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ViewAppointmentActivity.this, "Oops! An error occured.\nPlease try again..", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class  AsyncNotify extends AsyncTask<String, String , String>{
        @Override
        protected void onPreExecute(){
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
        protected void onPostExecute(String result) {
        }
    }

    private class  AsyncReportId extends AsyncTask<String, String , String>{
        @Override
        protected void onPreExecute(){
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
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("exception")) {
            } else if (!result.equals("[]")) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    reportId = jsonObject.getInt("report_id");
                } catch (JSONException e) {
                    reportId = 0;
                }
            }else {
                reportId = 0;
            }
        }
    }

}
