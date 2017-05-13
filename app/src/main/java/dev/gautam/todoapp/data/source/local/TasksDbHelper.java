package dev.gautam.todoapp.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gautam on 12/05/17.
 */

public class TasksDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String BOOLEAN_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TasksPersistenceContract.TaskEntry.TABLE_NAME + " (" +
                    TasksPersistenceContract.TaskEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED + BOOLEAN_TYPE +

                    " )";

    public TasksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
