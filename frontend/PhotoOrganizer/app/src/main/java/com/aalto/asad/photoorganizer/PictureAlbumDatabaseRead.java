package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Created by Juuso on 10.12.2017.
 */

public class PictureAlbumDatabaseRead extends AsyncTask<String, Void, List<PictureInfo>> {

    private OnTaskCompleted listener;
    private Context context;

    public PictureAlbumDatabaseRead(OnTaskCompleted listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected List<PictureInfo> doInBackground(String... groups) {
        List<PictureInfo> pictureInfoList = AppDatabase.getInstance(context).pictureInfoDao().findAllByGroup(groups[0]);
        Log.i("MCC", "DatabaseRead:doInBackGround ");
        for (int i = 0; i < pictureInfoList.size();i++) {
            PictureInfo p = pictureInfoList.get(i);
        }
        return pictureInfoList;
    }

    protected void onPostExecute(List<PictureInfo> result){
        Log.i("MCC", "DatabaseRead:onPostExecute ");
        listener.onTaskCompleted(result);
    }
}
