package com.example.goldenmedicare.Controller;


import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class WebHandler {
    /**
     * get response data from the url
     * @param query
     * @param url
     * @return response
     * @throws Exception
     */
    public static Response doPostRequest(String query, String url) throws Exception {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, query);
        //initializing HttpClient request for sending SMS
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();//requets bulding for the given URL
        return client.newCall(request).execute(); // response for the request
    }


}
