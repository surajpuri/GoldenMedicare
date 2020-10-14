package com.example.goldenmedicare.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Controller.WebHandler;

public class RegisterActivity extends AppCompatActivity {

    private static final String bundle_username = "bundle_username";
    private EditText txtFullName, txtEmail, txtUsername, txtPassword, txtConfirmPassword;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtFullName = findViewById(R.id.activity_register_include_full_name);
        txtEmail = findViewById(R.id.activity_register_include_email);
        txtUsername = findViewById(R.id.activity_register_include_username);
        txtPassword = findViewById(R.id.activity_register_include_password);
        txtConfirmPassword = findViewById(R.id.activity_register_include_confirm_password);

        findViewById(R.id.activity_register_include_registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
                if(token.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Seems you haven't received token yet.\nPlease try again later.", Toast.LENGTH_LONG).show();
                }else {
                    String fullName = txtFullName.getText().toString().trim();
                    String email = txtEmail.getText().toString().trim();
                    String username = txtUsername.getText().toString().trim();
                    String password = txtPassword.getText().toString().trim();
                    String confirmPassword = txtConfirmPassword.getText().toString().trim();
                    if(!password.equals(confirmPassword)){
                        Toast.makeText(RegisterActivity.this, "Passwords Don't Match.\nPlease try again!", Toast.LENGTH_LONG).show();
                    }else {
                        String query = "INSERT INTO tbl_user (full_name, user_email, username, password, user_type, user_token) " +
                                "VALUES ('" + fullName + "', '" + email + "', '" + username + "', '" + password
                                + "', 2, '" + token + "');";
                        new AsyncLogin().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");
                    }
                }
            }
        });

        findViewById(R.id.activity_register_include_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(bundle_username, txtUsername.getText().toString());
    }

    private class  AsyncLogin extends AsyncTask<String, String , String>{

        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
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
                Toast.makeText(RegisterActivity.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else if (result.equals("1")) {
                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                Intent intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Oops! An error occured.\nPlease try again..", Toast.LENGTH_LONG).show();
            }
        }
    }

}
