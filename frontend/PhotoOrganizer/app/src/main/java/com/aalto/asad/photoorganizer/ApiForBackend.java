package com.aalto.asad.photoorganizer;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Asad on 12/5/2017.
 */

public class ApiForBackend {

    private static final String TAG = "MCC";
    public static final String URL = "http://httpbin.org/";
    private OkHttpClient client;

    public ApiForBackend() {
        client = new OkHttpClient();
    }

    Call post(String url, RequestBody formBody, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public String executePost(String functionName, HashMap<String, String> parameters) {
        final String[] postResponse = {null};
        FormBody.Builder formBuilder = new FormBody.Builder();
        
        for (Map.Entry<String, String> entry: parameters.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = formBuilder.build();

        post(URL + functionName, formBody, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, "executePost:failure " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    postResponse[0] = response.body().string();
                    Log.i(TAG, "executePost:ResponseSuccess " + Arrays.toString(postResponse));
                } else {
                    Log.i(TAG, "executePost:ResponseFailure");
                }
            }
        });
        
        return postResponse[0];
    }
}
