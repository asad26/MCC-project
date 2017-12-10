package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

/**
 * Created by paavo on 12/10/17.
 */

public class PhotoViewActivity extends AppCompatActivity {
    private static final String TAG = "MCC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PhotoViewActivity:onCreate");
        setContentView(R.layout.photo_view);
        Bundle b = getIntent().getExtras();
        String path = "";
        if(b != null)
            path = b.getString("path");
        Log.d(TAG, path);
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Drawable d = new BitmapDrawable(getResources(), myBitmap);
            PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
            photoView.setImageDrawable(d);

        }


        //Uri imageUri = Uri.fromFile(new File(imagePath.get(position)));



        //photoView.setImageResource(R.drawable.image);
    }

}
