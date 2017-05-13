package dev.gautam.todoapp.taskdetail;

import dev.gautam.todoapp.BasePresenter;
import dev.gautam.todoapp.BaseView;

/**
 * Created by gautam on 13/05/17.
 */

public interface TaskDetailsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showMissingTask();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showCompletionStatus(boolean complete);

        void showEditTask(String taskId);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();

    }

    interface Presenter extends BasePresenter {
        void editTask();

        void deleteTask();

        void completeTask();

        void activateTask();
    }
}
