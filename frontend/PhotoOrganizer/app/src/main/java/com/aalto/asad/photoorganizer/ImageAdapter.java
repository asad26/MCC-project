package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.private_image_item, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewPrivate);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Uri imageUri = Uri.fromFile(new File(imagePath.get(position)));
        Glide.with(mContext).load(imageUri) .into(holder.imageView);
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
