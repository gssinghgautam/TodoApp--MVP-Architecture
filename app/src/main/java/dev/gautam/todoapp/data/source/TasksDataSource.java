package dev.gautam.todoapp.data.source;

/**
 * Created by gautam on 12/05/17.
 */

import android.support.annotation.NonNull;

import java.util.List;

import dev.gautam.todoapp.data.Task;

/**
 * Main entry point for accessing tasks data.
 * <p>
 * For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */

public interface TasksDataSource {

    interface LoadTasksCallback {

        void onTasksLoaded(List<Task> taskList);

        void onDataNotAvailable();

    }

    interface GetTaskCallback {

        void onTaskLoaded(Task task);

        void onDataNotAvailable();
    }

    void getTasks(@NonNull LoadTasksCallback loadTaskCallbacks);

    void getTask(@NonNull String taskId,@NonNull GetTaskCallback callback);

    void saveTask(@NonNull Task task);

    void completeTask(Task task);

    void completeTask(@NonNull String taskId);

    void activateTask(@NonNull Task task);

    void activateTask(@NonNull String taskId);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);

}
