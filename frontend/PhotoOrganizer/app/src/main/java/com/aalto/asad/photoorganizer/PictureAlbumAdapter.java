package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//public class PictureAlbumAdapter extends ArrayAdapter<String> {
public class PictureAlbumAdapter extends BaseAdapter {

    private static final String TAG = "MCC";
    //private static final String Storageroot = "gs://mcc-fall-2017-g18.appspot.com/";
    //private StorageReference mStorageReference;

    private final Context mContext;
    private ArrayList<String> pictures;

    public PictureAlbumAdapter(Context mContext, ArrayList<String> pictures) {
        super();
        this.pictures = pictures;
        this.mContext = mContext;
        // Create a storage reference from our app
        //mStorageReference = FirebaseStorage.getInstance().getReference();
        Log.d(TAG, "Adapter being created with "+pictures.size()+" pictures");
    }

    public int getCount() {
        return pictures.size();
    }

    public String getItem(int position) { return pictures.get(position); }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String uri = getItem(position);
        // Create a reference with an initial file path and name
        //StorageReference pathReference = mStorageReference.child(url);
        Log.d(TAG, "Loading image from "+uri+ " into the view");
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.album_image_item, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewAlbum);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Uri imageUri = Uri.fromFile(new File(imagePath.get(position)));
        //Glide.with(mContext).using(new FirebaseImageLoader()).load(pathReference).into(holder.imageView);
        Glide.with(mContext).load(new File(uri)).centerCrop().into(holder.imageView);
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}

/*public class PictureAlbumAdapter extends BaseAdapter {
    private HashMap<String, ArrayList<String>> picturesMap;
    public PictureAlbumAdapter(Context context, HashMap<String, ArrayList<String>> picturesMap) {
        super();
    }

    public int getCount() {
        return this.picturesMap.size();
    }

    public View getItem() {
    }


    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        Picture picture = (Picture) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
        }
        //ImageView iv = (ImageView) convertView.findViewById(R.id.image);
        //TextView tv = (TextView) convertView.findViewById(R.id.author);

        //Picasso.with(getContext()).load(image.url).resize(100,100).into(iv);
        //tv.setText(image.author);

        return convertView;
    }
}
}*/
