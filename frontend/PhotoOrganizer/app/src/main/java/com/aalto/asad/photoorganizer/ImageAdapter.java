package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 11/30/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private List<String> imagePath;

    public ImageAdapter(Context mContext, List<String> imagePath) {
        this.mContext = mContext;
        this.imagePath = imagePath;
    }

    public int getCount() {
        return imagePath.size();
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View customView = inflater.inflate(R.layout.private_image_item, parent, false);

        ImageView imageView = (ImageView) customView.findViewById(R.id.imageViewPrivate);

        Uri imageUri = Uri.fromFile(new File(imagePath.get(position)));
        imageView.setImageURI(imageUri);
        //Picasso.with(mContext).load(imagePath.get(position)).into(imageView);

        return customView;
    }
}
