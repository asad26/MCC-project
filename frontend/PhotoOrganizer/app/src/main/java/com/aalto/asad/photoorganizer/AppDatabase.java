package com.aalto.asad.photoorganizer;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Asad on 12/8/2017.
 */

@Database(entities = {PictureInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appInstance;
    public abstract PictureInfoDao pictureInfoDao();

    public static AppDatabase getInstance(final Context appContext) {
        if (appInstance == null) {
            appInstance = Room.databaseBuilder(appContext, AppDatabase.class, "pictures-info-database").build();
        }
        return  appInstance;
    }
}
