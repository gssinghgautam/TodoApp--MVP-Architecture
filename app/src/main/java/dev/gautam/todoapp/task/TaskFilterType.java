package dev.gautam.todoapp.task;

/**
 * Created by gautam on 13/05/17.
 */

public enum TaskFilterType {

    /**
     * Do Not Filter Task
     */
    ALL_TASKS,

    /**
     * Filter only active tasks(Not completed yet)
     */
    ACTIVE_TASKS,

    /**
     * Filter only completed task
     */
    COMPLETED_TASKS

}
