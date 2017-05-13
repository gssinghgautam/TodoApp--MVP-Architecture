package dev.gautam.todoapp.task;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dev.gautam.todoapp.addedittask.AddEditTaskActivity;
import dev.gautam.todoapp.data.Task;
import dev.gautam.todoapp.data.source.TasksDataSource;
import dev.gautam.todoapp.data.source.TasksRepository;
import dev.gautam.todoapp.utils.EspressoIdlingResource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by gautam on 13/05/17.
 */

public class TaskPresenter implements TaskContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TaskContract.View mTaskContractView;

    private TaskFilterType mCurrentFilteringType = TaskFilterType.ALL_TASKS;

    private boolean mFirstLoaded = true;

    public TaskPresenter(@NonNull TasksRepository tasksRepository, @NonNull TaskContract.View taskView) {
        mTasksRepository = checkNotNull(tasksRepository, "Task Repository Cannot be null");
        mTaskContractView = checkNotNull(taskView, "Task View Cannot be null");

        mTaskContractView.setPresenter(this);

    }


    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
// If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            mTaskContractView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoaded, true);
        mFirstLoaded = false;
    }

    @Override
    public void addNewTask() {
        mTaskContractView.showAddTask();
    }

    /**
     * @param forceUpdate   Pass true to refresh data
     * @param showLoadingUi Pass true to show loading indicator
     */

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUi) {
        if (showLoadingUi) {
            mTaskContractView.setLoadingIndicator(true);
        }

        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment();//App is little busy

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> taskList) {
                List<Task> taskToShowList = new ArrayList<Task>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();
                }

                //We will filter the task based on request type
                for (Task task : taskList) {
                    switch (mCurrentFilteringType) {
                        case ALL_TASKS:
                            taskToShowList.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                taskToShowList.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if (task.isCompleted()) {
                                taskToShowList.add(task);
                            }
                            break;
                        default:
                            taskToShowList.add(task);
                            break;
                    }
                }
                processTasks(taskToShowList);
                // The view may not be able to handle UI updates anymore
                if (!mTaskContractView.isActive()) {
                    return;
                }
                if (showLoadingUi) {
                    mTaskContractView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onDataNotAvailable() {
                if (!mTaskContractView.isActive()) {
                    return;
                }
                mTaskContractView.showLoadingTaskError();
            }
        });
    }

    private void processTasks(List<Task> taskList) {

        if (taskList.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            //Show the list of tasks
            mTaskContractView.showTasks(taskList);
            //Set filter label's text
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFilteringType) {
            case ALL_TASKS:
                mTaskContractView.showAllFilterLabel();
                break;
            case ACTIVE_TASKS:
                mTaskContractView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTaskContractView.showCompletedFilterLabel();
                break;
            default:
                mTaskContractView.showAllFilterLabel();
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFilteringType) {
            case ALL_TASKS:
                mTaskContractView.showNoTasks();
                break;
            case ACTIVE_TASKS:
                mTaskContractView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTaskContractView.showNoCompletedTasks();
                break;
            default:
                mTaskContractView.showNoTasks();
                break;
        }
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "Requested Task cannot be null");
        mTaskContractView.showTaskDetailsUi(requestedTask.getId());

    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "Completed Task cannot be null");
        mTasksRepository.completeTask(completedTask);
        mTaskContractView.showTaskMarkedCompleted();
        loadTasks(false, false);

    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "Active task cannot be null");
        mTasksRepository.activateTask(activeTask);
        mTaskContractView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTask() {
        mTasksRepository.clearCompletedTasks();
        mTaskContractView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(TaskFilterType taskFilterType) {
        mCurrentFilteringType = taskFilterType;
    }

    @Override
    public TaskFilterType getTaskFilterType() {
        return mCurrentFilteringType;
    }
}
