package ru.turovkv.java.executor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class Tests {
    @Test
    public void testNull() {
        TaskImpl.WrappedInt wrappedValue1 = new TaskImpl.WrappedInt(0);
        TaskImpl.WrappedInt wrappedValue2 = new TaskImpl.WrappedInt(0);

        TaskImpl task1 = new TaskImpl(wrappedValue1, x -> x + 2, null);
        List<Task> list = new ArrayList<>();
        list.add(null);
        TaskImpl task2 = new TaskImpl(wrappedValue2, x -> x * 2, list);

        TaskExecutor executor = new TaskExecutor();
        Assertions.assertThrows(NullPointerException.class, () -> executor.execute(List.of(task2, task1)));
    }

    @Test
    public void testCycle1() {
        TaskImpl.WrappedInt wrappedValue = new TaskImpl.WrappedInt(0);
        TaskImpl task1 = new TaskImpl(wrappedValue, x -> x + 2, null);
        TaskImpl task2 = new TaskImpl(wrappedValue, x -> x * 2, List.of(task1));
        task1.setDependenciesList(List.of(task2));

        TaskExecutor executor = new TaskExecutor();
        Assertions.assertThrows(IllegalStateException.class, () -> executor.execute(List.of(task2, task1)));
    }

    @Test
    public void testCycle2() {
        TaskImpl.WrappedInt wrappedValue = new TaskImpl.WrappedInt(0);
        TaskImpl task1 = new TaskImpl(wrappedValue, x -> x + 2, null);
        TaskImpl task2 = new TaskImpl(wrappedValue, x -> x * 2, List.of(task1));
        TaskImpl task3 = new TaskImpl(wrappedValue, x -> x * 2, List.of(task2));
        TaskImpl task4 = new TaskImpl(wrappedValue, x -> x * 2, List.of(task3));
        task1.setDependenciesList(List.of(task4));

        TaskExecutor executor = new TaskExecutor();
        Assertions.assertThrows(IllegalStateException.class, () -> executor.execute(List.of(task2, task1, task4, task3)));
    }

    @Test
    public void testSimple1() {
        TaskImpl.WrappedInt wrappedValue = new TaskImpl.WrappedInt(0);
        Task task1 = new TaskImpl(wrappedValue, x -> x + 2, null);
        Task task2 = new TaskImpl(wrappedValue, x -> x * 2, List.of(task1));

        TaskExecutor executor = new TaskExecutor();
        executor.execute(List.of(task2, task1));
        Assertions.assertEquals(4, wrappedValue.value);
    }

    @Test
    public void testSimple2() {
        TaskImpl.WrappedInt wrappedValue = new TaskImpl.WrappedInt(0);
        Task task1 = new TaskImpl(wrappedValue, x -> x * 2, null);
        Task task2 = new TaskImpl(wrappedValue, x -> x + 2, List.of(task1));

        TaskExecutor executor = new TaskExecutor();
        executor.execute(List.of(task1, task2));
        Assertions.assertEquals(2, wrappedValue.value);
    }

    @Test
    public void testSimple3() {
        TaskImpl.WrappedInt wrappedValue = new TaskImpl.WrappedInt(0);
        Task task1 = new TaskImpl(wrappedValue, x -> x + 1, null);
        Task task2 = new TaskImpl(wrappedValue, x -> x * 3, List.of(task1));
        Task task3 = new TaskImpl(wrappedValue, x -> x - 1, List.of(task1, task2));

        TaskExecutor executor = new TaskExecutor();
        executor.execute(List.of(task1, task3, task2));
        Assertions.assertEquals(2, wrappedValue.value);
    }
}
