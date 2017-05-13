package dev.gautam.todoapp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dev.gautam.todoapp.data.Task;
import dev.gautam.todoapp.data.source.TasksDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by gautam on 13/05/17.
 */

public class TasksLocalDataSource implements TasksDataSource {

    private static TasksLocalDataSource INSTANCE;
    private TasksDbHelper mDbHelper;

    //Prevent Direct Instantiation of this class
    private TasksLocalDataSource(Context context) {
        checkNotNull(context);
        mDbHelper = new TasksDbHelper(context);
    }

    //Single Instance
    public static TasksLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = new String[]{
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };
        String selection = TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        Task task = null;
        Cursor c = db.query(TasksPersistenceContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            task = new Task(title, description, itemId, completed);
        }

        if (c != null) {
            c.close();
        }
        db.close();
        if (task != null) {
            callback.onTaskLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getTasks(LoadTasksCallback callback) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = new String[]{
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };
        Cursor c = db.query(TasksPersistenceContract.TaskEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE));
                String description = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE));
                boolean completed = c.getInt(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
                Task task = new Task(title, description, id, completed);
                taskList.add(task);
            }
        }
        if (c != null) {
            c.close();
        }
        db.close();

        if (taskList.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(taskList);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        cv.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        cv.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        cv.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());

        db.insert(TasksPersistenceContract.TaskEntry.TABLE_NAME, null, cv);
        db.close();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());

        String selection = TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = new String[]{task.getId()};
        db.update(TasksPersistenceContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeTask(String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = new String[]{task.getId()};

        db.update(TasksPersistenceContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateTask(String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void clearCompletedTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = new String[]{"1"};
        db.delete(TasksPersistenceContract.TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshTasks() {

        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.

    }

    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TasksPersistenceContract.TaskEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        db.delete(TasksPersistenceContract.TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
