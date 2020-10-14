package com.example.goldenmedicare.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.Controller.WebHandler;
import com.example.goldenmedicare.R;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText txtFullName, txtEmail;

    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        txtFullName = findViewById(R.id.activity_update_profile_include_full_name);
        txtEmail = findViewById(R.id.activity_update_profile_include_email);

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.loginPreferences), MODE_PRIVATE);
        final String fullName = preferences.getString(getString(R.string.preferencesUserFullName), "");
        final String email = preferences.getString(getString(R.string.preferencesUserEmail), "");
        final int userId = preferences.getInt(getString(R.string.preferencesUserId), 0);
        userType = preferences.getInt(getString(R.string.preferencesUserType), 1);

        txtFullName.setText(fullName);
        txtEmail.setText(email);

        findViewById(R.id.activity_update_profile_include_updateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedFullName = txtFullName.getText().toString().trim();
                String editedEmail = txtEmail.getText().toString().trim();
                if(userId==0) {
                    Toast.makeText(UpdateProfileActivity.this, "An error occured.\nPlease try re-logging in", Toast.LENGTH_LONG).show();
                } else if(fullName.equals(editedFullName) && email.equals(editedEmail)){
                    Toast.makeText(UpdateProfileActivity.this, "No changes made", Toast.LENGTH_LONG).show();
                } else {
                    String query = "UPDATE tbl_user SET full_name = '" + editedFullName
                            + "', user_email = '" + editedEmail + "' WHERE user_id = " + userId + ";";
                    new AsyncLogin().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if(userType==1){
            intent = new Intent(this, DoctorActivity.class);
        } else {
            intent = new Intent(this, PatientActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private class  AsyncLogin extends AsyncTask<String, String , String>{

        ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
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
            progressDialog.dismiss();
            if (result.equalsIgnoreCase("exception")) {
                Toast.makeText(UpdateProfileActivity.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else if (result.equals("1")) {
                Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_LONG).show();
                Intent intent;
                if(userType==1){
                    intent = new Intent(UpdateProfileActivity.this, DoctorActivity.class);
                } else {
                    intent = new Intent(UpdateProfileActivity.this, PatientActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(UpdateProfileActivity.this, "Oops! An error occured.\nPlease try again..", Toast.LENGTH_LONG).show();
            }
        }
    }

}
