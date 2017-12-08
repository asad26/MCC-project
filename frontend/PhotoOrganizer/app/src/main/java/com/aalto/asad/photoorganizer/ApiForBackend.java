package com.aalto.asad.photoorganizer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.google.android.gms.internal.zzahg.runOnUiThread;

/**
 * Created by Asad on 12/7/2017.
 */

public class ApiForBackend {

    private static final String TAG = "MCC";
    public static final String URL = "https://mcc-fall-2017-g18.appspot.com";

    public void executePost(final String functionName, HashMap<String, String> parameters) {

        OkHttpClient okHttpClient = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry: parameters.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(URL + functionName)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, "executePost:failure " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();

                    //<----------------------------------------------

                    // Here the res can be parsed and we can handle group management

                    //<-----------------------------


                    Log.i(TAG, "executePost:ResponseSuccess " + res);
                } else {
                    Log.i(TAG, "executePost:ResponseFailure");
                }
            }
        });
    }
}
