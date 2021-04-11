package ru.turovkv.java.executor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class TaskExecutor {
    private final int TREAD_NUMBER = 4;
    private final List<List<Task>> taskGroups = new ArrayList<>(); // Список групп
    private final Map<Task, Integer> taskGroupNumber = new HashMap<>(); //
    private final Set<Task> isTaskInDFS = new HashSet<>();

    public void execute(Collection<Task> tasks) {
        // распределение по группам. В одной группе задачи независимы
        for (Task task : tasks) {
            if (!taskGroupNumber.containsKey(task)) {
                dfs(task);
            }
        }

        ForkJoinPool threadPool = new ForkJoinPool(TREAD_NUMBER);
        try {
            for (List<Task> group : taskGroups) {
                // параллельное выполнение задач из одной группы
                threadPool.submit(() ->
                        group.parallelStream().forEach(Task::execute)
                ).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            threadPool.shutdown();
        }
    }

    /*
    * Обход графа зависимостей в ширину с подсчетом уровня задачи
    * можно доказать, что любые две задачи из одного уровня независимы
    * */
    private void dfs(Task task) {
        isTaskInDFS.add(task);

        //вычисление уровня(группы) задачи.
        //группа == наибольшее расстояние до задачи без зависимостей
        int groupNumber = -1;
        if (task.dependencies() != null) {
            for (Task dependTask : task.dependencies()) {
                if (isTaskInDFS.contains(dependTask)) {
                    throw new IllegalStateException("Cyclic dependence");
                }
                if (!taskGroupNumber.containsKey(dependTask)) {
                    dfs(dependTask);
                }
                groupNumber = Math.max(0, taskGroupNumber.get(dependTask));
            }
        }
        groupNumber++;

        taskGroupNumber.put(task, groupNumber);

        if (taskGroups.size() <= groupNumber) {
            taskGroups.add(new ArrayList<>());
        }
        taskGroups.get(groupNumber).add(task);

        isTaskInDFS.remove(task);
    }
}
