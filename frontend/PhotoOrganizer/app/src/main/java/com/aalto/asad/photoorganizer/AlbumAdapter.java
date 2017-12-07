package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Created by Asad on 11/25/2017.
 */

public class AlbumAdapter extends BaseAdapter {
    private final Context mContext;
    private List<PhotoAlbum> albumList;

    public AlbumAdapter(Context mContext, List<PhotoAlbum> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    public int getCount() {
        return albumList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.gallery_album_card, parent, false);
            holder.albumName = (TextView) convertView.findViewById(R.id.textGalleryName);
            holder.totalPhotos = (TextView) convertView.findViewById(R.id.textTotalPictures);
            holder.imageThumbnail = (ImageView) convertView.findViewById(R.id.groupImages);
            holder.imageCloud = (ImageView) convertView.findViewById(R.id.cloudPicture);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PhotoAlbum album = albumList.get(position);

        holder.albumName.setText(album.getName());
        holder.totalPhotos.setText(album.getNumOfPhotos());
        Uri imageUri = Uri.fromFile(new File(album.getThumbnail()));
        Glide.with(mContext).load(imageUri).into(holder.imageThumbnail);
        holder.imageCloud.setImageResource(album.getCloudImage());

//        customView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("MCC", "AlbumAdapter");
//                //Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mContext, PrivateImageActivity.class);
//                mContext.startActivity(intent);
//            }
//        });

        return convertView;
    }

    static class ViewHolder {
        TextView albumName;
        TextView totalPhotos;
        ImageView imageThumbnail;
        ImageView imageCloud;
    }
}