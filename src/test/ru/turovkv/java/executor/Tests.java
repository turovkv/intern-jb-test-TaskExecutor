package ru.turovkv.java.executor;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {
    @Test
    public void testSimple() {
        TaskImpl.WrappedInt wrappedValue = new TaskImpl.WrappedInt(0);
        Task task1 = new TaskImpl(wrappedValue, x -> x + 2, null);
        Task task2 = new TaskImpl(wrappedValue, x -> x * 2, List.of(task1));

        TaskExecutor executor = new TaskExecutor();
        executor.execute(List.of(task2, task1));
        Assertions.assertEquals(4, wrappedValue.value);
    }
}
