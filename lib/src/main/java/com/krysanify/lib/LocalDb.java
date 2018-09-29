package com.krysanify.lib;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * DAO factory module
 */
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class LocalDb extends RoomDatabase {
    /**
     * Provider for user table's DAO.
     * @return generated DAO to do user operations
     */
    public abstract UserGen.Dao userDao();
}
