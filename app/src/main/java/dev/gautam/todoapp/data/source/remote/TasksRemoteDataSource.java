package dev.gautam.todoapp.data.source.remote;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.gautam.todoapp.data.Task;
import dev.gautam.todoapp.data.source.TasksDataSource;

/**
 * Created by gautam on 13/05/17.
 */

public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;
    private static final int SERVICE_LATENCY_IN_MILLS = 5000;
    private static final Map<String, Task> TASK_SERVICE_DATA;

    static {
        TASK_SERVICE_DATA = new LinkedHashMap<>();
        //addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        //addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASK_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    //Prevent Direct Instantiation
    private TasksRemoteDataSource() {

    }


    @Override
    public void getTasks(final @NonNull LoadTasksCallback callback) {
        //Simulate Network call by delaying the execution
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTasksLoaded(Lists.<Task>newArrayList(TASK_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLS);
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetTaskCallback callback) {
        final Task task = TASK_SERVICE_DATA.get(taskId);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTaskLoaded(task);
            }
        }, SERVICE_LATENCY_IN_MILLS);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASK_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(Task task) {
        Task taskCompleted = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASK_SERVICE_DATA.put(task.getId(), taskCompleted);
    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activateTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASK_SERVICE_DATA.put(task.getId(), activateTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        //Not required
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
        //Not Required
    }

    @Override
    public void deleteAllTasks() {
        TASK_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASK_SERVICE_DATA.remove(taskId);
    }
}
