package com.example.goldenmedicare.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.goldenmedicare.R;
import com.example.goldenmedicare.View.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class Utilities {

    public static void handleLogout(Context context){
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(context.getString(R.string.loginPreferences),MODE_PRIVATE);
        int userId = sharedPreferences.getInt(context.getString(R.string.preferencesUserId), 0);
        String query = "UPDATE tbl_user SET user_token = NULL WHERE user_id = " + userId + ";";
        new AsyncTokenDelete().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        if(context instanceof Activity){
            ((Activity)context).finish();
        }
    }


    private static class AsyncTokenDelete extends AsyncTask<String, String , String> {
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
