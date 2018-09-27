package com.krysanify.lib;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class LocalDb extends RoomDatabase {
    public abstract UserGen.Dao userDao();
}
