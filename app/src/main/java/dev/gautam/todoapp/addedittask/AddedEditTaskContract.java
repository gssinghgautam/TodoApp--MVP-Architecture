package dev.gautam.todoapp.addedittask;

import dev.gautam.todoapp.BasePresenter;
import dev.gautam.todoapp.BaseView;

/**
 * Created by gautam on 13/05/17.
 */

public interface AddedEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTaskLists();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();
    }
}
