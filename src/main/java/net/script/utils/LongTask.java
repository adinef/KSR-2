package net.script.utils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class LongTask extends Service<Boolean> {

    private final RunnableWithException runnable;

    public LongTask(RunnableWithException runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        };
    }
}
