package com.aalto.asad.photoorganizer;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Downloader;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Juuso on 10.12.2017.
 */

public class DownloadTask extends AsyncTask<DLparams, Integer, Long> {
    protected Long doInBackground(DLparams... params) {
        int count = params.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            try {
                Log.d("MCC", "DownloadTask starting for: "+params[i].url+" into: "+params[i].dest.getPath());
                params[i].dest.createNewFile();
                FileUtils.copyURLToFile(new java.net.URL(params[i].url), params[i].dest);
            } catch (MalformedURLException e) {
                Log.d("MCC", "MalformedURLException in DownloadTask");
            } catch (IOException e) {
                Log.d("MCC", "IOException in DownloadTask");
            }
            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
    }
}
