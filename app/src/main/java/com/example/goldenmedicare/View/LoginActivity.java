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

import com.example.goldenmedicare.Model.Patient;
import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Controller.WebHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String bundle_username = "bundle_username";
    private EditText txtUsername, txtPassword;

    private String token = "";
    public static boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.activity_login_include_username);
        txtPassword = findViewById(R.id.activity_login_include_password);

        findViewById(R.id.activity_login_include_loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
                if(token.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Seems you haven't received token yet.\nPlease try again later.", Toast.LENGTH_LONG).show();
                }else {
                    String username = txtUsername.getText().toString().trim();
                    String password = txtPassword.getText().toString().trim();
                    String query = "SELECT user_id, full_name, user_email, user_type, user_token " +
                            "FROM tbl_user WHERE username = '" + username +
                            "' AND password = '"+ password +"' LIMIT 1;";
                    new AsyncLogin().execute(query, "http://192.168.1.132/golden_medicare/mysql_get_data.php");
                }
            }
        });

        findViewById(R.id.activity_login_include_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(bundle_username, txtUsername.getText().toString().trim());
    }

    private class  AsyncLogin extends AsyncTask<String, String , String>{

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
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
            if (result.equals("[]")) {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception")) {
                Toast.makeText(LoginActivity.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    isLoggedIn = true;
                    SharedPreferences.Editor editor = getApplicationContext()
                            .getSharedPreferences(getString(R.string.loginPreferences), MODE_PRIVATE).edit();
                    editor.putBoolean(getString(R.string.loggedInPreference), isLoggedIn);
                    editor.putString(getString(R.string.loggedInPassword), txtPassword.getText().toString().trim());
                    int userId = jsonObject.getInt("user_id");
                    int userType = jsonObject.getInt("user_type");

                    editor.putInt(getString(R.string.preferencesUserId), userId);
                    editor.putString(getString(R.string.preferencesUsername), txtUsername.getText().toString().trim());
                    editor.putString(getString(R.string.preferencesUserFullName), jsonObject.getString("full_name"));
                    editor.putString(getString(R.string.preferencesUserEmail), jsonObject.getString("user_email"));
                    editor.putInt(getString(R.string.preferencesUserType), userType);
                    String serverToken = jsonObject.getString("user_token");
                    editor.apply();

                    Intent intent;
                    if(userType==1) {
                        intent = new Intent(LoginActivity.this, DoctorActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, PatientActivity.class);
                    }
                    startActivity(intent);

                    if(!token.equals(serverToken)) {
                        String tokenQuery = "UPDATE tbl_user SET user_token = '" + token + "' WHERE user_id = " + userId + ";";
                        new AsyncToken().execute(tokenQuery, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class AsyncToken extends AsyncTask<String, String , String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
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

}
