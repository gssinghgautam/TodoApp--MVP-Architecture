package dev.gautam.todoapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.gautam.todoapp.data.source.TasksDataSource;

/**
 * Created by gautam on 13/05/17.
 */

public class FakeTasksRemoteDataSource implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;
    private static final Map<String, Task> TASK_SERVICE_DATA = new LinkedHashMap<>();

    //Prevent from direct Instantiation
    private FakeTasksRemoteDataSource() {

    }

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        callback.onTasksLoaded(Lists.<Task>newArrayList(TASK_SERVICE_DATA.values()));
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        Task task = TASK_SERVICE_DATA.get(taskId);
        callback.onTaskLoaded(task);

    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASK_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(Task task) {
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASK_SERVICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        //Not Required
    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activatedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), false);
        TASK_SERVICE_DATA.put(task.getId(), activatedTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        //Not Required
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASK_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASK_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        TASK_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(Task... tasks) {
        for (Task task : tasks) {
            TASK_SERVICE_DATA.put(task.getId(), task);
        }
    }
}
