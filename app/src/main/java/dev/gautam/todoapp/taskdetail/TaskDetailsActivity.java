package dev.gautam.todoapp.taskdetail;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import dev.gautam.todoapp.Injection;
import dev.gautam.todoapp.R;
import dev.gautam.todoapp.utils.ActivityUtils;

public class TaskDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "task_extra_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Get the requested task id
        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        TaskDetailsFragment taskDetailsFragment = (TaskDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (taskDetailsFragment == null) {
            taskDetailsFragment = TaskDetailsFragment.newInstance(taskId);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), taskDetailsFragment, R.id.contentFrame);
        }

        new TaskDetailsPresenter(taskId, Injection.provideTaskRepository(TaskDetailsActivity.this), taskDetailsFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
