package com.example.goldenmedicare.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.Controller.WebHandler;
import com.example.goldenmedicare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestAppointment extends AppCompatActivity {

    private Spinner spCategory;
    private EditText txtDescription;

    private final String query = "SELECT * FROM tbl_category;";
    private final String url = "http://192.168.1.132/golden_medicare/mysql_get_data.php";
    private HashMap<String, Integer> categoryIdMap = new HashMap<>();

    String category, appointmentDesc, userFullName, token;
    int userId, appointmentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_appointment);

        txtDescription = findViewById(R.id.activity_take_appointment_include_description);
        spCategory = findViewById(R.id.activity_take_appointment_include_category);

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.loginPreferences), MODE_PRIVATE);
        userId = preferences.getInt(getString(R.string.preferencesUserId), 0);
        userFullName = preferences.getString(getString(R.string.preferencesUserFullName), "");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");

        findViewById(R.id.activity_take_appointment_include_requestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncAppointmentCount().execute("SELECT COUNT(*) AS appointment_count FROM tbl_appointment WHERE user_id = "
                                + userId + " AND appointment_status NOT IN ('DECLINED', 'COMPLETED');",
                        "http://192.168.1.132/golden_medicare/mysql_query_executor.php");
                if (appointmentCount>0){
                    Toast.makeText(RequestAppointment.this, "You have already requested for an appointment.", Toast.LENGTH_LONG).show();
                } else if(spCategory.getSelectedItemPosition()==0){
                    Toast.makeText(RequestAppointment.this, "Please select a category", Toast.LENGTH_LONG).show();
                    spCategory.requestFocus();
                } else {
                    category = spCategory.getSelectedItem().toString();
                    int categoryId = categoryIdMap.get(category);
                    appointmentDesc = txtDescription.getText().toString().trim();
                    String query = "INSERT INTO tbl_appointment (category_id, appointment_desc, appointment_status, user_id) " +
                            "VALUES (" + categoryId + ", '" + appointmentDesc + "', 'PENDING', " + userId + ");";
                    new AsyncRequest().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php", "" + categoryId);
                }
            }
        });
        new AsyncTable().execute(query, url);
    }


    private class AsyncTable extends AsyncTask<String, String , String> {
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
        protected void onPostExecute(String result){
            ArrayList<String> categoryList = new ArrayList<>();
            categoryList.add("Select Category");
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int categoryId = jsonObject.getInt("category_id");
                        String categoryTitle = jsonObject.getString("category_title");
                        categoryList.add(categoryTitle);
                        categoryIdMap.put(categoryTitle, categoryId);
                    } catch(JSONException | NullPointerException e){
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(RequestAppointment.this, "Oops!! An error occurred while fetching data.", Toast.LENGTH_LONG).show();
            }
            // Creating array adapter for sites
            ArrayAdapter categoryAdapter = new ArrayAdapter(RequestAppointment.this, android.R.layout.simple_spinner_item, categoryList);

            // Drop down style will be list view with radio button
            categoryAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

            // attaching data adapter to sensor_name
            spCategory.setAdapter(categoryAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private class  AsyncAppointmentCount extends AsyncTask<String, String , String>{
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
                    appointmentCount = jsonObject.getInt("appointment_count");
                } catch (JSONException e) {
                    appointmentCount = 0;
                }
            }else {
                appointmentCount = 0;
            }
        }
    }

    private class  AsyncRequest extends AsyncTask<String, String , String>{

        int categoryId;
        ProgressDialog progressDialog = new ProgressDialog(RequestAppointment.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params){
            categoryId = Integer.parseInt(params[2]);
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
                Toast.makeText(RequestAppointment.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else if (result.equals("1")) {
                String query = "SELECT appointment_id FROM tbl_appointment WHERE user_id = "
                        + userId + " AND appointment_status = 'PENDING' ORDER BY appointment_id DESC LIMIT 1;";
                new AsyncAppointmentId().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");
            } else {
                Toast.makeText(RequestAppointment.this, "Oops! An error occured.\nPlease try again..", Toast.LENGTH_LONG).show();
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

    private class  AsyncAppointmentId extends AsyncTask<String, String , String>{
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
                int appointmentId = Integer.parseInt(result);

                if (appointmentId > 0) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("appointment_id", appointmentId);
                        jsonObject.put("appointment_desc", appointmentDesc);
                        jsonObject.put("category", category);
                        jsonObject.put("status", "PENDING");
                        jsonObject.put("user_name", userFullName);
                        jsonObject.put("token", token);
                        jsonArray.put(jsonObject);

                        new AsyncNotify().execute(jsonArray.toString(), "http://192.168.1.132/golden_medicare/request_appointment_notification_pusher.php");
                    } catch (JSONException e) {
                    }
                }
            }else {
            }
            Toast.makeText(RequestAppointment.this, "Appointment Request Successful!", Toast.LENGTH_LONG).show();
            Intent intent =  new Intent(RequestAppointment.this, PatientActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
