package dev.gautam.todoapp;

/**
 * Created by gautam on 13/05/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;

import dev.gautam.todoapp.data.source.TasksRepository;
import dev.gautam.todoapp.data.source.local.TasksLocalDataSource;
import dev.gautam.todoapp.data.source.remote.TasksRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link TasksDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static TasksRepository provideTaskRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksLocalDataSource.getInstance(context));
    }
}
