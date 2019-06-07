package net.script.utils.tasking;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import net.script.utils.functional.RunnableWithException;

@Slf4j
public class LongTask extends Service<Boolean> {

    private final RunnableWithException runnable;

    public LongTask(RunnableWithException runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        };
    }
}
