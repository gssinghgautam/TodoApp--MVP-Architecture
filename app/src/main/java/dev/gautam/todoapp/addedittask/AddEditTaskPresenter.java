package dev.gautam.todoapp.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dev.gautam.todoapp.data.Task;
import dev.gautam.todoapp.data.source.TasksDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by gautam on 13/05/17.
 */

public class AddEditTaskPresenter implements AddedEditTaskContract.Presenter, TasksDataSource.GetTaskCallback {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AddedEditTaskContract.View mAddTaskView;

    @Nullable
    private String mTaskId;

    private boolean mIsDataMissing;

    public AddEditTaskPresenter(String taskId, TasksDataSource taskDataRepository, AddedEditTaskContract.View addedTaskView, boolean shouldLoadDataFromRepo) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(taskDataRepository, "Task Data source cannot be null");
        mAddTaskView = checkNotNull(addedTaskView, "Task Data View cannot be null");
        mIsDataMissing = shouldLoadDataFromRepo;

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public void onTaskLoaded(Task task) {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTaskLists();
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTasksRepository.saveTask(new Task(title, description, mTaskId));
        mAddTaskView.showTaskLists(); // After an edit, go back to the list.
    }
}
