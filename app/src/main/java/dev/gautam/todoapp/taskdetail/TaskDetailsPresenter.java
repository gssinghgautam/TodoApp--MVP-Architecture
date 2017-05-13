package dev.gautam.todoapp.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import dev.gautam.todoapp.data.Task;
import dev.gautam.todoapp.data.source.TasksDataSource;
import dev.gautam.todoapp.data.source.TasksRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by gautam on 13/05/17.
 */

public class TaskDetailsPresenter implements TaskDetailsContract.Presenter {

    private final TasksRepository mTasksRepository;
    private final TaskDetailsContract.View mTaskDetailsView;
    private String mTaskId;

    public TaskDetailsPresenter(@Nullable String taskId, @NonNull TasksRepository tasksRepository, @NonNull TaskDetailsContract.View taskDetailsView) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository, "Task Repository cannot be null");
        mTaskDetailsView = checkNotNull(taskDetailsView, "Task Details View Cannot be null");
        mTaskDetailsView.setPresenter(this);
    }

    @Override
    public void start() {
        openTask();
    }

    private void openTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailsView.showMissingTask();
            return;
        }
        mTaskDetailsView.setLoadingIndicator(true);
        mTasksRepository.getTask(mTaskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                //The view may not able to handle UI refresh any more
                if (!mTaskDetailsView.isActive()) {
                    return;
                }
                mTaskDetailsView.setLoadingIndicator(false);
                if (null == task) {
                    mTaskDetailsView.showMissingTask();
                } else {
                    showTask(task);
                }
            }

            @Override
            public void onDataNotAvailable() {
                //The view may not able to handle UI refresh any more
                if (!mTaskDetailsView.isActive()) {
                    return;
                }
                mTaskDetailsView.showMissingTask();
            }
        });
    }

    @Override
    public void editTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailsView.showMissingTask();
            return;
        }
        mTaskDetailsView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailsView.showMissingTask();
            return;
        }
        mTasksRepository.deleteTask(mTaskId);
        mTaskDetailsView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailsView.showMissingTask();
            return;
        }
        mTasksRepository.completeTask(mTaskId);
        mTaskDetailsView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailsView.showMissingTask();
            return;
        }
        mTasksRepository.activateTask(mTaskId);
        mTaskDetailsView.showTaskMarkedActive();
    }

    private void showTask(@NonNull Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailsView.hideTitle();
        } else {
            mTaskDetailsView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailsView.hideDescription();
        } else {
            mTaskDetailsView.showDescription(description);
        }
        mTaskDetailsView.showCompletionStatus(task.isCompleted());
    }
}
