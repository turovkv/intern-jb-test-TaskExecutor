package ru.turovkv.java.executor;

import java.util.Collection;
import java.util.function.Function;

public class TaskImpl implements Task {
    static class WrappedInt {
        public Integer value;

        public WrappedInt(Integer value) {
            this.value = value;
        }
    }

    private final WrappedInt wrappedValue;
    private final Function<Integer, Integer> transform;
    private final Collection<Task> dependenciesList;

    public TaskImpl(WrappedInt wrappedValue,
                    Function<Integer, Integer> transform,
                    Collection<Task> dependenciesList) {
        this.wrappedValue = wrappedValue;
        this.transform = transform;
        this.dependenciesList = dependenciesList;
    }

    public Integer getValue() {
        return wrappedValue.value;
    }

    @Override
    public void execute() {
        System.out.println(wrappedValue.value + " to " + transform.apply(wrappedValue.value));
        wrappedValue.value = transform.apply(wrappedValue.value);
    }

    @Override
    public Collection<Task> dependencies() {
        return dependenciesList;
    }
}
