package com.aalto.asad.photoorganizer;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Asad on 12/8/2017.
 */

@Dao
public interface PictureInfoDao {

    @Query("SELECT * FROM picturesInfo")
    List<PictureInfo> getAll();

    @Query("SELECT * FROM picturesInfo WHERE groupId IN (:userIds)")
    List<PictureInfo> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM picturesInfo WHERE user_name LIKE :userName")
    PictureInfo findByName(String userName);

    @Query("SELECT * FROM picturesInfo WHERE user_name = :userName")
    List<PictureInfo> findAllByName(String userName);

    @Insert
    void insertAll(PictureInfo... pictureInfos);

    @Delete
    void delete(PictureInfo pictureInfo);
}
