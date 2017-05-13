package dev.gautam.todoapp.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.gautam.todoapp.R;
import dev.gautam.todoapp.addedittask.AddEditTaskActivity;
import dev.gautam.todoapp.data.Task;
import dev.gautam.todoapp.taskdetail.TaskDetailsActivity;
import dev.gautam.todoapp.utils.ActivityUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by gautam on 13/05/17.
 */

public class TasksFragment extends Fragment implements TaskContract.View {

    private TaskContract.Presenter mTaskPresenter;

    private TaskAdapter mListAdapter;

    private View mNoTasksView;

    private ImageView mNoTaskIcon;

    private TextView mNoTasksMainView;

    private TextView mNoTaskAddView;

    private LinearLayout mTasksView;

    public TasksFragment() {
        //Default constructor required
    }

    private TextView mFilteringLabelView;

    public static TasksFragment newInstance() {
        Bundle args = new Bundle();
        TasksFragment fragment = new TasksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TaskAdapter(new ArrayList<Task>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTaskPresenter.start();
    }

    @Override
    public void setPresenter(TaskContract.Presenter presenter) {
        mTaskPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tasks_list);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        mFilteringLabelView = (TextView) rootView.findViewById(R.id.filteringLabel);
        mTasksView = (LinearLayout) rootView.findViewById(R.id.tasksLL);

        //setup no task views
        mNoTasksView = rootView.findViewById(R.id.noTasks);
        mNoTaskIcon = (ImageView) rootView.findViewById(R.id.noTasksIcon);
        mNoTasksMainView = (TextView) rootView.findViewById(R.id.noTasksMain);
        mNoTaskAddView = (TextView) rootView.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTask();
            }
        });

        //Setup floating action button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskPresenter.addNewTask();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTaskPresenter.loadTasks(false);
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_clear:
                mTaskPresenter.clearCompletedTask();
                break;
            case R.id.menu_refresh:
                mTaskPresenter.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
        //Make sure setRefreshing() is called after the layout is done with others
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTasks(List<Task> taskList) {
        mListAdapter.replaceData(taskList);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetailsUi(String taskId) {
// in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        Intent intent = new Intent(getContext(), TaskDetailsActivity.class);
        intent.putExtra(TaskDetailsActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    public void showTaskMarkedCompleted() {
        showMessage(getString(R.string.task_marked_complete));
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active));
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared));
    }

    @Override
    public void showLoadingTaskError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showNoActiveTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false
        );
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    private void showMessage(String message) {
        if (getView() != null)
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.fliter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all:
                        mTaskPresenter.setFiltering(TaskFilterType.ALL_TASKS);
                        break;
                    case R.id.active:
                        mTaskPresenter.setFiltering(TaskFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mTaskPresenter.setFiltering(TaskFilterType.COMPLETED_TASKS);
                        break;
                }
                mTaskPresenter.loadTasks(false);
                return true;
            }
        });
        popup.show();
    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            mTaskPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mTaskPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            mTaskPresenter.activateTask(activatedTask);
        }
    };

    static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private List<Task> mTasks;
        private TaskItemListener mTaskItemListener;

        public TaskAdapter(List<Task> tasks, TaskItemListener mTaskItemListener) {
            setList(tasks);
            this.mTaskItemListener = mTaskItemListener;
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> task) {
            mTasks = checkNotNull(task);
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            final Task task = mTasks.get(position);
            checkNotNull(task);
            holder.title.setText(task.getTitleForList());
            holder.complete.setChecked(task.isCompleted());

            if (task.isCompleted()) {
                ActivityUtils.setBackGround(holder.itemView.getContext(), holder.itemView, R.drawable.list_completed_touch_feedback);
            } else {
                ActivityUtils.setBackGround(holder.itemView.getContext(), holder.itemView, R.drawable.touch_feedback);
            }

            holder.complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!task.isCompleted()) {
                        mTaskItemListener.onCompleteTaskClick(task);
                    } else {
                        mTaskItemListener.onActivateTaskClick(task);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaskItemListener.onTaskClick(task);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {

            private AppCompatCheckBox complete;
            private TextView title;

            public TaskViewHolder(View itemView) {
                super(itemView);
                complete = (AppCompatCheckBox) itemView.findViewById(R.id.complete);
                title = (TextView) itemView.findViewById(R.id.title);
            }
        }
    }

    public interface TaskItemListener {

        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTasksMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTaskPresenter.result(requestCode, resultCode);
    }
}
