package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View customView = inflater.inflate(R.layout.gallery_album_card, parent, false);

        PhotoAlbum album = albumList.get(position);

        TextView albumName = (TextView) customView.findViewById(R.id.textGalleryName);
        TextView totalPhotos = (TextView) customView.findViewById(R.id.textTotalPictures);
        ImageView imageThumbnail = (ImageView) customView.findViewById(R.id.groupImages);

        albumName.setText(album.getName());
        totalPhotos.setText(album.getNumOfPhotos());
        imageThumbnail.setImageResource(album.getThumbnail());
        //imageThumbnail.setImageBitmap(album.getThumbnail());

        return customView;
    }
}