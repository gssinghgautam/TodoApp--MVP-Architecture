package dev.gautam.todoapp.task;

import android.support.annotation.NonNull;

import java.util.List;

import dev.gautam.todoapp.BasePresenter;
import dev.gautam.todoapp.BaseView;
import dev.gautam.todoapp.data.Task;

/**
 * Created by gautam on 13/05/17.
 */

public interface TaskContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> taskList);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedCompleted();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTaskError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestedTask);

        void completeTask(@NonNull Task completedTask);

        void activateTask(@NonNull Task activeTask);

        void clearCompletedTask();

        void setFiltering(TaskFilterType taskFilterType);

        TaskFilterType getTaskFilterType();

    }
}
