package com.example.eecs4443groupprojectgroup1.db;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {

    // Migration from version 1 to 2, adding 'gender' column to 'users' table
    public static final Migration MIGRATION = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add the 'gender' column to the 'users' table
            database.execSQL("ALTER TABLE users ADD COLUMN gender TEXT");
        }
    };
}
